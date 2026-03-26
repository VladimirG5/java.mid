package hr.abysalto.hiring.mid.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import hr.abysalto.hiring.mid.dto.request.AddToCartRequest;
import hr.abysalto.hiring.mid.dto.request.LoginRequest;
import hr.abysalto.hiring.mid.dto.request.RegisterRequest;
import hr.abysalto.hiring.mid.dto.response.DummyProduct;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CartIntegrationTest {

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
                .username("cart_user")
                .email("cart_user@example.com")
                .password("secret123")
                .build();

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isOk());

        LoginRequest login = LoginRequest.builder()
                .username("cart_user")
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
    void getCart_whenEmpty_thenReturnEmptyCart() throws Exception {
        mockMvc.perform(get("/cart")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalItems").value(0))
                .andExpect(jsonPath("$.items").isEmpty());
    }

    @Test
    void addToCart_thenGetCart_thenRemove_fullFlow() throws Exception {
        DummyProduct product = new DummyProduct();
        product.setId(10L);
        product.setTitle("Laptop");
        product.setPrice(999.99);
        when(dummyJsonClientService.getProduct(10L)).thenReturn(product);

        AddToCartRequest addRequest = new AddToCartRequest();
        addRequest.setProductId(10L);
        addRequest.setQuantity(2);

        mockMvc.perform(post("/cart/items")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalItems").value(1))
                .andExpect(jsonPath("$.items[0].productId").value(10))
                .andExpect(jsonPath("$.items[0].quantity").value(2));

        mockMvc.perform(get("/cart")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalItems").value(1));

        mockMvc.perform(delete("/cart/items/10")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalItems").value(0));
    }

    @Test
    void addToCart_whenSameProductAddedTwice_thenQuantityIsIncremented() throws Exception {
        DummyProduct product = new DummyProduct();
        product.setId(20L);
        product.setTitle("Headphones");
        when(dummyJsonClientService.getProduct(20L)).thenReturn(product);

        AddToCartRequest firstAdd = new AddToCartRequest();
        firstAdd.setProductId(20L);
        firstAdd.setQuantity(1);

        mockMvc.perform(post("/cart/items")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(firstAdd)))
                .andExpect(status().isOk());

        AddToCartRequest secondAdd = new AddToCartRequest();
        secondAdd.setProductId(20L);
        secondAdd.setQuantity(3);

        mockMvc.perform(post("/cart/items")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(secondAdd)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[?(@.productId == 20)].quantity").value(4));

        mockMvc.perform(delete("/cart/items/20")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void decreaseCartItemQuantity_whenQuantityIsAboveOne_thenDecrement() throws Exception {
        DummyProduct product = new DummyProduct();
        product.setId(30L);
        product.setTitle("Keyboard");
        when(dummyJsonClientService.getProduct(30L)).thenReturn(product);

        AddToCartRequest addRequest = new AddToCartRequest();
        addRequest.setProductId(30L);
        addRequest.setQuantity(3);

        mockMvc.perform(post("/cart/items")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addRequest)))
                .andExpect(status().isOk());

        mockMvc.perform(patch("/cart/items/30/decrease")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[?(@.productId == 30)].quantity").value(2));

        mockMvc.perform(delete("/cart/items/30")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void decreaseCartItemQuantity_whenQuantityIsOne_thenRemoveItemFromCart() throws Exception {
        DummyProduct product = new DummyProduct();
        product.setId(31L);
        product.setTitle("Mouse");
        when(dummyJsonClientService.getProduct(31L)).thenReturn(product);

        AddToCartRequest addRequest = new AddToCartRequest();
        addRequest.setProductId(31L);
        addRequest.setQuantity(1);

        mockMvc.perform(post("/cart/items")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalItems").value(1));

        mockMvc.perform(patch("/cart/items/31/decrease")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[?(@.productId == 31)]").isEmpty());
    }

    @Test
    void decreaseCartItemQuantity_whenProductNotInCart_thenReturn404() throws Exception {
        mockMvc.perform(patch("/cart/items/99999/decrease")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void addToCart_whenQuantityIsZero_thenReturn400() throws Exception {
        AddToCartRequest request = new AddToCartRequest();
        request.setProductId(10L);
        request.setQuantity(0);

        mockMvc.perform(post("/cart/items")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getCart_whenUnauthenticated_thenReturn401() throws Exception {
        mockMvc.perform(get("/cart"))
                .andExpect(status().isUnauthorized());
    }
}
