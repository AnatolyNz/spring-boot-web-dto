package mate.academy.mapper;

import java.util.List;
import mate.academy.config.MapperConfig;
import mate.academy.dto.OrderRequestDto;
import mate.academy.dto.OrderResponseDto;
import mate.academy.model.Order;
import mate.academy.model.ShoppingCart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

@Mapper(config = MapperConfig.class, uses = OrderItemMapper.class)
public interface OrderMapper {

    OrderRequestDto toDto(ShoppingCart shoppingCart);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "orderStatus", source = "status")
    OrderResponseDto toDto(Order order);

    Order toModel(OrderRequestDto orderRequestDto);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "orderStatus", source = "status")
    List<OrderResponseDto> toResponseDtoList(Page<Order> allOrders);
}
