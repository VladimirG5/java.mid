package hr.abysalto.hiring.mid.service.impl;

import hr.abysalto.hiring.mid.dto.response.DummyProduct;
import hr.abysalto.hiring.mid.dto.response.DummyProductsResponse;
import hr.abysalto.hiring.mid.dto.response.PageResponse;
import hr.abysalto.hiring.mid.model.FavoriteProduct;
import hr.abysalto.hiring.mid.model.User;
import hr.abysalto.hiring.mid.repository.FavoriteProductRepository;
import hr.abysalto.hiring.mid.service.DummyJsonClientService;
import hr.abysalto.hiring.mid.service.ProductService;
import hr.abysalto.hiring.mid.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final UserService userService;
    private final DummyJsonClientService dummyJsonClient;
    private final FavoriteProductRepository favoriteProductRepository;

    @Override
    public DummyProduct getProduct(Long id) {
        return dummyJsonClient.getProduct(id);
    }

    public PageResponse<DummyProduct> getProducts(int page, int size, String sortBy, String order) {
        DummyProductsResponse response = dummyJsonClient.getProducts(page, size, sortBy, order);
        int totalPages = (int) Math.ceil((double) response.getTotal() / size);

        return PageResponse.<DummyProduct>builder()
                .content(response.getProducts())
                .page(page)
                .size(size)
                .total(response.getTotal())
                .totalPages(totalPages)
                .build();
    }


    @Override
    public void addToFavorites(String username, Long productId) {
        User user = userService.getUser(username);
        if (!favoriteProductRepository.existsByUserIdAndProductId(user.getId(), productId)) {
            favoriteProductRepository.save(FavoriteProduct.builder()
                    .userId(user.getId())
                    .productId(productId)
                    .build());
        }
    }

    @Override
    public void removeFromFavorites(String username, Long productId) {
        User user = userService.getUser(username);
        favoriteProductRepository.deleteByUserIdAndProductId(user.getId(), productId);
    }

    @Override
    public List<DummyProduct> getFavorites(String username) {
        User user = userService.getUser(username);

        return favoriteProductRepository.findByUserId(user.getId())
                .stream()
                .map(fav -> dummyJsonClient.getProduct(fav.getProductId()))
                .toList();
    }

}
