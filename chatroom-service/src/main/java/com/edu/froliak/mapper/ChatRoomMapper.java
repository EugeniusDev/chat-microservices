package com.edu.froliak.mapper;

import com.edu.froliak.dto.ChatRoomBasicDTO;
import com.edu.froliak.dto.ChatRoomCreateDTO;
import com.edu.froliak.dto.ChatRoomDTO;
import com.edu.froliak.dto.ChatRoomUpdateDTO;
import com.edu.froliak.model.ChatRoom;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ChatRoomMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "users", ignore = true)
    ChatRoomDTO toChatRoomDTO(ChatRoom chatRoom);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    ChatRoomBasicDTO toChatRoomBasicDTO(ChatRoom chatRoom);

    default List<ChatRoomDTO> toChatRoomDTOList(List<ChatRoom> chatRooms) {
        if (chatRooms == null) {
            return Collections.emptyList();
        }
        return chatRooms.stream()
                .map(this::toChatRoomDTO)
                .collect(Collectors.toList());
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "name")
    @Mapping(target = "userIds", ignore = true)
    ChatRoom toChatRoom(ChatRoomCreateDTO chatRoomCreateDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "name")
    @Mapping(target = "userIds", ignore = true)
    void updateChatRoomFromDto(ChatRoomUpdateDTO chatRoomUpdateDTO, @MappingTarget ChatRoom chatRoom);
}