package com.edu.froliak.service;

import com.edu.froliak.dto.ChatRoomCreateDTO;
import com.edu.froliak.dto.ChatRoomDTO;
import com.edu.froliak.dto.ChatRoomUpdateDTO;
import com.edu.froliak.dto.UserBasicDTO;
import com.edu.froliak.exception.ResourceNotFoundException;
import com.edu.froliak.mapper.ChatRoomMapper;
import com.edu.froliak.model.ChatRoom;
import com.edu.froliak.repository.ChatRoomRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.naming.ServiceUnavailableException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ChatRoomService {
    private static final Logger log = LoggerFactory.getLogger(ChatRoomService.class);

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMapper chatRoomMapper;
    private final RestTemplate restTemplate;

    private static final String USER_SERVICE_NAME = "user-service";

    @Autowired
    public ChatRoomService(ChatRoomRepository chatRoomRepository, ChatRoomMapper chatRoomMapper, RestTemplate restTemplate) {
        this.chatRoomRepository = chatRoomRepository;
        this.chatRoomMapper = chatRoomMapper;
        this.restTemplate = restTemplate;
    }

    @Transactional
    public ChatRoom createChatRoom(ChatRoomCreateDTO createDTO) {
        ChatRoom chatRoom = chatRoomMapper.toChatRoom(createDTO);
        chatRoom.setUserIds(new HashSet<>());
        ChatRoom savedRoom = chatRoomRepository.save(chatRoom);
        log.info("Створено чат-кімнату '{}' з ID: {}", savedRoom.getName(), savedRoom.getId());
        return savedRoom;
    }

    @Transactional(readOnly = true)
    public List<ChatRoom> getAllChatRoomsEntities() {
        return chatRoomRepository.findAll();
    }

    @Transactional(readOnly = true)
    public ChatRoomDTO getChatRoomDTOById(Long roomId) {
        ChatRoom room = getChatRoomEntityById(roomId);
        ChatRoomDTO roomDTO = chatRoomMapper.toChatRoomDTO(room);

        if (room.getUserIds() != null && !room.getUserIds().isEmpty()) {
            Set<UserBasicDTO> users = room.getUserIds().parallelStream()
                    .map(this::fetchUserBasicDetails)
                    .filter(java.util.Objects::nonNull)
                    .collect(Collectors.toSet());
            roomDTO.setUsers(users);
        } else {
            roomDTO.setUsers(Collections.emptySet());
        }
        return roomDTO;
    }

    @Transactional(readOnly = true)
    public ChatRoom getChatRoomEntityById(Long roomId) {
        return chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Чат-кімнату з ID " + roomId + " не знайдено."));
    }

    @Transactional
    public ChatRoom updateChatRoom(Long roomId, ChatRoomUpdateDTO updateDTO) {
        ChatRoom existingRoom = getChatRoomEntityById(roomId);
        chatRoomMapper.updateChatRoomFromDto(updateDTO, existingRoom);
        ChatRoom updatedRoom = chatRoomRepository.save(existingRoom);
        log.info("Оновлено чат-кімнату з ID: {}", updatedRoom.getId());
        return updatedRoom;
    }

    @Transactional
    public void deleteChatRoom(Long roomId) {
        if (!chatRoomRepository.existsById(roomId)) {
            throw new ResourceNotFoundException("Чат-кімнату з ID " + roomId + " не знайдено для видалення.");
        }
        log.warn("Видалення чат-кімнати з ID: {}", roomId);
        chatRoomRepository.deleteById(roomId);
    }

    @Transactional
    public ChatRoom addUserToChatRoom(Long roomId, Long userId) {
        ChatRoom room = getChatRoomEntityById(roomId);
        checkUserExists(userId);

        if (room.getUserIds().contains(userId)) {
            log.warn("Користувач ID {} вже в кімнаті ID {}", userId, roomId);
            return room;
        }
        boolean added = room.getUserIds().add(userId);
        if (added) {
            log.info("Додано користувача ID {} до кімнати ID {}", userId, roomId);
            return chatRoomRepository.save(room);
        }
        return room;
    }

    @Transactional
    public ChatRoom removeUserFromChatRoom(Long roomId, Long userId) {
        ChatRoom room = getChatRoomEntityById(roomId);

        if (!room.getUserIds().contains(userId)) {
            log.warn("Користувача ID {} немає в кімнаті ID {}, видалення неможливе", userId, roomId);
            return room;
        }
        boolean removed = room.getUserIds().remove(userId);
        if (removed) {
            log.info("Видалено користувача ID {} з кімнати ID {}", userId, roomId);
            return chatRoomRepository.save(room);
        }
        return room;
    }

    @Transactional(readOnly = true)
    public boolean isUserMember(Long roomId, Long userId) {
        ChatRoom room = getChatRoomEntityById(roomId);
        return room.getUserIds() != null && room.getUserIds().contains(userId);
    }

    private UserBasicDTO fetchUserBasicDetails(Long userId) {
        if (userId == null) return null;
        try {
            String url = "http://" + USER_SERVICE_NAME + "/api/v1/users/" + userId;
            ResponseEntity<UserBasicDTO> response = restTemplate.getForEntity(url, UserBasicDTO.class);
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody();
            }
            log.warn("Не вдалося отримати UserBasicDTO для userId {} з {}. Статус: {}", userId, USER_SERVICE_NAME, response.getStatusCode());
        } catch (HttpClientErrorException.NotFound ex) {
            log.warn("Користувач з ID {} не знайдений в {}.", userId, USER_SERVICE_NAME);
        } catch (RestClientException ex) {
            log.error("Помилка RestTemplate при отриманні деталей користувача {} з {}: {}", userId, USER_SERVICE_NAME, ex.getMessage());
        }
        return null;
    }

    private boolean checkUserExists(Long userId) {
        if (userId == null) return false;
        try {
            String url = "http://" + USER_SERVICE_NAME + "/api/v1/users/" + userId;
            restTemplate.headForHeaders(url);
            return true;
        } catch (HttpClientErrorException.NotFound ex) {
            return false;
        } catch (RestClientException ex) {
            log.error("Помилка RestTemplate під час перевірки користувача {} в {}: {}", userId, USER_SERVICE_NAME, ex.getMessage());
            throw new RestClientException("Сервіс користувачів (" + USER_SERVICE_NAME + ") недоступний для перевірки ID " + userId, ex);
        }
    }
}