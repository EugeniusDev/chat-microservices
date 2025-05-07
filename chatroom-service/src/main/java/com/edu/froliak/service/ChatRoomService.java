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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ChatRoomService {
    private static final Logger log = LoggerFactory.getLogger(ChatRoomService.class);

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMapper chatRoomMapper;
    private final RestTemplate restTemplate;

    @Value("${user.service.url}")
    private String userServiceUrl;

    @Autowired
    public ChatRoomService(ChatRoomRepository chatRoomRepository, ChatRoomMapper chatRoomMapper, RestTemplate restTemplate) {
        this.chatRoomRepository = chatRoomRepository;
        this.chatRoomMapper = chatRoomMapper;
        this.restTemplate = restTemplate;
    }

    @Transactional
    public ChatRoom createChatRoom(ChatRoomCreateDTO createDTO) {
        ChatRoom chatRoom = chatRoomMapper.toChatRoom(createDTO);
        // chatRoom.setUserIds(new HashSet<>());
        return chatRoomRepository.save(chatRoom);
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
            Set<UserBasicDTO> users = room.getUserIds().stream()
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
        return chatRoomRepository.save(existingRoom);
    }

    @Transactional
    public void deleteChatRoom(Long roomId) {
        if (!chatRoomRepository.existsById(roomId)) {
            throw new ResourceNotFoundException("Чат-кімнату з ID " + roomId + " не знайдено для видалення.");
        }
        chatRoomRepository.deleteById(roomId);
    }

    @Transactional
    public ChatRoom addUserToChatRoom(Long roomId, Long userId) {
        ChatRoom room = getChatRoomEntityById(roomId);
        if (!checkUserExists(userId)) {
            throw new ResourceNotFoundException("Користувача з ID " + userId + " не знайдено.");
        }
        if (room.getUserIds().contains(userId)) {
            log.warn("Користувач ID {} вже в кімнаті ID {}", userId, roomId);
            return room;
        }
        room.getUserIds().add(userId);
        return chatRoomRepository.save(room);
    }

    @Transactional
    public ChatRoom removeUserFromChatRoom(Long roomId, Long userId) {
        ChatRoom room = getChatRoomEntityById(roomId);
        if (!checkUserExists(userId)) {
            log.warn("Спроба видалити неіснуючого користувача ID {} з кімнати ID {}", userId, roomId);
        }
        if (!room.getUserIds().contains(userId)) {
            log.warn("Користувача ID {} немає в кімнаті ID {}", userId, roomId);
            return room;
        }
        room.getUserIds().remove(userId);
        return chatRoomRepository.save(room);
    }

    @Transactional(readOnly = true)
    public boolean isUserMember(Long roomId, Long userId) {
        ChatRoom room = getChatRoomEntityById(roomId);
        return room.getUserIds().contains(userId);
    }

    private UserBasicDTO fetchUserBasicDetails(Long userId) {
        try {
            String url = userServiceUrl + "/" + userId;
            ResponseEntity<UserBasicDTO> response = restTemplate.getForEntity(url, UserBasicDTO.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            }
            log.warn("Не вдалося отримати UserBasicDTO для userId {}. Статус: {}", userId, response.getStatusCode());
        } catch (HttpClientErrorException.NotFound ex) {
            log.warn("Користувач з ID {} не знайдений в user-service.", userId);
        } catch (Exception ex) {
            log.error("Помилка при отриманні деталей користувача {} з user-service: {}", userId, ex.getMessage());
        }
        return null;
    }

    private boolean checkUserExists(Long userId) {
        try {
            String url = userServiceUrl + "/" + userId;
            restTemplate.getForEntity(url, Void.class);
            return true;
        } catch (HttpClientErrorException.NotFound ex) {
            return false;
        } catch (Exception ex) {
            log.error("Помилка перевірки існування користувача {} в user-service: {}", userId, ex.getMessage());
            return false;
        }
    }
}