package hr.abysalto.hiring.mid.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CartResponse {

    private List<CartItemResponse> items;
    private int totalItems;
}
