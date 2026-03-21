package hr.abysalto.hiring.mid.service;

import hr.abysalto.hiring.mid.dto.response.DummyProduct;

public interface DummyJsonClientService {
    public DummyProduct getProduct(Long id);
}
