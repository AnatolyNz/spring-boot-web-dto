package mate.academy.mapper;

import mate.academy.config.MapperConfig;
import mate.academy.dto.OrderItemDto;
import mate.academy.dto.OrderItemResponseDto;
import mate.academy.model.CartItem;
import mate.academy.model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface OrderItemMapper {
    @Mapping(target = "price", source = "book.price")
    OrderItemDto toDto(CartItem cartItem);

    OrderItem toEntity(OrderItemDto orderItemDto);

    @Mapping(target = "bookId", source = "book.id")
    OrderItemResponseDto toResponseDto(OrderItem orderItem);
}
