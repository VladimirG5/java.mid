package hr.abysalto.hiring.mid.controller;

import hr.abysalto.hiring.mid.dto.response.DummyProduct;
import hr.abysalto.hiring.mid.dto.response.PageResponse;
import hr.abysalto.hiring.mid.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Products", description = "Product browsing and favorites")
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class ProductController {

    private final ProductService productService;

    @Operation(
            summary = "Get a product by ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Product found",
                            content = @Content(schema = @Schema(implementation = DummyProduct.class))),
                    @ApiResponse(responseCode = "404", description = "Product not found")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<DummyProduct> getProduct(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProduct(id));
    }

    @Operation(
            summary = "Get a paginated list of products",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Products retrieved successfully")
            }
    )
    @GetMapping
    public ResponseEntity<PageResponse<DummyProduct>> getProducts(
            @Parameter(description = "Zero-based page index") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Field to sort by") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort direction: asc or desc") @RequestParam(defaultValue = "asc") String order) {
        return ResponseEntity.ok(productService.getProducts(page, size, sortBy, order));
    }

    @Operation(
            summary = "Add a product to favorites",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Product added to favorites"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Product not found")
            }
    )
    @PostMapping("/{id}/favorite")
    public ResponseEntity<Void> addToFavorites(@PathVariable Long id,
                                               @AuthenticationPrincipal UserDetails userDetails) {
        productService.addToFavorites(userDetails.getUsername(), id);

        return ResponseEntity.ok()
                .build();
    }

    @Operation(
            summary = "Remove a product from favorites",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Product removed from favorites"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    @DeleteMapping("/{id}/favorite")
    public ResponseEntity<Void> removeFromFavorites(@PathVariable Long id,
                                                    @AuthenticationPrincipal UserDetails userDetails) {
        productService.removeFromFavorites(userDetails.getUsername(), id);

        return ResponseEntity.noContent()
                .build();
    }

    @Operation(
            summary = "Get all favorite products for the current user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Favorites retrieved successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    @GetMapping("/favorites")
    public ResponseEntity<List<DummyProduct>> getFavorites(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(productService.getFavorites(userDetails.getUsername()));
    }
}