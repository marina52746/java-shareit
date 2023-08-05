package ru.practicum.shareit.user;

import org.springframework.web.client.HttpClientErrorException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import javax.management.InstanceNotFoundException;
import java.rmi.ServerException;
import java.util.List;

public interface UserService {

    User createUser(User user) throws ServerException;

    User updateUser(Long userId, UserDto user) throws NotFoundException, ServerException;

    User getUserById(Long id) throws NotFoundException;

    void deleteUser(Long id);

    List<User> getAllUsers();
}
