package mate.academy.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import mate.academy.dto.OrderItemResponseDto;
import mate.academy.dto.OrderResponseDto;
import mate.academy.dto.OrderStatusDto;
import mate.academy.dto.ShippingAddressRequestDto;
import mate.academy.exception.EntityNotFoundException;
import mate.academy.mapper.OrderItemMapper;
import mate.academy.mapper.OrderMapper;
import mate.academy.model.Order;
import mate.academy.model.OrderItem;
import mate.academy.model.ShoppingCart;
import mate.academy.model.User;
import mate.academy.repository.OrderItemRepository;
import mate.academy.repository.OrderRepository;
import mate.academy.repository.ShoppingCartRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;

    @Override
    @Transactional
    public Order placeOrder(User user, ShippingAddressRequestDto shippingAddress) {
        ShoppingCart shoppingCart = shoppingCartRepository.findShoppingCartByUserId(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Can't find shopping cart "
                        + "by user id " + user.getId()));
        double total = shoppingCart.getCartItems().stream()
                .mapToDouble(cartItem -> (double) cartItem.getQuantity()
                        * cartItem.getBook().getPrice().doubleValue())
                .sum();
        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(shippingAddress.getShippingAddress());
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(Order.Status.NEW);
        order.setTotal(BigDecimal.valueOf(total));
        Set<OrderItem> orderItems = shoppingCart.getCartItems().stream()
                .map(cartItem -> {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setBook(cartItem.getBook());
                    orderItem.setQuantity(cartItem.getQuantity());
                    orderItem.setPrice(cartItem.getBook().getPrice());
                    orderItem.setOrder(order);
                    return orderItemRepository.save(orderItem);
                })
                .collect(Collectors.toSet());
        shoppingCartRepository.delete(shoppingCart);
        return orderRepository.save(order);
    }

    @Override
    public List<OrderResponseDto> getAllOrders(User user,
                                               Pageable pageable) {
        Page<Order> allOrders = orderRepository.findAllByUserId(user.getId(), pageable);
        return allOrders.stream()
                .map(orderMapper::toResponseDto)
                .toList();
    }

    @Override
    public OrderResponseDto updateOrderStatus(Long orderId,
                                              OrderStatusDto statusDto) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Can't find order by id "
                        + orderId));
        order.setStatus(statusDto.getStatus());
        return orderMapper.toDto(orderRepository.save(order));
    }

    @Override
    public List<OrderItemResponseDto> getAllOrderItems(User user, Long orderId, Pageable pageable) {
        Order order = orderRepository.findByUserIdAndId(user.getId(), orderId)
                .orElseThrow(() -> new EntityNotFoundException("Can't find order by order id "
                        + orderId + " and user id " + user.getId()));
        return (List<OrderItemResponseDto>) orderItemMapper.toResponseDto((OrderItem) order.getOrderItems());
    }

    @Override
    public OrderItemResponseDto getSpecificOrderItem(Long orderId) {
        OrderItem orderItem = orderItemRepository.findByOrderId(orderId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Can't find order item by order id."));
        return orderItemMapper.toResponseDto(orderItem);
    }
}
