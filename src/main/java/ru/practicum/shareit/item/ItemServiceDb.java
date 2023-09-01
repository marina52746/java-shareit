package ru.practicum.shareit.item;

import org.springframework.context.annotation.Primary;
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
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.rmi.ServerException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Primary
@Service
public class ItemServiceDb implements ItemService {
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final CommentMapper commentMapper;
    private final CommentRepository commentRepository;
    private final ItemMapper itemMapper;

    public ItemServiceDb(UserService userService, BookingRepository bookingRepository,
                         ItemRepository itemRepository, CommentRepository commentRepository) {
        this.userService = userService;
        this.bookingRepository = bookingRepository;
        this.itemRepository = itemRepository;
        this.itemMapper = new ItemMapper(userService, bookingRepository);
        this.commentMapper = new CommentMapper(userService, bookingRepository);
        this.commentRepository = commentRepository;
    }

    public List<ItemWithBookingsAndComments> getUserItems(Long userId) {
        List<Item> items = itemRepository.findByOwnerIdEquals(userId);
        return items.stream()
                .map(item -> {
                    return new ItemWithBookingsAndComments(
                            item.getId(),
                            item.getName(),
                            item.getDescription(),
                            item.getAvailable(),
                            BookingMapper.fromBookingToOwnerBookingDto(bookingRepository
                                    .findByItem_Owner_IdAndItem_IdAndStatusAndEndIsBefore(item.getOwner().getId(),
                                            item.getId(), BookingStatus.APPROVED,
                                    LocalDateTime.now()).stream().max(BookingMapper.bookingComparator)),
                            BookingMapper.fromBookingToOwnerBookingDto(bookingRepository
                                    .findByItem_Owner_IdAndItem_IdAndStatusAndStartIsAfter(item.getOwner().getId(),
                                            item.getId(), BookingStatus.APPROVED,
                                    LocalDateTime.now()).stream().min(BookingMapper.bookingComparator)),
                            (commentRepository.findByItem_IdEquals(item.getId(),
                                    Sort.by(Sort.Direction.DESC, "created"))).stream()
                                    .map(CommentMapper::fromCommentToCommentDto).collect(Collectors.toList()));
                }).collect(Collectors.toList());
    }

    @Override
    public ItemDto createItem(Long userId, ItemDto itemDto) throws NotFoundException {
        userService.getUserById(userId);
        itemDto.setOwnerId(userId);
        return itemMapper.fromItemToItemDto(itemRepository.save(itemMapper.fromItemDtoToItem(itemDto)));
    }

    @Override
    public CommentDto createComment(Long userId, Long itemId, CommentDto commentDto) throws NotFoundException {
        UserDto user = userService.getUserById(userId);
        itemRepository.findById(itemId);
        List<Booking> bookingList = bookingRepository.findByBooker_IdAndItem_IdAndStatusAndStartIsBefore(
                userId, itemId, BookingStatus.APPROVED, LocalDateTime.now());
        if (bookingList.isEmpty())
            throw new ValidationException("User can't leave comments");
        commentDto.setAuthorId(userId);
        commentDto.setAuthorName(user.getName());
        commentDto.setItemId(itemId);
        commentDto.setCreated(LocalDateTime.now());
        commentDto = commentMapper.fromCommentToCommentDto(
                commentRepository.save(commentMapper.fromCommentDtoToComment(commentDto, this)));
        return commentDto;
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
            return itemMapper.fromItemToItemDto(itemRepository.save(newItem));
        } catch (Exception NoSuchElementException) {
            throw new NotFoundException("Item with id = " + itemId + "doesn't exist");
        }
    }

    @Override
    public ItemDto getItemById(Long id) {
        try {
            return itemMapper.fromItemToItemDto(itemRepository.findById(id).orElseThrow(NoSuchElementException::new));
        } catch (Exception NoSuchElementException) {
            throw new NotFoundException("Item with id = " + id + "doesn't exist");
        }
    }

    @Override
    public ItemWithBookingsAndComments getItemWithCommentsById(Long userId, Long itemId) {
        Item item;
        try {
            item = itemRepository.findById(itemId).orElseThrow(NoSuchElementException::new);
        } catch (Exception NoSuchElementException) {
            throw new NotFoundException("Item with id " + itemId + " doesn't exist");
        }
        List<CommentDto> comments = commentRepository.findByItem_IdEquals(item.getId(),
                Sort.by(Sort.Direction.DESC, "created")).stream().map(CommentMapper::fromCommentToCommentDto)
                .collect(Collectors.toList());
        ItemWithBookingsAndComments itemWith = new ItemWithBookingsAndComments(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                null,
                null,
                comments);
        if (item.getOwner().getId() == userId) {
            itemWith.setLastBooking(BookingMapper.fromBookingToOwnerBookingDto(
                    bookingRepository.findByItem_Owner_IdAndItem_IdAndStatusAndStartIsBefore(item.getOwner().getId(),
                            item.getId(), BookingStatus.APPROVED, LocalDateTime.now())
                            .stream().max(BookingMapper.bookingComparator)));
            itemWith.setNextBooking(BookingMapper.fromBookingToOwnerBookingDto(
                    bookingRepository.findByItem_Owner_IdAndItem_IdAndStatusAndStartIsAfter(item.getOwner().getId(),
                            item.getId(), BookingStatus.APPROVED, LocalDateTime.now())
                            .stream().min(BookingMapper.bookingComparator)));
        }
        return itemWith;
    }

    @Override
    public void deleteItem(Long id) {
        itemRepository.deleteById(id);
    }

    @Override
    public List<ItemDto> getAllItems() {
        return itemRepository.findAll().stream().map(x -> itemMapper.fromItemToItemDto(x))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> findByText(String text) {
        if (text.isEmpty()) return new ArrayList<>();
        return itemRepository.search(text).stream().map(x -> itemMapper.fromItemToItemDto(x))
                .collect(Collectors.toList());
    }

    public List<Item> getAll() {
        return itemRepository.findAll();
    }

    public List<Comment> getAllComments() {
        return commentRepository.findAll();
    }
}
