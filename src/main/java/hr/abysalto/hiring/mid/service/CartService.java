package hr.abysalto.hiring.mid.service;

import hr.abysalto.hiring.mid.dto.request.AddToCartRequest;
import hr.abysalto.hiring.mid.dto.response.CartResponse;

public interface CartService {
    CartResponse getCart(String username);

    CartResponse addToCart(String username, AddToCartRequest request);

    CartResponse removeFromCart(String username, Long productId);

}
