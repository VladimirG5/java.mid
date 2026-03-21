package hr.abysalto.hiring.mid.controller;

import hr.abysalto.hiring.mid.dto.response.DummyProduct;
import hr.abysalto.hiring.mid.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/{id}")
    public ResponseEntity<DummyProduct> getProduct(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProduct(id));
    }

    @PostMapping("/{id}/favorite")
    public ResponseEntity<Void> addToFavorites(@PathVariable Long id,
                                               @AuthenticationPrincipal UserDetails userDetails) {
        productService.addToFavorites(userDetails.getUsername(), id);

        return ResponseEntity.ok()
                .build();
    }

    @DeleteMapping("/{id}/favorite")
    public ResponseEntity<Void> removeFromFavorites(@PathVariable Long id,
                                                    @AuthenticationPrincipal UserDetails userDetails) {
        productService.removeFromFavorites(userDetails.getUsername(), id);

        return ResponseEntity.noContent()
                .build();
    }

    @GetMapping("/favorites")
    public ResponseEntity<List<DummyProduct>> getFavorites(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(productService.getFavorites(userDetails.getUsername()));
    }
}
