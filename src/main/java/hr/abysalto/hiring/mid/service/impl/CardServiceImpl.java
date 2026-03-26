package hr.abysalto.hiring.mid.service.impl;

import hr.abysalto.hiring.mid.dto.request.AddToCartRequest;
import hr.abysalto.hiring.mid.dto.response.CartItemResponse;
import hr.abysalto.hiring.mid.dto.response.CartResponse;
import hr.abysalto.hiring.mid.exception.CartItemNotFoundException;
import hr.abysalto.hiring.mid.exception.CartItemQuantityException;
import hr.abysalto.hiring.mid.model.CartItem;
import hr.abysalto.hiring.mid.model.User;
import hr.abysalto.hiring.mid.repository.CartItemRepository;
import hr.abysalto.hiring.mid.service.CartService;
import hr.abysalto.hiring.mid.service.DummyJsonClientService;
import hr.abysalto.hiring.mid.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CartService {

    private final UserService userService;
    private final CartItemRepository cartItemRepository;
    private final DummyJsonClientService dummyJsonClient;

    @Override
    public CartResponse getCart(String username) {
        User user = userService.getUser(username);
        List<CartItemResponse> items = cartItemRepository.findByUserId(user.getId())
                .stream()
                .map(item -> CartItemResponse.builder()
                        .productId(item.getProductId())
                        .quantity(item.getQuantity())
                        .product(dummyJsonClient.getProduct(item.getProductId()))
                        .build())
                .toList();

        return CartResponse.builder()
                .items(items)
                .totalItems(items.size())
                .build();
    }

    @Override
    public CartResponse addToCart(String username, AddToCartRequest request) {
        User user = userService.getUser(username);
        cartItemRepository.findByUserIdAndProductId(user.getId(), request.getProductId())
                .ifPresentOrElse(existing -> {
                    existing.setQuantity(existing.getQuantity() + request.getQuantity());
                    cartItemRepository.save(existing);
                }, () -> cartItemRepository.save(CartItem.builder()
                        .userId(user.getId())
                        .productId(request.getProductId())
                        .quantity(request.getQuantity())
                        .build()));

        return getCart(username);
    }

    @Override
    public CartResponse decreaseCartItemQuantity(String username, Long productId) {
        User user = userService.getUser(username);
        CartItem item = cartItemRepository.findByUserIdAndProductId(user.getId(), productId)
                .orElseThrow(() -> new CartItemNotFoundException(productId));

        if (item.getQuantity() <= 0) {
            throw new CartItemQuantityException(productId);
        }

        item.setQuantity(item.getQuantity() - 1);
        cartItemRepository.save(item);

        return getCart(username);
    }

    @Override
    public CartResponse removeFromCart(String username, Long productId) {
        User user = userService.getUser(username);
        cartItemRepository.deleteByUserIdAndProductId(user.getId(), productId);

        return getCart(username);
    }
}
