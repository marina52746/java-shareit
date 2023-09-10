package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemRequestServiceDb implements ItemRequestService {
    private final UserService userService;
    private final ItemRequestRepository requestRepository;
    private final ItemRepository itemRepository;

    @Autowired
    public ItemRequestServiceDb(UserService userService, ItemRequestRepository requestRepository,
                                ItemRepository itemRepository) {
        this.userService = userService;
        this.requestRepository = requestRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public ItemRequestDto createItemRequest(ItemRequestDto itemRequestDto, Long userId) {
        itemRequestDto.setCreated(LocalDateTime.now());
        User user = UserMapper.fromUserDtoToUser(userService.getUserById(userId));
        return ItemRequestMapper.fromItemRequestToItemRequestDto(requestRepository.save(
                ItemRequestMapper.fromItemRequestDtoToItemRequest(itemRequestDto, user)), new ArrayList<>());
    }

    @Override
    public List<ItemRequestDto> getUserRequests(Long userId) {
        userService.getUserById(userId);
        List<ItemRequest> requests = requestRepository.findByRequestor_IdOrderByCreatedDesc(userId);
        return requests.stream().map(request -> ItemRequestMapper.fromItemRequestToItemRequestDto(
                request, itemRepository.findByRequest_IdOrderByRequest_CreatedDesc(request.getId()).stream()
                        .map(item -> ItemMapper.fromItemToItemDto(item)).collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getAllRequestsExceptUsers(Long userId, Pageable pageable) {
        userService.getUserById(userId);
        Page<ItemRequest> requests = requestRepository.findByRequestor_IdNotOrderByCreatedDesc(userId, pageable);
        return requests.getContent().stream().map(request -> ItemRequestMapper.fromItemRequestToItemRequestDto(
                        request, itemRepository.findByRequest_IdOrderByRequest_CreatedDesc(request.getId()).stream()
                                .map(item -> ItemMapper.fromItemToItemDto(item)).collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getRequestByIdOrThrow(Long userId, Long requestId) {
        userService.getUserById(userId);
        ItemRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request with id " + requestId + " not found"));
        return ItemRequestMapper.fromItemRequestToItemRequestDto(
                request, itemRepository.findByRequest_IdOrderByRequest_CreatedDesc(request.getId())
                        .stream().map(item -> ItemMapper.fromItemToItemDto(item)).collect(Collectors.toList()));
    }

    public ItemRequestDto getRequestById(Long userId, Long requestId) {
        userService.getUserById(userId);
        ItemRequest request = requestRepository.findById(requestId).orElse(null);
        if (request == null) return null;
        return ItemRequestMapper.fromItemRequestToItemRequestDto(
                request, itemRepository.findByRequest_IdOrderByRequest_CreatedDesc(request.getId())
                        .stream().map(item -> ItemMapper.fromItemToItemDto(item)).collect(Collectors.toList()));
    }
}
