package com.edu.froliak.controller;

import com.edu.froliak.dto.MessageCreateDTO;
import com.edu.froliak.dto.MessageDTO;
import com.edu.froliak.dto.MessageUpdateDTO;
import com.edu.froliak.mapper.MessageMapper;
import com.edu.froliak.model.Message;
import com.edu.froliak.service.MessageService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
public class MessageController {

    private final MessageService messageService;
    private final MessageMapper messageMapper;

    @Autowired
    public MessageController(MessageService messageService, MessageMapper messageMapper) {
        this.messageService = messageService;
        this.messageMapper = messageMapper;
    }

    @PostMapping("/messages")
    public ResponseEntity<MessageDTO> sendMessage(@Valid @RequestBody MessageCreateDTO createDTO) {
        Message savedMessage = messageService.sendMessage(createDTO);
        MessageDTO messageDTO = messageMapper.toMessageDTO(savedMessage, messageService);
        return new ResponseEntity<>(messageDTO, HttpStatus.CREATED);
    }

    @GetMapping("/chatrooms/{chatRoomId}/messages")
    public ResponseEntity<List<MessageDTO>> getMessagesByChatRoom(@PathVariable Long chatRoomId) {
        List<Message> messages = messageService.getMessagesByChatRoom(chatRoomId);
        List<MessageDTO> messageDTOs = messages.stream()
                .map(msg -> messageMapper.toMessageDTO(msg, messageService))
                .collect(Collectors.toList());
        return ResponseEntity.ok(messageDTOs);
    }

    @GetMapping("/messages/{id}")
    public ResponseEntity<MessageDTO> getMessageById(@PathVariable Long id) {
        Message message = messageService.getMessageById(id);
        MessageDTO messageDTO = messageMapper.toMessageDTO(message, messageService);
        return ResponseEntity.ok(messageDTO);
    }

    @PutMapping("/messages/{id}")
    public ResponseEntity<MessageDTO> updateMessage(@PathVariable Long id, @Valid @RequestBody MessageUpdateDTO updateDTO) {
        Message updatedMessage = messageService.updateMessage(id, updateDTO);
        MessageDTO messageDTO = messageMapper.toMessageDTO(updatedMessage, messageService);
        return ResponseEntity.ok(messageDTO);
    }

    @DeleteMapping("/messages/{id}")
    public ResponseEntity<Void> deleteMessage(@PathVariable Long id) {
        messageService.deleteMessage(id);
        return ResponseEntity.noContent().build();
    }
}