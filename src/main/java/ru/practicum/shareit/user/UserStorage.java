package ru.practicum.shareit.user;

import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.rmi.ServerException;
import java.util.List;

public interface UserStorage {
    User createUser(User user) throws ServerException;

    User updateUser(Long userId, User user) throws NotFoundException, ServerException;

    User getUserById(Long id) throws NotFoundException;

    void deleteUser(Long id);

    List<User> getAllUsers();
}
