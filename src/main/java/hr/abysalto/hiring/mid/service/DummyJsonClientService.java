package hr.abysalto.hiring.mid.service;

import hr.abysalto.hiring.mid.dto.response.DummyProduct;
import hr.abysalto.hiring.mid.dto.response.DummyProductsResponse;

public interface DummyJsonClientService {
    public DummyProduct getProduct(Long id);

    public DummyProductsResponse getProducts(int page, int size, String sortBy, String order);
}
