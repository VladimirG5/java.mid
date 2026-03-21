package hr.abysalto.hiring.mid.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Table;

@Table("FAVORITE_PRODUCTS")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteProduct extends BaseModel{

    private Long userId;
    private Long productId;
}
