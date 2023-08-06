package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.UserMapper;

import java.rmi.ServerException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserServiceImpl(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public UserDto createUser(UserDto userDto) throws ServerException {
        return UserMapper.fromUserToUserDto(userStorage.createUser(UserMapper.fromUserDtoToUser(userDto)));
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) throws NotFoundException, ServerException,
            ValidationException {
        patchUserDtoValidate(userDto);
        return UserMapper.fromUserToUserDto(userStorage.updateUser(userId, UserMapper.fromUserDtoToUser(userDto)));
    }

    private void patchUserDtoValidate(UserDto userDto) throws ValidationException{
        if (userDto.getEmail() != null) {
            Matcher matcher = validateEmail.matcher(userDto.getEmail());
            if (!matcher.matches())
                throw new ValidationException("Email " + userDto.getEmail() + " is not Valid");
        }
    }

    private static final Pattern validateEmail =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    @Override
    public UserDto getUserById(Long id) throws NotFoundException {
        return UserMapper.fromUserToUserDto(userStorage.getUserById(id));
    }

    @Override
    public void deleteUser(Long id) {
        userStorage.deleteUser(id);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userStorage.getAllUsers().stream().map(x -> UserMapper.fromUserToUserDto(x))
                .collect(Collectors.toList());
    }
}
