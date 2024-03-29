package mate.academy.service;

import mate.academy.dto.CartItemQuantityRequestDto;
import mate.academy.dto.CartItemRequestDto;
import mate.academy.dto.ShoppingCartDto;
import mate.academy.model.User;
import org.springframework.security.core.Authentication;

public interface ShoppingCartService {
    void addItemToCart(User user, CartItemRequestDto cartItemRequestDto);

    ShoppingCartDto getAllCartItems(Authentication authentication);

    void updateBookQuantity(Authentication authentication, Long cartItemId,
                            CartItemQuantityRequestDto qtyToSubtract);
}
