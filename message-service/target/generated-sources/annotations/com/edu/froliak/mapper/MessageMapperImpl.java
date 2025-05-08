package com.edu.froliak.mapper;

import com.edu.froliak.dto.MessageCreateDTO;
import com.edu.froliak.dto.MessageDTO;
import com.edu.froliak.dto.MessageUpdateDTO;
import com.edu.froliak.model.Message;
import com.edu.froliak.service.MessageService;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-05-07T12:09:42+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.14 (Amazon.com Inc.)"
)
@Component
public class MessageMapperImpl implements MessageMapper {

    @Override
    public Message toMessage(MessageCreateDTO createDTO) {
        if ( createDTO == null ) {
            return null;
        }

        Message message = new Message();

        message.setContent( createDTO.getContent() );
        message.setSenderId( createDTO.getSenderId() );
        message.setChatRoomId( createDTO.getChatRoomId() );

        return message;
    }

    @Override
    public MessageDTO toMessageDTO(Message message, MessageService messageService) {
        if ( message == null ) {
            return null;
        }

        MessageDTO messageDTO = new MessageDTO();

        messageDTO.setSender( mapSenderDetails( message, messageService ) );
        messageDTO.setId( message.getId() );
        messageDTO.setContent( message.getContent() );
        messageDTO.setTimestamp( message.getTimestamp() );
        messageDTO.setChatRoomId( message.getChatRoomId() );

        return messageDTO;
    }

    @Override
    public List<MessageDTO> toMessageDTOList(List<Message> messages, MessageService messageService) {
        if ( messages == null ) {
            return null;
        }

        List<MessageDTO> list = new ArrayList<MessageDTO>( messages.size() );
        for ( Message message : messages ) {
            list.add( toMessageDTO( message, messageService ) );
        }

        return list;
    }

    @Override
    public void updateMessageFromDto(MessageUpdateDTO updateDTO, Message message) {
        if ( updateDTO == null ) {
            return;
        }

        if ( updateDTO.getContent() != null ) {
            message.setContent( updateDTO.getContent() );
        }
    }
}
