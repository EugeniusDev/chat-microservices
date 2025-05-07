package com.edu.froliak.mapper;

import com.edu.froliak.dto.MessageCreateDTO;
import com.edu.froliak.dto.MessageDTO;
import com.edu.froliak.dto.MessageUpdateDTO;
import com.edu.froliak.dto.UserBasicDTO;
import com.edu.froliak.model.Message;
import com.edu.froliak.service.MessageService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Context;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface MessageMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "timestamp", ignore = true)
    Message toMessage(MessageCreateDTO createDTO);

    @Mapping(target = "sender", source = "message", qualifiedByName = "mapSenderDetails")
    MessageDTO toMessageDTO(Message message, @Context MessageService messageService);

    List<MessageDTO> toMessageDTOList(List<Message> messages, @Context MessageService messageService);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "timestamp", ignore = true)
    @Mapping(target = "senderId", ignore = true)
    @Mapping(target = "chatRoomId", ignore = true)
    void updateMessageFromDto(MessageUpdateDTO updateDTO, @MappingTarget Message message);

    @org.mapstruct.Named("mapSenderDetails")
    default UserBasicDTO mapSenderDetails(Message message, @Context MessageService messageService) {
        if (message == null || message.getSenderId() == null || messageService == null) {
            return null;
        }
        return messageService.getSenderDetails(message.getSenderId());
    }
}