package ru.practicum.shareit.user;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.rmi.ServerException;
import java.util.*;

@Service
public class UserServiceImpl implements UserService {
    private static long usersCount = 0;

    public Map<Long, User> users = new HashMap<>();

    public Map<Long, String> emails = new HashMap<>();


    @Override
    public User createUser(User user) throws ServerException {
        CheckEmailExists(user.getEmail());
        user.setId(++usersCount);
        users.put(user.getId(), user);
        emails.put(user.getId(), user.getEmail());
        return user;
    }

    @Override
    public User updateUser(Long userId, UserDto user) throws NotFoundException, ServerException {
        User newUser = getUserById(userId);
        if (!((user.getEmail() == null || user.getEmail().isEmpty()))
            && !(user.getEmail().equals(newUser.getEmail()))) {
            CheckEmailExists(user.getEmail());
            newUser.setEmail(user.getEmail());
        }
        if (!(user.getName() == null || user.getName().isEmpty()))
            newUser.setName(user.getName());
        newUser.setId(userId);
        users.put(userId, newUser);
        emails.put(userId, user.getEmail());
        return newUser;
    }

    public void CheckEmailExists(String email) throws ServerException {
        if (emails.values().contains(email))
            throw new ServerException("User with this email already exists");
    }

    @Override
    public User getUserById(Long id) throws NotFoundException {
        if (users.get(id) == null)
            throw new NotFoundException("User with id = " + id + " not found");
        return users.get(id);
    }

    @Override
    public void deleteUser(Long id) {
        users.remove(id);
        emails.remove(id);
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<User>(users.values());
    }
}
