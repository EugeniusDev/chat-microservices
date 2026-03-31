package com.edu.froliak.service;

import com.edu.froliak.dto.ChatRoomCreateDTO;
import com.edu.froliak.mapper.ChatRoomMapper;
import com.edu.froliak.model.ChatRoom;
import com.edu.froliak.repository.ChatRoomRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
/*
  @author eugen
  @project project
  @class ChatRoomServiceTest
  @version 1.0.0
  @since 3/29/2026 - 16.07
*/

@ExtendWith(MockitoExtension.class)
class ChatRoomServiceTest {

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @Mock
    private ChatRoomMapper chatRoomMapper;

    @InjectMocks
    private ChatRoomService chatRoomService;

    @Test
    void createChatRoom_ShouldReturnSavedRoom() {
        ChatRoomCreateDTO dto = new ChatRoomCreateDTO();
        dto.setName("Test Room");
        ChatRoom room = new ChatRoom();
        room.setName("Test Room");

        when(chatRoomMapper.toChatRoom(dto)).thenReturn(room);
        when(chatRoomRepository.save(room)).thenReturn(room);

        ChatRoom result = chatRoomService.createChatRoom(dto);

        assertNotNull(result);
        assertEquals("Test Room", result.getName());
        verify(chatRoomRepository, times(1)).save(room);
    }
}