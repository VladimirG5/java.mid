package hr.abysalto.hiring.mid.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import hr.abysalto.hiring.mid.dto.request.LoginRequest;
import hr.abysalto.hiring.mid.dto.request.RegisterRequest;
import hr.abysalto.hiring.mid.dto.response.DummyProduct;
import hr.abysalto.hiring.mid.dto.response.DummyProductsResponse;
import hr.abysalto.hiring.mid.service.DummyJsonClientService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProductIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    DummyJsonClientService dummyJsonClientService;

    private String token;

    @BeforeAll
    void setUp() throws Exception {
        RegisterRequest register = RegisterRequest.builder()
                .username("product_user")
                .email("product_user@example.com")
                .password("secret123")
                .build();

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isOk());

        LoginRequest login = LoginRequest.builder()
                .username("product_user")
                .password("secret123")
                .build();

        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andReturn();

        token = objectMapper.readTree(result.getResponse().getContentAsString())
                .get("token").asText();

        assertThat(token).isNotBlank();
    }

    @Test
    void getProducts_whenValidParams_thenReturnPage() throws Exception {
        DummyProductsResponse response = new DummyProductsResponse();
        response.setProducts(List.of(buildProduct(1L, "iPhone"), buildProduct(2L, "Laptop")));
        response.setTotal(100);
        response.setSkip(0);
        response.setLimit(10);

        when(dummyJsonClientService.getProducts(0, 10, "id", "asc")).thenReturn(response);

        mockMvc.perform(get("/products")
                        .header("Authorization", "Bearer " + token)
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "id")
                        .param("order", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.total").value(100))
                .andExpect(jsonPath("$.totalPages").value(10));
    }

    @Test
    void getProduct_whenProductExists_thenReturnProduct() throws Exception {
        DummyProduct product = buildProduct(1L, "iPhone");
        when(dummyJsonClientService.getProduct(1L)).thenReturn(product);

        mockMvc.perform(get("/products/1")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("iPhone"));
    }

    @Test
    void addToFavorites_thenGetFavorites_thenRemove_fullFlow() throws Exception {
        DummyProduct product = buildProduct(5L, "Smartwatch");
        when(dummyJsonClientService.getProduct(5L)).thenReturn(product);

        mockMvc.perform(post("/products/5/favorite")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        mockMvc.perform(get("/products/favorites")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == 5)].title").value("Smartwatch"));

        mockMvc.perform(delete("/products/5/favorite")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/products/favorites")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == 5)]").isEmpty());
    }

    @Test
    void addToFavorites_whenAddedTwice_thenOnlyOneEntryExists() throws Exception {
        DummyProduct product = buildProduct(6L, "Tablet");
        when(dummyJsonClientService.getProduct(6L)).thenReturn(product);

        mockMvc.perform(post("/products/6/favorite")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        mockMvc.perform(post("/products/6/favorite")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        mockMvc.perform(get("/products/favorites")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == 6)]").isArray())
                .andExpect(jsonPath("$[?(@.id == 6)].title").value("Tablet"));

        mockMvc.perform(delete("/products/6/favorite")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
    }

    @Test
    void getProducts_whenUnauthenticated_thenReturn401() throws Exception {
        mockMvc.perform(get("/products"))
                .andExpect(status().isUnauthorized());
    }

    private DummyProduct buildProduct(Long id, String title) {
        DummyProduct product = new DummyProduct();
        product.setId(id);
        product.setTitle(title);
        product.setPrice(199.99);
        return product;
    }
}
