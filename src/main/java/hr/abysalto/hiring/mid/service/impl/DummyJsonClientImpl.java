package hr.abysalto.hiring.mid.service.impl;

import hr.abysalto.hiring.mid.dto.response.DummyProduct;
import hr.abysalto.hiring.mid.service.DummyJsonClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class DummyJsonClientImpl implements DummyJsonClientService {

    private final RestTemplate restTemplate;

    @Value("${app.dummyjson.base-url}")
    private String baseUrl;

    @Override
    public DummyProduct getProduct(Long id) {
        return restTemplate.getForObject(baseUrl + "/products/" + id, DummyProduct.class);
    }
}
