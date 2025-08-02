package SureShop.commerce.project.repositories;

import SureShop.commerce.project.models.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    
    // Find cart by user ID
    Optional<Cart> findByUserIdAndActiveTrue(Long userId);
    
    // Find cart by user ID (including inactive)
    Optional<Cart> findByUserId(Long userId);
    
    // Check if user has an active cart
    boolean existsByUserIdAndActiveTrue(Long userId);
    
    // Get cart with items and products (eager loading)
    @Query("SELECT c FROM Cart c LEFT JOIN FETCH c.items ci LEFT JOIN FETCH ci.product WHERE c.user.id = :userId AND c.active = true")
    Optional<Cart> findByUserIdWithItems(@Param("userId") Long userId);
    
    // Delete inactive carts (cleanup)
    void deleteByActiveFalse();
} 