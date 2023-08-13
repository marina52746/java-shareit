package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.rmi.ServerException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class UserStorageInMemory implements UserStorage {
    private static long usersCount = 0;

    public Map<Long, User> users = new HashMap<>();

    public Map<Long, String> emails = new HashMap<>();

    @Override
    public User createUser(User user) throws ServerException {
        checkEmailExists(user.getEmail());
        user.setId(++usersCount);
        users.put(user.getId(), user);
        emails.put(user.getId(), user.getEmail());
        return user;
    }

    @Override
    public User updateUser(Long userId, User user) throws NotFoundException, ServerException {
        User changingUser = getUserById(userId);
        if (!(user.getEmail() == null || user.getEmail().isEmpty())
                && !(user.getEmail().equals(changingUser.getEmail()))) {
            checkEmailExists(user.getEmail());
            changingUser.setEmail(user.getEmail());
        }
        if (!(user.getName() == null || user.getName().isEmpty()))
            changingUser.setName(user.getName());
        users.put(userId, changingUser);
        emails.put(userId, user.getEmail());
        return changingUser;
    }

    private void checkEmailExists(String email) throws ServerException {
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
