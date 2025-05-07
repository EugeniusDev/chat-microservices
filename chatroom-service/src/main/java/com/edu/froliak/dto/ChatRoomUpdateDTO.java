package com.edu.froliak.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
@Data
public class ChatRoomUpdateDTO {
    @NotBlank(message = "Назва чату не може бути порожньою")
    @Size(max = 100, message = "Назва чату не може перевищувати 100 символів")
    private String name;
}