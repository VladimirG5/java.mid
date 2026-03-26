package hr.abysalto.hiring.mid.exception;

public class CartItemQuantityException extends RuntimeException {

    public CartItemQuantityException(Long productId) {
        super("Cannot decrease quantity below zero for product id: " + productId);
    }
}
