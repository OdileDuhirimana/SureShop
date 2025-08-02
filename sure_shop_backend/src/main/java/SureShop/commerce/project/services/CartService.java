package SureShop.commerce.project.services;

import SureShop.commerce.project.dto.CartItemRequest;
import SureShop.commerce.project.dto.CartItemResponse;
import SureShop.commerce.project.dto.CartResponse;
import SureShop.commerce.project.models.Cart;
import SureShop.commerce.project.models.CartItem;
import SureShop.commerce.project.models.Product;
import SureShop.commerce.project.models.User;
import SureShop.commerce.project.repositories.CartItemRepository;
import SureShop.commerce.project.repositories.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CartService {
    
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductService productService;
    private final UserService userService;

    @Autowired
    public CartService(CartRepository cartRepository, CartItemRepository cartItemRepository, 
                      ProductService productService, UserService userService) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productService = productService;
        this.userService = userService;
    }

    // Get user's cart
    public CartResponse getUserCart(String username) {
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Cart cart = cartRepository.findByUserIdWithItems(user.getId())
                .orElseGet(() -> createNewCart(user));
        
        return convertToCartResponse(cart);
    }

    // Add item to cart
    public CartResponse addItemToCart(String username, CartItemRequest request) {
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Product product = productService.getProductEntity(request.getProductId());
        validateProduct(product);
        
        Cart cart = getOrCreateCart(user);
        CartItem existingItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), request.getProductId())
                .orElse(null);
        
        if (existingItem != null) {
            // Update existing item quantity
            int newQuantity = existingItem.getQuantity() + request.getQuantity();
            validateQuantity(newQuantity, product.getStockQuantity());
            existingItem.updateQuantity(newQuantity);
            cartItemRepository.save(existingItem);
        } else {
            // Add new item
            validateQuantity(request.getQuantity(), product.getStockQuantity());
            CartItem newItem = createCartItem(cart, product, request.getQuantity());
            cart.addItem(newItem);
            cartItemRepository.save(newItem);
        }
        
        cartRepository.save(cart);
        return convertToCartResponse(cart);
    }

    // Update cart item quantity
    public CartResponse updateCartItem(String username, Long productId, CartItemRequest request) {
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Cart cart = cartRepository.findByUserIdAndActiveTrue(user.getId())
                .orElseThrow(() -> new RuntimeException("Cart not found"));
        
        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));
        
        Product product = cartItem.getProduct();
        validateQuantity(request.getQuantity(), product.getStockQuantity());
        
        cartItem.updateQuantity(request.getQuantity());
        cartItemRepository.save(cartItem);
        
        return convertToCartResponse(cart);
    }

    // Remove item from cart
    public CartResponse removeItemFromCart(String username, Long productId) {
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Cart cart = cartRepository.findByUserIdAndActiveTrue(user.getId())
                .orElseThrow(() -> new RuntimeException("Cart not found"));
        
        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));
        
        cart.removeItem(cartItem);
        cartItemRepository.delete(cartItem);
        cartRepository.save(cart);
        
        return convertToCartResponse(cart);
    }

    // Clear cart
    public CartResponse clearCart(String username) {
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Cart cart = cartRepository.findByUserIdAndActiveTrue(user.getId())
                .orElseThrow(() -> new RuntimeException("Cart not found"));
        
        cart.clearItems();
        cartItemRepository.deleteByCartId(cart.getId());
        cartRepository.save(cart);
        
        return convertToCartResponse(cart);
    }

    // Get cart item count
    public Integer getCartItemCount(String username) {
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return cartRepository.findByUserIdAndActiveTrue(user.getId())
                .map(cart -> cart.getTotalItems())
                .orElse(0);
    }

    // Helper methods
    private Cart getOrCreateCart(User user) {
        return cartRepository.findByUserIdAndActiveTrue(user.getId())
                .orElseGet(() -> createNewCart(user));
    }

    private Cart createNewCart(User user) {
        Cart cart = Cart.builder()
                .user(user)
                .build();
        return cartRepository.save(cart);
    }

    private CartItem createCartItem(Cart cart, Product product, Integer quantity) {
        BigDecimal unitPrice = calculateFinalPrice(product);
        
        CartItem cartItem = CartItem.builder()
                .cart(cart)
                .product(product)
                .quantity(quantity)
                .unitPrice(unitPrice)
                .build();
        
        cartItem.calculateTotalPrice();
        return cartItem;
    }

    private BigDecimal calculateFinalPrice(Product product) {
        BigDecimal finalPrice = product.getPrice();
        if (product.getDiscount() != null && product.getDiscount().compareTo(BigDecimal.ZERO) > 0) {
            finalPrice = product.getPrice().subtract(
                    product.getPrice().multiply(product.getDiscount())
                            .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
            );
        }
        return finalPrice;
    }

    private void validateProduct(Product product) {
        if (!product.getActive()) {
            throw new RuntimeException("Product is not available");
        }
        if (product.getStockQuantity() <= 0) {
            throw new RuntimeException("Product is out of stock");
        }
    }

    private void validateQuantity(Integer quantity, Integer availableStock) {
        if (quantity <= 0) {
            throw new RuntimeException("Quantity must be greater than 0");
        }
        if (quantity > availableStock) {
            throw new RuntimeException("Requested quantity exceeds available stock");
        }
    }

    private CartResponse convertToCartResponse(Cart cart) {
        List<CartItemResponse> itemResponses = cart.getItems().stream()
                .map(this::convertToCartItemResponse)
                .collect(Collectors.toList());
        
        return CartResponse.builder()
                .id(cart.getId())
                .userId(cart.getUser().getId())
                .items(itemResponses)
                .totalItems(cart.getTotalItems())
                .totalPrice(cart.getTotalPrice())
                .createdAt(cart.getCreatedAt())
                .updatedAt(cart.getUpdatedAt())
                .build();
    }

    private CartItemResponse convertToCartItemResponse(CartItem cartItem) {
        Product product = cartItem.getProduct();
        String productImage = product.getImages() != null && !product.getImages().isEmpty() 
                ? product.getImages().get(0) : null;
        
        return CartItemResponse.builder()
                .id(cartItem.getId())
                .productId(product.getId())
                .productTitle(product.getTitle())
                .productImage(productImage)
                .quantity(cartItem.getQuantity())
                .unitPrice(cartItem.getUnitPrice())
                .totalPrice(cartItem.getTotalPrice())
                .discount(product.getDiscount())
                .finalUnitPrice(cartItem.getUnitPrice())
                .build();
    }
} 