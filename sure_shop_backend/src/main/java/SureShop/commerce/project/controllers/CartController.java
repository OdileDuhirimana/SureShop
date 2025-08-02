package SureShop.commerce.project.controllers;

import SureShop.commerce.project.dto.CartItemRequest;
import SureShop.commerce.project.dto.CartResponse;
import SureShop.commerce.project.services.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    
    private final CartService cartService;

    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    // Get user's cart
    @GetMapping
    public ResponseEntity<CartResponse> getUserCart(Authentication authentication) {
        try {
            String username = authentication.getName();
            CartResponse cart = cartService.getUserCart(username);
            return ResponseEntity.ok(cart);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Add item to cart
    @PostMapping("/add")
    public ResponseEntity<CartResponse> addItemToCart(
            @RequestBody CartItemRequest request,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            CartResponse cart = cartService.addItemToCart(username, request);
            return ResponseEntity.ok(cart);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // Update cart item quantity
    @PutMapping("/update/{productId}")
    public ResponseEntity<CartResponse> updateCartItem(
            @PathVariable Long productId,
            @RequestBody CartItemRequest request,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            CartResponse cart = cartService.updateCartItem(username, productId, request);
            return ResponseEntity.ok(cart);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // Remove item from cart
    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<CartResponse> removeItemFromCart(
            @PathVariable Long productId,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            CartResponse cart = cartService.removeItemFromCart(username, productId);
            return ResponseEntity.ok(cart);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // Clear cart
    @DeleteMapping("/clear")
    public ResponseEntity<CartResponse> clearCart(Authentication authentication) {
        try {
            String username = authentication.getName();
            CartResponse cart = cartService.clearCart(username);
            return ResponseEntity.ok(cart);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // Get cart item count
    @GetMapping("/count")
    public ResponseEntity<Integer> getCartItemCount(Authentication authentication) {
        try {
            String username = authentication.getName();
            Integer count = cartService.getCartItemCount(username);
            return ResponseEntity.ok(count);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(0);
        }
    }
} 