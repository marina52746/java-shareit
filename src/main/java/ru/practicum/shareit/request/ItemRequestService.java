package ru.practicum.shareit.request;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto createItemRequest(ItemRequestDto itemRequestDto, Long userId);

    List<ItemRequestDto> getUserRequests(Long userId);

    List<ItemRequestDto> getAllRequestsExceptUsers(Long userId, Pageable pageable);

    ItemRequestDto getRequestByIdOrThrow(Long userId, Long requestId);

    ItemRequestDto getRequestById(Long userId, Long requestId);
}
