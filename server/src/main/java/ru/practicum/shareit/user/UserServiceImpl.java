package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        return UserMapper.fromUserToUserDto(userRepository.save(UserMapper.fromUserDtoToUser(userDto)));
    }

    @Override
    @Modifying
    public UserDto updateUser(Long userId, UserDto user) throws NotFoundException, ValidationException {
        if (!(userRepository.existsById(userId)))
            throw new NotFoundException("User with id = " + userId + "doesn't exist");
        UserDto changingUser = getUserById(userId);
        if (!(user.getEmail() == null || user.getEmail().isEmpty())
                && !(user.getEmail().equals(changingUser.getEmail()))) {
            changingUser.setEmail(user.getEmail());
        }
        if (!(user.getName() == null || user.getName().isEmpty()))
            changingUser.setName(user.getName());
        userRepository.save(UserMapper.fromUserDtoToUser(changingUser));
        return changingUser;
    }

    public UserDto getUserById(Long id) throws NotFoundException {
        try {
            return UserMapper.fromUserToUserDto(userRepository.findById(id).orElseThrow());
        } catch (Exception exception) {
            throw new NotFoundException("User with id = " + id + " doesn't exist");
        }
    }

    public ResponseEntity<Object> deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream().map(x -> UserMapper.fromUserToUserDto(x))
                .collect(Collectors.toList());
    }

}
