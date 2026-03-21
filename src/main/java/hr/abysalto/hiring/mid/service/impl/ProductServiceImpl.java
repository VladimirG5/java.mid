package hr.abysalto.hiring.mid.service.impl;

import hr.abysalto.hiring.mid.dto.response.DummyProduct;
import hr.abysalto.hiring.mid.service.DummyJsonClientService;
import hr.abysalto.hiring.mid.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final DummyJsonClientService dummyJsonClient;

    @Override
    public DummyProduct getProduct(Long id) {
        return dummyJsonClient.getProduct(id);
    }
}
