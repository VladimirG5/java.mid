package hr.abysalto.hiring.mid.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Table;

@Table("CART_ITEMS")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItem extends BaseModel{

    private Long userId;
    private Long productId;
    private Integer quantity;
}