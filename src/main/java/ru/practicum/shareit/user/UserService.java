package ru.practicum.shareit.user;

import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;

import java.rmi.ServerException;
import java.util.List;

public interface UserService {

    UserDto createUser(UserDto userDto) throws ServerException;

    UserDto updateUser(Long userId, UserDto user) throws NotFoundException, ServerException, ValidationException;

    UserDto getUserById(Long id) throws NotFoundException;

    void deleteUser(Long id);

    List<UserDto> getAllUsers();
}
