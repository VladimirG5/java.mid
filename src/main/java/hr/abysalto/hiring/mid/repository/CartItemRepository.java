package hr.abysalto.hiring.mid.repository;


import hr.abysalto.hiring.mid.model.CartItem;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends CrudRepository<CartItem, Long> {

    List<CartItem> findByUserId(Long userId);

    Optional<CartItem> findByUserIdAndProductId(Long userId, Long productId);

    @Modifying
    @Query("DELETE FROM cart_items WHERE user_id = :userId AND product_id = :productId")
    void deleteByUserIdAndProductId(Long userId, Long productId);
}
