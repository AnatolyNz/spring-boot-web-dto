package mate.academy.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mate.academy.dto.CartItemQuantityRequestDto;
import mate.academy.dto.CartItemRequestDto;
import mate.academy.dto.ShoppingCartDto;
import mate.academy.repository.CartItemRepository;
import mate.academy.service.ShoppingCartService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Shopping cart management", description = "Shopping cart for managing categories")
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/cart")
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;
    private final CartItemRepository cartItemRepository;

    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PostMapping
    @Operation(summary = "Add new book to cart",
            description = "Before adding new book we check if this book is already in cart "
                    + "and than or change qty or add new cartItem")
    public void addBook(Authentication authentication,
                        @RequestBody @Valid CartItemRequestDto cartItemRequestDto) {
        shoppingCartService.addItemToCart(authentication, cartItemRequestDto);
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping
    @Operation(summary = "Get all books from cart", description = "Get all books from cart")
    public ShoppingCartDto getAllCartItems(Authentication authentication) {
        return shoppingCartService.getAllCartItems(authentication);
    }

    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PutMapping("/cart-items/{id}")
    @Operation(summary = "Update book qty in cart",
            description = "You just set new qty of book in your shopping cart")
    public void updateCartItemByBookId(Authentication authentication, @PathVariable Long id,
                                       @RequestBody @Valid
                                       CartItemQuantityRequestDto qty) {
        shoppingCartService.updateBookQuantity(authentication, id, qty);
    }

    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @DeleteMapping("/cart-items/{id}")
    @Operation(summary = "Delete cart item", description = "Delete cart item")
    public void removeCartItemByBookId(@PathVariable Long id) {
        cartItemRepository.deleteById(id);
    }
}
