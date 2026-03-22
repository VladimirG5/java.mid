package hr.abysalto.hiring.mid.service.impl;

import hr.abysalto.hiring.mid.dto.request.AddToCartRequest;
import hr.abysalto.hiring.mid.dto.response.CartResponse;
import hr.abysalto.hiring.mid.dto.response.DummyProduct;
import hr.abysalto.hiring.mid.model.CartItem;
import hr.abysalto.hiring.mid.model.User;
import hr.abysalto.hiring.mid.repository.CartItemRepository;
import hr.abysalto.hiring.mid.service.DummyJsonClientService;
import hr.abysalto.hiring.mid.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CardServiceImplTest {

    @Mock
    CartItemRepository cartItemRepository;

    @Mock
    UserService userService;

    @Mock
    DummyJsonClientService dummyJsonClient;

    @InjectMocks
    CardServiceImpl cartService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).username("john").build();
    }

    @Test
    void getCart_whenItemsAndProductsArePresent_thenReturnCartResponse() {
        CartItem item = CartItem.builder().id(1L).userId(1L).productId(10L).quantity(2).build();
        DummyProduct product = new DummyProduct();
        product.setId(10L);
        product.setTitle("Phone");

        when(userService.getUser("john")).thenReturn(user);
        when(cartItemRepository.findByUserId(1L)).thenReturn(List.of(item));
        when(dummyJsonClient.getProduct(10L)).thenReturn(product);

        CartResponse response = cartService.getCart("john");

        assertThat(response.getTotalItems()).isEqualTo(1);
        assertThat(response.getItems().get(0).getProductId()).isEqualTo(10L);
        assertThat(response.getItems().get(0).getQuantity()).isEqualTo(2);
        assertThat(response.getItems().get(0).getProduct().getTitle()).isEqualTo("Phone");
    }

    @Test
    void getCart_whenNoItems_thenReturnEmptyCart() {
        when(userService.getUser("john")).thenReturn(user);
        when(cartItemRepository.findByUserId(1L)).thenReturn(List.of());

        CartResponse response = cartService.getCart("john");

        assertThat(response.getTotalItems()).isEqualTo(0);
        assertThat(response.getItems()).isEmpty();
    }

    @Test
    void addToCart_whenItemDoesNotExists_thenCreateNewItem() {
        AddToCartRequest request = new AddToCartRequest();
        request.setProductId(10L);
        request.setQuantity(2);

        when(userService.getUser("john")).thenReturn(user);
        when(cartItemRepository.findByUserIdAndProductId(1L, 10L)).thenReturn(Optional.empty());
        when(cartItemRepository.findByUserId(1L)).thenReturn(List.of());

        cartService.addToCart("john", request);

        verify(cartItemRepository).save(argThat(item ->
                item.getUserId().equals(1L) &&
                        item.getProductId().equals(10L) &&
                        item.getQuantity().equals(2)));
    }

    @Test
    void addToCart_whenItemAlreadyExists_thenIncrementQuantity() {
        AddToCartRequest request = new AddToCartRequest();
        request.setProductId(10L);
        request.setQuantity(3);

        CartItem existing = CartItem.builder().id(1L).userId(1L).productId(10L).quantity(2).build();

        when(userService.getUser("john")).thenReturn(user);
        when(cartItemRepository.findByUserIdAndProductId(1L, 10L)).thenReturn(Optional.of(existing));
        when(cartItemRepository.findByUserId(1L)).thenReturn(List.of());

        cartService.addToCart("john", request);

        verify(cartItemRepository).save(argThat(item -> item.getQuantity().equals(5)));
    }

    @Test
    void removeFromCart_whenRequestIsValid_thenDeleteItemAndReturnCart() {
        when(userService.getUser("john")).thenReturn(user);
        when(cartItemRepository.findByUserId(1L)).thenReturn(List.of());

        CartResponse response = cartService.removeFromCart("john", 10L);

        verify(cartItemRepository).deleteByUserIdAndProductId(1L, 10L);
        assertThat(response.getTotalItems()).isEqualTo(0);
    }

    @Test
    void getCart_whenUserIsNotFound_thenThrowException() {
        when(userService.getUser("ghost")).thenThrow(new RuntimeException("User not found: ghost"));

        assertThatThrownBy(() -> cartService.getCart("ghost"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found");
    }
}