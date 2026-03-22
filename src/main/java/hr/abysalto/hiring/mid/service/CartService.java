package hr.abysalto.hiring.mid.service;

import hr.abysalto.hiring.mid.dto.request.AddToCartRequest;
import hr.abysalto.hiring.mid.dto.response.CartResponse;

public interface CartService {

    /**
     * Returns the current cart for the given user.
     *
     * @param username the authenticated user's username
     * @return a {@link CartResponse} containing all cart items with product details
     * @throws RuntimeException if the user is not found
     */
    CartResponse getCart(String username);

    /**
     * Adds a product to the user's cart. If the product is already in the cart,
     * its quantity is incremented by the requested amount.
     *
     * @param username the authenticated user's username
     * @param request  the product ID and quantity to add
     * @return the updated {@link CartResponse}
     * @throws RuntimeException if the user is not found
     */
    CartResponse addToCart(String username, AddToCartRequest request);

    /**
     * Removes a product from the user's cart.
     *
     * @param username  the authenticated user's username
     * @param productId the ID of the product to remove
     * @return the updated {@link CartResponse}
     * @throws RuntimeException if the user is not found
     */
    CartResponse removeFromCart(String username, Long productId);
}