package com.edu.froliak.service;

import com.edu.froliak.dto.UserCreateDTO;
import com.edu.froliak.dto.UserUpdateDTO;
import com.edu.froliak.exception.ResourceNotFoundException;
import com.edu.froliak.mapper.UserMapper;
import com.edu.froliak.model.User;
import com.edu.froliak.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User createUser(UserCreateDTO userCreateDTO) {
        userRepository.findByUsername(userCreateDTO.getUsername()).ifPresent(u -> {
            throw new IllegalArgumentException("Користувач з іменем '" + userCreateDTO.getUsername() + "' вже існує.");
        });
        User user = userMapper.toUser(userCreateDTO);
        user.setPassword(passwordEncoder.encode(userCreateDTO.getPassword()));
        log.info("Хешування пароля для користувача {}", user.getUsername());
        User savedUser = userRepository.save(user);
        log.info("Створено користувача з ID: {}", savedUser.getId());
        return savedUser;
    }

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Користувача з ID " + userId + " не знайдено."));
    }

    @Transactional(readOnly = true)
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Користувача з іменем '" + username + "' не знайдено."));
    }

    @Transactional
    public User updateUser(Long userId, UserUpdateDTO userUpdateDTO) {
        User existingUser = getUserById(userId);
        String newUsername = userUpdateDTO.getUsername();
        if (newUsername != null && !newUsername.isBlank() && !newUsername.equals(existingUser.getUsername())) {
            userRepository.findByUsername(newUsername).ifPresent(u -> {
                throw new IllegalArgumentException("Користувач з іменем '" + newUsername + "' вже існує.");
            });
            log.info("Оновлення username для користувача ID {} на '{}'", userId, newUsername);
        }
        userMapper.updateUserFromDto(userUpdateDTO, existingUser);
        User updatedUser = userRepository.save(existingUser);
        log.info("Оновлено користувача з ID: {}", updatedUser.getId());
        return updatedUser;
    }

    @Transactional
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("Користувача з ID " + userId + " не знайдено для видалення.");
        }

        log.warn("Видалення користувача з ID: {}", userId);
        userRepository.deleteById(userId);
    }
}