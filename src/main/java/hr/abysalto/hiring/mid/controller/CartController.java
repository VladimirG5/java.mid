package hr.abysalto.hiring.mid.controller;

import hr.abysalto.hiring.mid.dto.request.AddToCartRequest;
import hr.abysalto.hiring.mid.dto.response.CartResponse;
import hr.abysalto.hiring.mid.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Cart", description = "Shopping cart management")
@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class CartController {

    private final CartService cartService;

    @Operation(
            summary = "Get current user's cart",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Cart retrieved successfully",
                            content = @Content(schema = @Schema(implementation = CartResponse.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    @GetMapping
    public ResponseEntity<CartResponse> getCart(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(cartService.getCart(userDetails.getUsername()));
    }

    @Operation(
            summary = "Add a product to the cart",
            description = "Adds the specified product to the cart. If the product already exists, its quantity is incremented.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Product added, updated cart returned",
                            content = @Content(schema = @Schema(implementation = CartResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request body"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    @PostMapping("/items")
    public ResponseEntity<CartResponse> addToCart(@AuthenticationPrincipal UserDetails userDetails,
                                                  @Valid @RequestBody AddToCartRequest request) {
        return ResponseEntity.ok(cartService.addToCart(userDetails.getUsername(), request));
    }

    @Operation(
            summary = "Decrease product quantity in the cart by one",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Quantity decreased, updated cart returned",
                            content = @Content(schema = @Schema(implementation = CartResponse.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Product not in cart"),
                    @ApiResponse(responseCode = "422", description = "Cannot decrease quantity below zero")
            }
    )
    @PatchMapping("/items/{productId}/decrease")
    public ResponseEntity<CartResponse> decreaseCartItemQuantity(@AuthenticationPrincipal UserDetails userDetails,
                                                                 @PathVariable Long productId) {
        return ResponseEntity.ok(cartService.decreaseCartItemQuantity(userDetails.getUsername(), productId));
    }

    @Operation(
            summary = "Remove a product from the cart",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Product removed, updated cart returned",
                            content = @Content(schema = @Schema(implementation = CartResponse.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    @DeleteMapping("/items/{productId}")
    public ResponseEntity<CartResponse> removeFromCart(@AuthenticationPrincipal UserDetails userDetails,
                                                       @PathVariable Long productId) {
        return ResponseEntity.ok(cartService.removeFromCart(userDetails.getUsername(), productId));
    }
}