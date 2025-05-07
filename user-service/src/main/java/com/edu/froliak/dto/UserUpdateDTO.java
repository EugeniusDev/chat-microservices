package com.edu.froliak.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserUpdateDTO {
    @Size(min = 3, max = 50, message = "Ім'я користувача має містити від 3 до 50 символів")
    private String username;
}