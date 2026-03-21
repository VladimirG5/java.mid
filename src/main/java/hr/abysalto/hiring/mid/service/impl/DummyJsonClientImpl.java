package hr.abysalto.hiring.mid.service.impl;

import hr.abysalto.hiring.mid.dto.response.DummyProduct;
import hr.abysalto.hiring.mid.dto.response.DummyProductsResponse;
import hr.abysalto.hiring.mid.service.DummyJsonClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@RequiredArgsConstructor
public class DummyJsonClientImpl implements DummyJsonClientService {

    private final RestTemplate restTemplate;

    @Value("${app.dummyjson.base-url}")
    private String baseUrl;

    @Override
    @Cacheable(value = "products", key = "#page + '_' + #size + '_' + #sortBy + '_' + #order")
    public DummyProductsResponse getProducts(int page, int size, String sortBy, String order) {
        int skip = page * size;
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/products")
                .queryParam("limit", size)
                .queryParam("skip", skip)
                .queryParam("sortBy", sortBy)
                .queryParam("order", order)
                .toUriString();
        return restTemplate.getForObject(url, DummyProductsResponse.class);
    }

    @Override
    @Cacheable(value = "product", key = "#id")
    public DummyProduct getProduct(Long id) {
        return restTemplate.getForObject(baseUrl + "/products/" + id, DummyProduct.class);
    }
}
