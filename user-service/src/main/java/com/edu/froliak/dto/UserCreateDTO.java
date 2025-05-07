package com.edu.froliak.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserCreateDTO {
    @NotBlank(message = "Ім'я користувача не може бути порожнім")
    @Size(min = 3, max = 50, message = "Ім'я користувача має містити від 3 до 50 символів")
    private String username;

    @NotBlank(message = "Пароль не може бути порожнім")
    @Size(min = 6, message = "Пароль має містити щонайменше 6 символів")
    private String password;
}