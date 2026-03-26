package hr.abysalto.hiring.mid.service.impl;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import hr.abysalto.hiring.mid.dto.response.DummyProduct;
import hr.abysalto.hiring.mid.dto.response.DummyProductsResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.web.reactive.function.client.WebClient;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;

class DummyJsonClientImplTest {

    @RegisterExtension
    static WireMockExtension wiremock = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .build();

    private DummyJsonClientImpl client;

    @BeforeEach
    void setUp() {
        WebClient webClient = WebClient.builder()
                .baseUrl(wiremock.baseUrl())
                .build();
        client = new DummyJsonClientImpl(webClient);
    }

    @Test
    void getProduct_whenProductExists_thenReturnParsedProduct() {
        wiremock.stubFor(get("/products/1")
                .willReturn(okJson("""
                        {
                          "id": 1,
                          "title": "iPhone 9",
                          "description": "An apple mobile which is nothing like apple",
                          "category": "smartphones",
                          "price": 549.99,
                          "rating": 4.69,
                          "stock": 94,
                          "brand": "Apple",
                          "thumbnail": "https://cdn.dummyjson.com/product-images/1/thumbnail.jpg"
                        }
                        """)));

        DummyProduct product = client.getProduct(1L);

        assertThat(product.getId()).isEqualTo(1L);
        assertThat(product.getTitle()).isEqualTo("iPhone 9");
        assertThat(product.getPrice()).isEqualTo(549.99);
        assertThat(product.getBrand()).isEqualTo("Apple");

        wiremock.verify(getRequestedFor(urlEqualTo("/products/1")));
    }

    @Test
    void getProduct_whenResponseHasUnknownFields_thenIgnoreThem() {
        wiremock.stubFor(get("/products/2")
                .willReturn(okJson("""
                        {
                          "id": 2,
                          "title": "Unknown Fields Product",
                          "unknownField": "should be ignored",
                          "anotherUnknown": 12345
                        }
                        """)));

        DummyProduct product = client.getProduct(2L);

        assertThat(product.getId()).isEqualTo(2L);
        assertThat(product.getTitle()).isEqualTo("Unknown Fields Product");
    }

    @Test
    void getProducts_whenValidParams_thenReturnParsedPage() {
        wiremock.stubFor(get(urlPathEqualTo("/products"))
                .withQueryParam("limit", equalTo("10"))
                .withQueryParam("skip", equalTo("0"))
                .withQueryParam("sortBy", equalTo("id"))
                .withQueryParam("order", equalTo("asc"))
                .willReturn(okJson("""
                        {
                          "products": [
                            {"id": 1, "title": "iPhone 9", "price": 549.99},
                            {"id": 2, "title": "iPhone X", "price": 899.99}
                          ],
                          "total": 100,
                          "skip": 0,
                          "limit": 10
                        }
                        """)));

        DummyProductsResponse response = client.getProducts(0, 10, "id", "asc");

        assertThat(response.getProducts()).hasSize(2);
        assertThat(response.getTotal()).isEqualTo(100);
        assertThat(response.getProducts().get(0).getTitle()).isEqualTo("iPhone 9");
        assertThat(response.getProducts().get(1).getTitle()).isEqualTo("iPhone X");
    }

    @Test
    void getProducts_whenPageIsTwo_thenSkipIsCalculatedCorrectly() {
        wiremock.stubFor(get(urlPathEqualTo("/products"))
                .withQueryParam("limit", equalTo("5"))
                .withQueryParam("skip", equalTo("10"))
                .willReturn(okJson("""
                        {
                          "products": [],
                          "total": 100,
                          "skip": 10,
                          "limit": 5
                        }
                        """)));

        DummyProductsResponse response = client.getProducts(2, 5, "id", "asc");

        assertThat(response.getSkip()).isEqualTo(10);
        wiremock.verify(getRequestedFor(urlPathEqualTo("/products"))
                .withQueryParam("skip", equalTo("10")));
    }
}
