package com.edu.froliak.controller;

import com.edu.froliak.dto.ChatRoomCreateDTO;
import com.edu.froliak.dto.ChatRoomDTO;
import com.edu.froliak.dto.ChatRoomUpdateDTO;
import com.edu.froliak.model.ChatRoom;
import com.edu.froliak.service.ChatRoomService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/chatrooms")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @Autowired
    public ChatRoomController(ChatRoomService chatRoomService) {
        this.chatRoomService = chatRoomService;
    }

    @PostMapping
    public ResponseEntity<ChatRoomDTO> createChatRoom(@Valid @RequestBody ChatRoomCreateDTO createDTO) {
        ChatRoom createdRoomEntity = chatRoomService.createChatRoom(createDTO);
        ChatRoomDTO roomDTO = chatRoomService.getChatRoomDTOById(createdRoomEntity.getId());
        return new ResponseEntity<>(roomDTO, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ChatRoomDTO>> getAllChatRooms() {
        List<ChatRoom> roomEntities = chatRoomService.getAllChatRoomsEntities();
        List<ChatRoomDTO> roomDTOs = roomEntities.stream()
                .map(room -> chatRoomService.getChatRoomDTOById(room.getId()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(roomDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChatRoomDTO> getChatRoomById(@PathVariable Long id) {
        ChatRoomDTO roomDTO = chatRoomService.getChatRoomDTOById(id);
        return ResponseEntity.ok(roomDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ChatRoomDTO> updateChatRoom(@PathVariable Long id, @Valid @RequestBody ChatRoomUpdateDTO updateDTO) {
        ChatRoom updatedRoomEntity = chatRoomService.updateChatRoom(id, updateDTO);
        ChatRoomDTO roomDTO = chatRoomService.getChatRoomDTOById(updatedRoomEntity.getId());
        return ResponseEntity.ok(roomDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChatRoom(@PathVariable Long id) {
        chatRoomService.deleteChatRoom(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{chatRoomId}/users")
    public ResponseEntity<ChatRoomDTO> addUserToChatRoom(@PathVariable Long chatRoomId, @RequestBody Map<String, Long> payload) {
        Long userId = payload.get("userId");
        if (userId == null) {
            throw new IllegalArgumentException("userId є обов'язковим");
        }
        chatRoomService.addUserToChatRoom(chatRoomId, userId);
        ChatRoomDTO roomDTO = chatRoomService.getChatRoomDTOById(chatRoomId);
        return ResponseEntity.ok(roomDTO);
    }

    @DeleteMapping("/{chatRoomId}/users/{userId}")
    public ResponseEntity<ChatRoomDTO> removeUserFromChatRoom(@PathVariable Long chatRoomId, @PathVariable Long userId) {
        chatRoomService.removeUserFromChatRoom(chatRoomId, userId);
        ChatRoomDTO roomDTO = chatRoomService.getChatRoomDTOById(chatRoomId);
        return ResponseEntity.ok(roomDTO);
    }

    @GetMapping("/{roomId}/users/{userId}/membership")
    public ResponseEntity<Void> checkUserMembership(@PathVariable Long roomId, @PathVariable Long userId) {
        boolean isMember = chatRoomService.isUserMember(roomId, userId);
        if (isMember) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}