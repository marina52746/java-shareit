package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.pagination.FromSizeRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;

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
                                 @RequestBody ItemRequestDto itemRequestDto) {
        ShareItServer.sharerUserId = userId;
        return itemRequestService.createItemRequest(itemRequestDto, ShareItServer.sharerUserId);
    }

    @GetMapping
    public List<ItemRequestDto> getUserRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        ShareItServer.sharerUserId = userId;
        return itemRequestService.getUserRequests(ShareItServer.sharerUserId);
    }

    //GET /requests/all?from={from}&size={size}
    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequestsExceptUsers(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(required = false, defaultValue = "0") Integer from,
            @RequestParam(required = false, defaultValue = "10") Integer size) {
        ShareItServer.sharerUserId = userId;
        final PageRequest pageRequest = FromSizeRequest.of(from, size, Sort.by(ASC, "id"));
        return itemRequestService.getAllRequestsExceptUsers(ShareItServer.sharerUserId, pageRequest);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @PathVariable Long requestId) {
        ShareItServer.sharerUserId = userId;
        return itemRequestService.getRequestByIdOrThrow(ShareItServer.sharerUserId, requestId);
    }
}
