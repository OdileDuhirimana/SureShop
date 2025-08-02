package SureShop.commerce.project.repositories;

import SureShop.commerce.project.models.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    
    // Find cart items by cart ID
    List<CartItem> findByCartId(Long cartId);
    
    // Find cart item by cart ID and product ID
    Optional<CartItem> findByCartIdAndProductId(Long cartId, Long productId);
    
    // Delete cart items by cart ID
    void deleteByCartId(Long cartId);
    
    // Count items in a cart
    @Query("SELECT COUNT(ci) FROM CartItem ci WHERE ci.cart.id = :cartId")
    Long countByCartId(@Param("cartId") Long cartId);
    
    // Get cart items with product details
    @Query("SELECT ci FROM CartItem ci LEFT JOIN FETCH ci.product WHERE ci.cart.id = :cartId")
    List<CartItem> findByCartIdWithProduct(@Param("cartId") Long cartId);
} 