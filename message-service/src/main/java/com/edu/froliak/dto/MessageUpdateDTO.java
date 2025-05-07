package com.edu.froliak.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
@Data
public class MessageUpdateDTO {
    @NotBlank(message = "Вміст повідомлення не може бути порожнім")
    @Size(max = 1000, message = "Повідомлення не може перевищувати 1000 символів")
    private String content;
}