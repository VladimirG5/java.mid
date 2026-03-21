package hr.abysalto.hiring.mid.repository;

import hr.abysalto.hiring.mid.model.FavoriteProduct;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteProductRepository extends CrudRepository<FavoriteProduct, Long> {

    List<FavoriteProduct> findByUserId(Long userId);

    boolean existsByUserIdAndProductId(Long userId, Long productId);

    Optional<FavoriteProduct> findByUserIdAndProductId(Long userId, Long productId);

    @Modifying
    @Query("DELETE FROM favorite_products WHERE user_id = :userId AND product_id = :productId")
    void deleteByUserIdAndProductId(Long userId, Long productId);
}
