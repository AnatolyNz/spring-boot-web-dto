package mate.academy.service;

import java.util.List;
import mate.academy.dto.OrderItemResponseDto;
import mate.academy.dto.OrderResponseDto;
import mate.academy.dto.OrderStatusDto;
import mate.academy.dto.ShippingAddressRequestDto;
import mate.academy.model.User;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    void placeOrder(User user,
                    ShippingAddressRequestDto shippingAddress);

    List<OrderResponseDto> getAllOrders(User user, Pageable pageable);

    OrderResponseDto updateOrderStatus(User user, Long orderId,
                                       OrderStatusDto statusDto);

    List<OrderItemResponseDto> getAllOrderItems(User user,
                                                Long orderId, Pageable pageable);

    OrderItemResponseDto getSpecificOrderItem(User user, Long orderId,
                                              Long itemId);
}
