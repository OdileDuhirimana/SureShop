package SureShop.commerce.project.repositories;

import SureShop.commerce.project.models.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    
    // Find reviews by product
    Page<Review> findByProductIdAndActiveTrueOrderByCreatedAtDesc(Long productId, Pageable pageable);
    
    // Find review by user and product (to check if user already reviewed)
    Optional<Review> findByUserIdAndProductIdAndActiveTrue(Long userId, Long productId);
    
    // Calculate average rating for a product
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product.id = :productId AND r.active = true")
    BigDecimal getAverageRatingByProductId(@Param("productId") Long productId);
    
    // Count reviews for a product
    @Query("SELECT COUNT(r) FROM Review r WHERE r.product.id = :productId AND r.active = true")
    Long countReviewsByProductId(@Param("productId") Long productId);
    
    // Find reviews by user
    Page<Review> findByUserIdAndActiveTrueOrderByCreatedAtDesc(Long userId, Pageable pageable);
    
    // Find reviews by rating range
    @Query("SELECT r FROM Review r WHERE r.product.id = :productId AND r.rating >= :minRating AND r.active = true")
    List<Review> findByProductIdAndMinRating(@Param("productId") Long productId, @Param("minRating") BigDecimal minRating);
} 