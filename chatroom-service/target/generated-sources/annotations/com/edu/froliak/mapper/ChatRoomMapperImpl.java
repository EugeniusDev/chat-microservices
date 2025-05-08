package com.edu.froliak.mapper;

import com.edu.froliak.dto.ChatRoomBasicDTO;
import com.edu.froliak.dto.ChatRoomCreateDTO;
import com.edu.froliak.dto.ChatRoomDTO;
import com.edu.froliak.dto.ChatRoomUpdateDTO;
import com.edu.froliak.model.ChatRoom;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-05-08T13:48:08+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.14 (Amazon.com Inc.)"
)
@Component
public class ChatRoomMapperImpl implements ChatRoomMapper {

    @Override
    public ChatRoomDTO toChatRoomDTO(ChatRoom chatRoom) {
        if ( chatRoom == null ) {
            return null;
        }

        ChatRoomDTO chatRoomDTO = new ChatRoomDTO();

        chatRoomDTO.setId( chatRoom.getId() );
        chatRoomDTO.setName( chatRoom.getName() );

        return chatRoomDTO;
    }

    @Override
    public ChatRoomBasicDTO toChatRoomBasicDTO(ChatRoom chatRoom) {
        if ( chatRoom == null ) {
            return null;
        }

        ChatRoomBasicDTO chatRoomBasicDTO = new ChatRoomBasicDTO();

        chatRoomBasicDTO.setId( chatRoom.getId() );
        chatRoomBasicDTO.setName( chatRoom.getName() );

        return chatRoomBasicDTO;
    }

    @Override
    public ChatRoom toChatRoom(ChatRoomCreateDTO chatRoomCreateDTO) {
        if ( chatRoomCreateDTO == null ) {
            return null;
        }

        ChatRoom chatRoom = new ChatRoom();

        chatRoom.setName( chatRoomCreateDTO.getName() );

        return chatRoom;
    }

    @Override
    public void updateChatRoomFromDto(ChatRoomUpdateDTO chatRoomUpdateDTO, ChatRoom chatRoom) {
        if ( chatRoomUpdateDTO == null ) {
            return;
        }

        if ( chatRoomUpdateDTO.getName() != null ) {
            chatRoom.setName( chatRoomUpdateDTO.getName() );
        }
    }
}
