package com.edu.froliak.mapper;

import com.edu.froliak.dto.UserBasicDTO;
import com.edu.froliak.dto.UserCreateDTO;
import com.edu.froliak.dto.UserDTO;
import com.edu.froliak.dto.UserUpdateDTO;
import com.edu.froliak.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    UserDTO toUserDTO(User user);
    UserBasicDTO toUserBasicDTO(User user);
    List<UserDTO> toUserDTOList(List<User> users);

    @Mapping(target = "id", ignore = true)
    User toUser(UserCreateDTO userCreateDTO); // Пароль буде хешовано в сервісі

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true) // Пароль не оновлюємо через цей DTO
    void updateUserFromDto(UserUpdateDTO userUpdateDTO, @MappingTarget User user);
}