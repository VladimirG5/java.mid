package hr.abysalto.hiring.mid.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CartItemResponse {

    private Long productId;
    private Integer quantity;
    private DummyProduct product;
}
