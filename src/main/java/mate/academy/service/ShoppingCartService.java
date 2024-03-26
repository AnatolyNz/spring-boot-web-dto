package mate.academy.service;

import mate.academy.dto.CartItemQuantityRequestDto;
import mate.academy.dto.CartItemRequestDto;
import mate.academy.dto.ShoppingCartDto;

public interface ShoppingCartService {
    void addItemToCart(CartItemRequestDto cartItemRequestDto);

    ShoppingCartDto getAllCartItems();

    void updateBookQuantity(Long cartItemId,
                            CartItemQuantityRequestDto qtyToSubtract);
}
