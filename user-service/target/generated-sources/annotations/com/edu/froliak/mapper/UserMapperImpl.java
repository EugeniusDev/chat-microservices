package com.edu.froliak.mapper;

import com.edu.froliak.dto.UserBasicDTO;
import com.edu.froliak.dto.UserCreateDTO;
import com.edu.froliak.dto.UserDTO;
import com.edu.froliak.dto.UserUpdateDTO;
import com.edu.froliak.model.User;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-05-07T12:09:33+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.14 (Amazon.com Inc.)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserDTO toUserDTO(User user) {
        if ( user == null ) {
            return null;
        }

        UserDTO userDTO = new UserDTO();

        userDTO.setId( user.getId() );
        userDTO.setUsername( user.getUsername() );

        return userDTO;
    }

    @Override
    public UserBasicDTO toUserBasicDTO(User user) {
        if ( user == null ) {
            return null;
        }

        UserBasicDTO userBasicDTO = new UserBasicDTO();

        userBasicDTO.setId( user.getId() );
        userBasicDTO.setUsername( user.getUsername() );

        return userBasicDTO;
    }

    @Override
    public List<UserDTO> toUserDTOList(List<User> users) {
        if ( users == null ) {
            return null;
        }

        List<UserDTO> list = new ArrayList<UserDTO>( users.size() );
        for ( User user : users ) {
            list.add( toUserDTO( user ) );
        }

        return list;
    }

    @Override
    public User toUser(UserCreateDTO userCreateDTO) {
        if ( userCreateDTO == null ) {
            return null;
        }

        User user = new User();

        user.setUsername( userCreateDTO.getUsername() );
        user.setPassword( userCreateDTO.getPassword() );

        return user;
    }

    @Override
    public void updateUserFromDto(UserUpdateDTO userUpdateDTO, User user) {
        if ( userUpdateDTO == null ) {
            return;
        }

        if ( userUpdateDTO.getUsername() != null ) {
            user.setUsername( userUpdateDTO.getUsername() );
        }
    }
}
