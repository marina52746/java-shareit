package ru.practicum.shareit.item;

import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.*;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserMapper;

import java.rmi.ServerException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Primary
@Service
public class ItemServiceDb implements ItemService {
    private final UserService userService;
    private final ItemRequestService requestService;
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;

    public ItemServiceDb(UserService userService, BookingRepository bookingRepository, ItemRequestService requestService,
                         ItemRepository itemRepository, CommentRepository commentRepository) {
        this.userService = userService;
        this.requestService = requestService;
        this.bookingRepository = bookingRepository;
        this.itemRepository = itemRepository;
        this.commentRepository = commentRepository;
    }

    public List<ItemWithBookingsAndComments> getUserItems(Long userId, Pageable pageable) {
        Page<Item> items = itemRepository.findByOwnerIdEquals(userId, pageable);
        return items.getContent().stream()
                .map(item -> getItemWithCommentsById(userId, item.getId(), item)).collect(Collectors.toList());
    }

    @Override
    public ItemDto createItem(Long userId, ItemDto itemDto) throws NotFoundException {
        User user = UserMapper.fromUserDtoToUser(userService.getUserById(userId));
        itemDto.setOwnerId(userId);
        ItemRequest request = null;
        if (itemDto.getRequestId() != null) {
            ItemRequestDto requestDto = requestService.getRequestById(userId, itemDto.getRequestId());
            request = ItemRequestMapper.fromItemRequestDtoToItemRequest(requestDto, user);
        }
        return ItemMapper.fromItemToItemDto(itemRepository.save(ItemMapper.fromItemDtoToItem(itemDto, user, request)));
    }

    @Override
    public CommentDto createComment(Long userId, Long itemId, CommentDto commentDto) throws NotFoundException {
        User user = UserMapper.fromUserDtoToUser(userService.getUserById(userId));
        Item item = itemRepository.findById(itemId).orElseThrow(
                () -> new NotFoundException("Item with id " + itemId + "doesn't exist"));
        List<Booking> bookingList = bookingRepository.findByBooker_IdAndItem_IdAndStatusAndStartIsBefore(
                userId, itemId, BookingStatus.APPROVED, LocalDateTime.now());
        if (bookingList.isEmpty())
            throw new ValidationException("User can't leave comments");
        commentDto = CommentMapper.commentDtoSetValues(commentDto, userId, user.getName(), itemId);
        Comment comment = commentRepository.save(CommentMapper.fromCommentDtoToComment(commentDto, item, user));
        return CommentMapper.fromCommentToCommentDto(comment);
    }

    private void patchItemDtoValidate(ItemDto itemDto) {
        if (itemDto.getName() != null && itemDto.getName().isBlank())
            throw new ValidationException("Item name can't be empty!");
        if (itemDto.getDescription() != null && itemDto.getDescription().isBlank())
            throw new ValidationException("Item description can't be empty!");
    }

    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) throws ServerException, NotFoundException {
        patchItemDtoValidate(itemDto);
        if (userId == null)
            throw new ServerException("X-Sharer-User-Id is absent");
        UserDto user = userService.getUserById(userId);
        if (!(itemRepository.findByOwnerIdEquals(userId).stream().map(x -> x.getId()).collect(Collectors.toList())
                .contains(itemId)))
            throw new NotFoundException("This user isn't thing's owner");
        try {
            Item newItem = itemRepository.findById(itemId).orElseThrow(NoSuchElementException::new);
            if (itemDto.getName() != null) newItem.setName(itemDto.getName());
            if (itemDto.getDescription() != null) newItem.setDescription(itemDto.getDescription());
            if (itemDto.getAvailable() != null) newItem.setAvailable(itemDto.getAvailable());
            return ItemMapper.fromItemToItemDto(itemRepository.save(newItem));
        } catch (Exception exception) {
            throw new NotFoundException("Item with id = " + itemId + "doesn't exist");
        }
    }

    @Override
    public ItemDto getItemById(Long id) {
        try {
            return ItemMapper.fromItemToItemDto(itemRepository.findById(id).orElseThrow(NoSuchElementException::new));
        } catch (Exception exception) {
            throw new NotFoundException("Item with id = " + id + "doesn't exist");
        }
    }

    @Override
    public ItemWithBookingsAndComments getItemWithCommentsById(Long userId, Long itemId, Item item) {
        if (item == null)
        try {
            item = itemRepository.findById(itemId).orElseThrow(NoSuchElementException::new);
        } catch (Exception exception) {
            throw new NotFoundException("Item with id " + itemId + " doesn't exist");
        }
        List<CommentDto> comments = commentRepository.findByItem_IdEquals(item.getId(),
                Sort.by(Sort.Direction.DESC, "created")).stream()
                .map(CommentMapper::fromCommentToCommentDto)
                .collect(Collectors.toList());
        ItemWithBookingsAndComments itemWith = ItemMapper.fromItemToItemWithBookingsAndComments(item, null,
                null, comments);
        if (item.getOwner().getId() == userId) {
            itemWith.setLastBooking(BookingMapper.fromBookingToOwnerBookingDto(
                    bookingRepository.findByItem_Owner_IdAndItem_IdAndStatusAndStartIsBefore(item.getOwner().getId(),
                            item.getId(), BookingStatus.APPROVED, LocalDateTime.now())
                            .stream().max(Booking.bookingComparator).orElse(null)));
            itemWith.setNextBooking(BookingMapper.fromBookingToOwnerBookingDto(
                    bookingRepository.findByItem_Owner_IdAndItem_IdAndStatusAndStartIsAfter(item.getOwner().getId(),
                            item.getId(), BookingStatus.APPROVED, LocalDateTime.now())
                            .stream().min(Booking.bookingComparator).orElse(null)));
        }
        return itemWith;
    }

    @Override
    public List<ItemDto> findByText(String text, Pageable pageable) {
        if (text.isEmpty()) return new ArrayList<>();
        return itemRepository.search(text, pageable).getContent().stream().map(x -> ItemMapper.fromItemToItemDto(x))
                .collect(Collectors.toList());
    }

    public List<Item> getAll() {
        return itemRepository.findAll();
    }

    public List<Comment> getAllComments() {
        return commentRepository.findAll();
    }
}
