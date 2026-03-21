package hr.abysalto.hiring.mid.service;

import hr.abysalto.hiring.mid.dto.response.DummyProduct;
import hr.abysalto.hiring.mid.dto.response.PageResponse;

import java.util.List;

public interface ProductService {

    DummyProduct getProduct(Long id);

    PageResponse<DummyProduct> getProducts(int page, int size, String sortBy, String order);

    void addToFavorites(String username, Long productId);

    void removeFromFavorites(String username, Long productId);

    List<DummyProduct> getFavorites(String username);
}
