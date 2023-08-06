package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import javax.management.InstanceNotFoundException;
import javax.validation.Valid;
import java.rmi.ServerException;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserDto> findAll() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public UserDto findUserById(@PathVariable("id") Long userId) throws NotFoundException {
        return userService.getUserById(userId);
    }

    @PostMapping
    public UserDto createUser(@Valid @RequestBody UserDto userDto) throws ServerException {
        return userService.createUser(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto update(@RequestBody UserDto userDto,
                       @PathVariable Long userId) throws InstanceNotFoundException, ServerException {
        return userService.updateUser(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable Long userId) {
        userService.deleteUser(userId);
    }
}
