package mate.academy.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import mate.academy.dto.CartItemQuantityRequestDto;
import mate.academy.dto.CartItemRequestDto;
import mate.academy.dto.ShoppingCartDto;
import mate.academy.exception.EntityNotFoundException;
import mate.academy.mapper.CartItemMapper;
import mate.academy.mapper.ShoppingCartMapper;
import mate.academy.model.Book;
import mate.academy.model.CartItem;
import mate.academy.model.ShoppingCart;
import mate.academy.model.User;
import mate.academy.repository.CartItemRepository;
import mate.academy.repository.ShoppingCartRepository;
import mate.academy.repository.UserRepository;
import mate.academy.repository.book.BookRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final BookRepository bookRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final CartItemMapper cartItemMapper;
    private final ShoppingCartMapper shoppingCartMapper;

    @Override
    @Transactional
    public void addItemToCart(CartItemRequestDto cartItemRequestDto) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ShoppingCart shoppingCart = shoppingCartRepository.findShoppingCartByUserId(user.getId())
                .orElseGet(() -> registerNewShoppingCart(user));

        Optional<CartItem> existingCartItem = shoppingCart.getCartItems().stream()
                .filter(item -> item.getBook().getId().equals(cartItemRequestDto.getBookId()))
                .findFirst();
        final CartItem[] cartItemToUpdate = new CartItem[1];
        existingCartItem.ifPresentOrElse(
                cartItem -> {
                    cartItem.setQuantity(cartItemRequestDto.getQuantity() + cartItem.getQuantity());
                    cartItemToUpdate[0] = cartItem;
                },
                () -> cartItemToUpdate[0] = createNewCartItem(cartItemRequestDto, shoppingCart)
        );
        cartItemRepository.save(cartItemToUpdate[0]);
    }

    @Override
    @Transactional
    public ShoppingCartDto getAllCartItems() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ShoppingCart shoppingCart = shoppingCartRepository.findShoppingCartByUserId(user.getId())
                .orElseGet(() -> registerNewShoppingCart(user));
        return shoppingCartMapper.toDto(shoppingCart);
    }

    @Override
    @Transactional
    public void updateBookQuantity(Long cartItemId,
                                   CartItemQuantityRequestDto qtyRequestDto) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ShoppingCart shoppingCart = shoppingCartRepository.findShoppingCartByUserId(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Can't find shopping cart by "
                        + "user id " + user.getId()));

        CartItem cartItem = cartItemRepository.findByIdAndShoppingCartId(cartItemId,
                        shoppingCart.getId())
                .orElseThrow(() -> new EntityNotFoundException("Can't find cart item "
                        + "by cart item id " + cartItemId));
        cartItem.setQuantity(qtyRequestDto.getQuantity());
        cartItemRepository.save(cartItem);
    }

    private ShoppingCart registerNewShoppingCart(User user) {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(user);
        return shoppingCartRepository.save(shoppingCart);
    }

    private User getUser() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = user.getEmail();
        return userRepository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException("Can not find user by email" + email));
    }

    @Transactional
    private CartItem createNewCartItem(CartItemRequestDto cartItemRequestDto,
                                       ShoppingCart shoppingCart) {
        Book bookFromDb = bookRepository
                .findById(cartItemRequestDto.getBookId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can not find book with id: "
                                + cartItemRequestDto.getBookId()));
        CartItem cartItem = cartItemMapper.toEntity(cartItemRequestDto);
        cartItem.setBook(bookFromDb);
        cartItem.setShoppingCart(shoppingCart);
        return cartItem;
    }
}
