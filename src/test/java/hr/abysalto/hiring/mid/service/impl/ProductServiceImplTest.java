package hr.abysalto.hiring.mid.service.impl;

import hr.abysalto.hiring.mid.dto.response.DummyProduct;
import hr.abysalto.hiring.mid.dto.response.DummyProductsResponse;
import hr.abysalto.hiring.mid.dto.response.PageResponse;
import hr.abysalto.hiring.mid.model.FavoriteProduct;
import hr.abysalto.hiring.mid.model.User;
import hr.abysalto.hiring.mid.repository.FavoriteProductRepository;
import hr.abysalto.hiring.mid.service.DummyJsonClientService;
import hr.abysalto.hiring.mid.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    DummyJsonClientService dummyJsonClient;

    @Mock
    FavoriteProductRepository favoriteProductRepository;

    @Mock
    UserService userService;

    @InjectMocks
    ProductServiceImpl productService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).username("john").build();
    }

    @Test
    void getProducts_whenProductsAreRequested_thenReturnPaginatedResponse() {
        DummyProduct p = new DummyProduct();
        p.setId(1L);
        p.setTitle("Phone");

        DummyProductsResponse apiResponse = new DummyProductsResponse();
        apiResponse.setProducts(List.of(p));
        apiResponse.setTotal(50);

        when(dummyJsonClient.getProducts(0, 10, "id", "asc")).thenReturn(apiResponse);

        PageResponse<DummyProduct> result = productService.getProducts(0, 10, "id", "asc");

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("Phone");
        assertThat(result.getTotal()).isEqualTo(50);
        assertThat(result.getTotalPages()).isEqualTo(5);
        assertThat(result.getPage()).isEqualTo(0);
        assertThat(result.getSize()).isEqualTo(10);
    }

    @Test
    void getProduct_whenProductIsRequested_thenDelegateToClient() {
        DummyProduct product = new DummyProduct();
        product.setId(1L);
        product.setTitle("Phone");

        when(dummyJsonClient.getProduct(1L)).thenReturn(product);

        DummyProduct result = productService.getProduct(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("Phone");
    }

    @Test
    void addToFavorites_whenProductIsNotInFavorites_thenSaveTheProduct() {
        when(userService.getUser("john")).thenReturn(user);
        when(favoriteProductRepository.existsByUserIdAndProductId(1L, 10L)).thenReturn(false);

        productService.addToFavorites("john", 10L);

        verify(favoriteProductRepository).save(argThat(fav ->
                fav.getUserId().equals(1L) && fav.getProductId().equals(10L)));
    }

    @Test
    void addToFavorites_whenProductIsAlreadyInFavorites_thenSkip() {
        when(userService.getUser("john")).thenReturn(user);
        when(favoriteProductRepository.existsByUserIdAndProductId(1L, 10L)).thenReturn(true);

        productService.addToFavorites("john", 10L);

        verify(favoriteProductRepository, never()).save(any(FavoriteProduct.class));
    }

    @Test
    void removeFromFavorites_whenRequestIsValid_thenDeleteByUserAndProduct() {
        when(userService.getUser("john")).thenReturn(user);

        productService.removeFromFavorites("john", 10L);

        verify(favoriteProductRepository).deleteByUserIdAndProductId(1L, 10L);
    }

    @Test
    void getFavorites_whenNoFavorites_thenReturnsEmptyList() {
        when(userService.getUser("john")).thenReturn(user);
        when(favoriteProductRepository.findByUserId(1L)).thenReturn(List.of());

        List<DummyProduct> result = productService.getFavorites("john");

        assertThat(result).isEmpty();
    }

    @Test
    void getFavorites_whenRequestIsValid_thenReturnMappedProducts() {
        FavoriteProduct fav = FavoriteProduct.builder().id(1L).userId(1L).productId(10L).build();
        DummyProduct product = new DummyProduct();
        product.setId(10L);
        product.setTitle("Phone");

        when(userService.getUser("john")).thenReturn(user);
        when(favoriteProductRepository.findByUserId(1L)).thenReturn(List.of(fav));
        when(dummyJsonClient.getProduct(10L)).thenReturn(product);

        List<DummyProduct> result = productService.getFavorites("john");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Phone");
    }

    @Test
    void addToFavorites_whenUserIsNotFound_thenThrowException() {
        when(userService.getUser("ghost")).thenThrow(new RuntimeException("User not found: ghost"));

        assertThatThrownBy(() -> productService.addToFavorites("ghost", 10L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found");
    }
}