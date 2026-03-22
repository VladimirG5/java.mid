package hr.abysalto.hiring.mid.service;

import hr.abysalto.hiring.mid.dto.response.DummyProduct;
import hr.abysalto.hiring.mid.dto.response.PageResponse;

import java.util.List;

public interface ProductService {

    /**
     * Retrieves a single product by its ID from the external product source.
     *
     * @param id the product ID
     * @return the matching {@link DummyProduct}
     */
    DummyProduct getProduct(Long id);

    /**
     * Returns a paginated, sorted list of products from the external product source.
     *
     * @param page   zero-based page index
     * @param size   number of items per page
     * @param sortBy field name to sort by
     * @param order  sort direction: {@code "asc"} or {@code "desc"}
     * @return a {@link PageResponse} containing the requested page of products
     */
    PageResponse<DummyProduct> getProducts(int page, int size, String sortBy, String order);

    /**
     * Adds a product to the user's favorites.
     *
     * @param username  the authenticated user's username
     * @param productId the ID of the product to favorite
     * @throws RuntimeException if the user is not found
     */
    void addToFavorites(String username, Long productId);

    /**
     * Removes a product from the user's favorites.
     *
     * @param username  the authenticated user's username
     * @param productId the ID of the product to unfavorite
     * @throws RuntimeException if the user is not found
     */
    void removeFromFavorites(String username, Long productId);

    /**
     * Returns all favorited products for the given user, enriched with product details.
     *
     * @param username the authenticated user's username
     * @return list of favorited {@link DummyProduct} objects
     * @throws RuntimeException if the user is not found
     */
    List<DummyProduct> getFavorites(String username);
}