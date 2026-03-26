package hr.abysalto.hiring.mid.service.impl;

import hr.abysalto.hiring.mid.dto.response.DummyProduct;
import hr.abysalto.hiring.mid.dto.response.DummyProductsResponse;
import hr.abysalto.hiring.mid.service.DummyJsonClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class DummyJsonClientImpl implements DummyJsonClientService {

    private final WebClient webClient;

    @Override
    @Cacheable(value = "products", key = "#page + '_' + #size + '_' + #sortBy + '_' + #order")
    public DummyProductsResponse getProducts(int page, int size, String sortBy, String order) {
        int skip = page * size;
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/products")
                        .queryParam("limit", size)
                        .queryParam("skip", skip)
                        .queryParam("sortBy", sortBy)
                        .queryParam("order", order)
                        .build())
                .retrieve()
                .bodyToMono(DummyProductsResponse.class)
                .block();
    }

    @Override
    @Cacheable(value = "product", key = "#id")
    public DummyProduct getProduct(Long id) {
        return webClient.get()
                .uri("/products/{id}", id)
                .retrieve()
                .bodyToMono(DummyProduct.class)
                .block();
    }
}
