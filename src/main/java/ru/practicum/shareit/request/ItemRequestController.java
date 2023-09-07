package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.ShareItApp;
import ru.practicum.shareit.pagination.FromSizeRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static org.springframework.data.domain.Sort.Direction.ASC;

@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @Autowired
    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @PostMapping
    public ItemRequestDto createRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @Valid @RequestBody ItemRequestDto itemRequestDto) {
        ShareItApp.sharerUserId = userId;
        return itemRequestService.createItemRequest(itemRequestDto, ShareItApp.sharerUserId);
    }

    @GetMapping
    public List<ItemRequestDto> getUserRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        ShareItApp.sharerUserId = userId;
        return itemRequestService.getUserRequests(ShareItApp.sharerUserId);
    }

    //GET /requests/all?from={from}&size={size}
    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequestsExceptUsers(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PositiveOrZero @RequestParam(required = false, defaultValue = "0") Integer from,
            @Positive @RequestParam(required = false, defaultValue = "10") Integer size) {
        ShareItApp.sharerUserId = userId;
        final PageRequest pageRequest = FromSizeRequest.of(from, size, Sort.by(ASC, "id"));
        return itemRequestService.getAllRequestsExceptUsers(ShareItApp.sharerUserId, pageRequest);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @PathVariable Long requestId) {
        ShareItApp.sharerUserId = userId;
        return itemRequestService.getRequestByIdOrThrow(ShareItApp.sharerUserId, requestId);
    }
}
