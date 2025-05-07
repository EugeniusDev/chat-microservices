package com.edu.froliak.dto;

import lombok.Data;
import java.time.LocalDateTime;
@Data
public class MessageDTO {
    private Long id;
    private String content;
    private LocalDateTime timestamp;
    private UserBasicDTO sender;
    private Long chatRoomId;
}