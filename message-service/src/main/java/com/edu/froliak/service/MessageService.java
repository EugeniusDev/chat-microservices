package com.edu.froliak.service;

import com.edu.froliak.dto.MessageCreateDTO;
import com.edu.froliak.dto.MessageUpdateDTO;
import com.edu.froliak.dto.UserBasicDTO;
import com.edu.froliak.exception.ResourceNotFoundException;
import com.edu.froliak.mapper.MessageMapper;
import com.edu.froliak.model.Message;
import com.edu.froliak.repository.MessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class MessageService {

    private static final Logger log = LoggerFactory.getLogger(MessageService.class);

    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;
    private final RestTemplate restTemplate;

    @Value("${user.service.url}")
    private String userServiceUrl;

    @Value("${chatroom.service.url}")
    private String chatroomServiceUrl;

    private static final UserBasicDTO UNKNOWN_USER_DTO = new UserBasicDTO(-1L, "Невідомий");
    private static final UserBasicDTO ERROR_USER_DTO = new UserBasicDTO(-2L, "Помилка");


    @Autowired
    public MessageService(MessageRepository messageRepository, MessageMapper messageMapper, RestTemplate restTemplate) {
        this.messageRepository = messageRepository;
        this.messageMapper = messageMapper;
        this.restTemplate = restTemplate;
    }

    @Transactional
    public Message sendMessage(MessageCreateDTO messageCreateDTO) {
        Long senderId = messageCreateDTO.getSenderId();
        Long chatRoomId = messageCreateDTO.getChatRoomId();

        checkUserExists(senderId);
        checkChatRoomExists(chatRoomId);
        checkUserMembership(senderId, chatRoomId);

        Message message = messageMapper.toMessage(messageCreateDTO);
        Message savedMessage = messageRepository.save(message);
        log.info("Надіслано повідомлення ID {} в чат ID {} від користувача ID {}",
                savedMessage.getId(), chatRoomId, senderId);
        return savedMessage;
    }

    @Transactional(readOnly = true)
    public List<Message> getMessagesByChatRoom(Long chatRoomId) {
        checkChatRoomExists(chatRoomId);
        return messageRepository.findByChatRoomIdOrderByTimestampAsc(chatRoomId);
    }

    @Transactional(readOnly = true)
    public Message getMessageById(Long messageId) {
        return messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Повідомлення з ID " + messageId + " не знайдено."));
    }

    @Transactional
    public Message updateMessage(Long messageId, MessageUpdateDTO messageUpdateDTO) {
        Message existingMessage = getMessageById(messageId);

        // todo перевірка прав на редагування (порівняти existingMessage.getSenderId() з ID поточного користувача)

        messageMapper.updateMessageFromDto(messageUpdateDTO, existingMessage);
        Message updatedMessage = messageRepository.save(existingMessage);
        log.info("Оновлено повідомлення з ID: {}", updatedMessage.getId());
        return updatedMessage;
    }

    @Transactional
    public void deleteMessage(Long messageId) {
        Message existingMessage = getMessageById(messageId);
        //todo перевірка прав на видалення

        log.warn("Видалення повідомлення з ID: {}", messageId);
        messageRepository.delete(existingMessage);
    }

    public UserBasicDTO getSenderDetails(Long senderId) {
        if (senderId == null) return UNKNOWN_USER_DTO;
        try {
            String url = userServiceUrl + "/" + senderId;
            ResponseEntity<UserBasicDTO> response = restTemplate.getForEntity(url, UserBasicDTO.class);
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody();
            } else {
                log.warn("Не вдалося отримати UserBasicDTO для userId {}. Статус: {}", senderId, response.getStatusCode());
                return UNKNOWN_USER_DTO;
            }
        } catch (HttpClientErrorException.NotFound ex) {
            log.warn("Користувач з ID {} не знайдений в user-service.", senderId);
            return UNKNOWN_USER_DTO;
        } catch (RestClientException ex) {
            log.error("Помилка RestTemplate при отриманні деталей користувача {}: {}", senderId, ex.getMessage());
            return ERROR_USER_DTO;
        }
    }

    private void checkUserExists(Long userId) {
        try {
            String url = userServiceUrl + "/" + userId;
            restTemplate.headForHeaders(url);
            log.debug("Перевірка існування користувача {}: OK", userId);
        } catch (HttpClientErrorException.NotFound ex) {
            log.warn("Перевірка існування користувача {}: НЕ ЗНАЙДЕНО", userId);
            throw new ResourceNotFoundException("Відправника з ID " + userId + " не знайдено.");
        } catch (RestClientException ex) {
            log.error("Помилка RestTemplate під час перевірки користувача {}: {}", userId, ex.getMessage());
            throw new ResourceNotFoundException("Сервіс користувачів недоступний для перевірки ID " + userId);
        }
    }

    private void checkChatRoomExists(Long chatRoomId) {
        try {
            String url = chatroomServiceUrl + "/" + chatRoomId;
            restTemplate.headForHeaders(url);
            log.debug("Перевірка існування кімнати {}: OK", chatRoomId);
        } catch (HttpClientErrorException.NotFound ex) {
            log.warn("Перевірка існування кімнати {}: НЕ ЗНАЙДЕНО", chatRoomId);
            throw new ResourceNotFoundException("Чат-кімнату з ID " + chatRoomId + " не знайдено.");
        } catch (RestClientException ex) {
            log.error("Помилка RestTemplate під час перевірки кімнати {}: {}", chatRoomId, ex.getMessage());
            throw new ResourceNotFoundException("Сервіс чат-кімнат недоступний для перевірки ID " + chatRoomId);
        }
    }

    private void checkUserMembership(Long userId, Long chatRoomId) {
        try {
            String url = chatroomServiceUrl + "/" + chatRoomId + "/users/" + userId + "/membership";
            restTemplate.getForEntity(url, Void.class);
            log.debug("Перевірка членства користувача {} в кімнаті {}: УЧАСНИК", userId, chatRoomId);
        } catch (HttpClientErrorException.NotFound ex) {
            log.warn("Перевірка членства користувача {} в кімнаті {}: НЕ УЧАСНИК", userId, chatRoomId);
            throw new SecurityException("Користувач ID " + userId + " не є учасником чату ID " + chatRoomId);
        } catch (RestClientException ex) {
            log.error("Помилка RestTemplate під час перевірки членства користувача {} в кімнаті {}: {}", userId, chatRoomId, ex.getMessage());
            throw new ResourceNotFoundException("Сервіс чат-кімнат недоступний для перевірки членства.");
        }
    }
}