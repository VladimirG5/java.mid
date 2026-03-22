package hr.abysalto.hiring.mid.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hr.abysalto.hiring.mid.dto.response.DummyProduct;
import hr.abysalto.hiring.mid.dto.response.PageResponse;
import hr.abysalto.hiring.mid.security.JwtUtil;
import hr.abysalto.hiring.mid.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    ProductService productService;

    @MockitoBean
    JwtUtil jwtUtil;

    @MockitoBean
    UserDetailsService userDetailsService;

    @Test
    @WithMockUser(username = "john")
    void getProducts_whenRequestIsValid_thenReturnPaginatedList() throws Exception {
        DummyProduct p = new DummyProduct();
        p.setId(1L);
        p.setTitle("Phone");

        PageResponse<DummyProduct> pageResponse = PageResponse.<DummyProduct>builder()
                .content(List.of(p)).page(0).size(10).total(1).totalPages(1).build();

        when(productService.getProducts(0, 10, "id", "asc")).thenReturn(pageResponse);

        mockMvc.perform(get("/products")
                        .param("page", "0").param("size", "10")
                        .param("sortBy", "id").param("order", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Phone"))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.total").value(1));
    }

    @Test
    @WithMockUser(username = "john")
    void getProduct_whenRequestIsValid_thenReturnProduct() throws Exception {
        DummyProduct p = new DummyProduct();
        p.setId(1L);
        p.setTitle("Phone");

        when(productService.getProduct(1L)).thenReturn(p);

        mockMvc.perform(get("/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Phone"));
    }

    @Test
    @WithMockUser(username = "john")
    void addToFavorites_whenRequestIsValid_thenReturn200() throws Exception {
        doNothing().when(productService).addToFavorites("john", 1L);

        mockMvc.perform(post("/products/1/favorite").with(csrf()))
                .andExpect(status().isOk());

        verify(productService).addToFavorites("john", 1L);
    }

    @Test
    @WithMockUser(username = "john")
    void removeFromFavorites_whenRequestIsValid_thenReturn204() throws Exception {
        doNothing().when(productService).removeFromFavorites("john", 1L);

        mockMvc.perform(delete("/products/1/favorite").with(csrf()))
                .andExpect(status().isNoContent());

        verify(productService).removeFromFavorites("john", 1L);
    }

    @Test
    @WithMockUser(username = "john")
    void getFavorites_whenRequestIsValid_thenReturnList() throws Exception {
        DummyProduct p = new DummyProduct();
        p.setId(1L);
        p.setTitle("Phone");

        when(productService.getFavorites("john")).thenReturn(List.of(p));

        mockMvc.perform(get("/products/favorites"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Phone"));
    }

    @Test
    void getProducts_whenRequestIsUnauthorized_thenReturn401() throws Exception {
        mockMvc.perform(get("/products"))
                .andExpect(status().isUnauthorized());
    }
}