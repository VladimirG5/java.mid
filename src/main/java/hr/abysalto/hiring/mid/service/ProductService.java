package hr.abysalto.hiring.mid.service;

import hr.abysalto.hiring.mid.dto.response.DummyProduct;

import java.util.List;

public interface ProductService {

    DummyProduct getProduct(Long id);

    void addToFavorites(String username, Long productId);

    void removeFromFavorites(String username, Long productId);

    List<DummyProduct> getFavorites(String username);
}
