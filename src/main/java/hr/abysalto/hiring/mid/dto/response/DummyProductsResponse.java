package hr.abysalto.hiring.mid.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DummyProductsResponse {

    private List<DummyProduct> products;
    private Integer total;
    private Integer skip;
    private Integer limit;
}