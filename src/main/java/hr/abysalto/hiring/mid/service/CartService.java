package hr.abysalto.hiring.mid.service;

import hr.abysalto.hiring.mid.dto.request.AddToCartRequest;
import hr.abysalto.hiring.mid.dto.response.CartResponse;
import hr.abysalto.hiring.mid.model.User;

public interface CartService {
    public CartResponse getCart(String username);

    public CartResponse addToCart(String username, AddToCartRequest request);

    public CartResponse removeFromCart(String username, Long productId);

    public User getUser(String username);
}
