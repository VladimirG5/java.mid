package hr.abysalto.hiring.mid.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hr.abysalto.hiring.mid.dto.request.AddToCartRequest;
import hr.abysalto.hiring.mid.dto.response.CartItemResponse;
import hr.abysalto.hiring.mid.dto.response.CartResponse;
import hr.abysalto.hiring.mid.dto.response.DummyProduct;
import hr.abysalto.hiring.mid.security.JwtUtil;
import hr.abysalto.hiring.mid.service.CartService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import hr.abysalto.hiring.mid.exception.CartItemQuantityException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CartController.class)
class CartControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    CartService cartService;

    @MockitoBean
    JwtUtil jwtUtil;

    @MockitoBean
    UserDetailsService userDetailsService;

    @Test
    @WithMockUser(username = "john")
    void getCart_whenRequestIsAuthenticated_thenReturnCart() throws Exception {
        DummyProduct product = new DummyProduct();
        product.setId(10L);
        product.setTitle("Phone");

        CartItemResponse itemResponse = CartItemResponse.builder()
                .productId(10L).quantity(2).product(product).build();
        CartResponse cartResponse = CartResponse.builder()
                .items(List.of(itemResponse)).totalItems(1).build();

        when(cartService.getCart("john")).thenReturn(cartResponse);

        mockMvc.perform(get("/cart"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalItems").value(1))
                .andExpect(jsonPath("$.items[0].productId").value(10));
    }

    @Test
    void getCart_whenRequestIsUnauthorized_thenReturn401() throws Exception {
        mockMvc.perform(get("/cart"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "john")
    void addToCart_whenRequestIsValid_thenReturnUpdatedCart() throws Exception {
        AddToCartRequest request = new AddToCartRequest();
        request.setProductId(10L);
        request.setQuantity(2);

        CartResponse cartResponse = CartResponse.builder()
                .items(List.of()).totalItems(1).build();

        when(cartService.addToCart(eq("john"), any())).thenReturn(cartResponse);

        mockMvc.perform(post("/cart/items")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalItems").value(1));
    }

    @Test
    @WithMockUser(username = "john")
    void addToCart_whenQuantityIsZero_thenReturn400() throws Exception {
        AddToCartRequest request = new AddToCartRequest();
        request.setProductId(10L);
        request.setQuantity(0);

        mockMvc.perform(post("/cart/items")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }


    @Test
    @WithMockUser(username = "john")
    void decreaseCartItemQuantity_whenRequestIsValid_thenReturnUpdatedCart() throws Exception {
        CartResponse cartResponse = CartResponse.builder()
                .items(List.of()).totalItems(0).build();

        when(cartService.decreaseCartItemQuantity("john", 10L)).thenReturn(cartResponse);

        mockMvc.perform(patch("/cart/items/10/decrease")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalItems").value(0));
    }

    @Test
    @WithMockUser(username = "john")
    void decreaseCartItemQuantity_whenQuantityIsZero_thenReturn422() throws Exception {
        when(cartService.decreaseCartItemQuantity("john", 10L))
                .thenThrow(new CartItemQuantityException(10L));

        mockMvc.perform(patch("/cart/items/10/decrease")
                        .with(csrf()))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("Cannot decrease quantity below zero for product id: 10"));
    }

    @Test
    @WithMockUser(username = "john")
    void removeFromCart_whenRequestIsValid_thenReturnUpdatedCart() throws Exception {
        CartResponse cartResponse = CartResponse.builder()
                .items(List.of()).totalItems(0).build();

        when(cartService.removeFromCart("john", 10L)).thenReturn(cartResponse);

        mockMvc.perform(delete("/cart/items/10")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalItems").value(0));
    }
}