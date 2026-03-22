package hr.abysalto.hiring.mid.service;

import hr.abysalto.hiring.mid.dto.response.DummyProduct;
import hr.abysalto.hiring.mid.dto.response.DummyProductsResponse;

public interface DummyJsonClientService {

    /**
     * Fetches a single product from the external DummyJSON API.
     *
     * @param id the product ID
     * @return the matching {@link DummyProduct}
     */
    DummyProduct getProduct(Long id);

    /**
     * Fetches a paginated, sorted list of products from the external DummyJSON API.
     *
     * @param page   zero-based page index
     * @param size   number of items per page
     * @param sortBy field name to sort by
     * @param order  sort direction: {@code "asc"} or {@code "desc"}
     * @return a {@link DummyProductsResponse} containing the page of products
     */
    DummyProductsResponse getProducts(int page, int size, String sortBy, String order);
}