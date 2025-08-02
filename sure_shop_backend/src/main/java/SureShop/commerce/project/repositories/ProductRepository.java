package SureShop.commerce.project.repositories;

import SureShop.commerce.project.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    
    // Find all active products
    Page<Product> findByActiveTrue(Pageable pageable);
    
    // Search by title or description
    @Query("SELECT p FROM Product p WHERE p.active = true AND " +
           "(LOWER(p.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Product> searchProducts(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    // Filter by category
    Page<Product> findByCategoryAndActiveTrue(String category, Pageable pageable);
    
    // Filter by price range
    @Query("SELECT p FROM Product p WHERE p.active = true AND p.price BETWEEN :minPrice AND :maxPrice")
    Page<Product> findByPriceRange(@Param("minPrice") BigDecimal minPrice, 
                                  @Param("maxPrice") BigDecimal maxPrice, 
                                  Pageable pageable);
    
    // Filter by rating
    @Query("SELECT p FROM Product p WHERE p.active = true AND p.averageRating >= :minRating")
    Page<Product> findByMinRating(@Param("minRating") BigDecimal minRating, Pageable pageable);
    
    // Combined search with filters
    @Query("SELECT p FROM Product p WHERE p.active = true AND " +
           "(:category IS NULL OR p.category = :category) AND " +
           "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
           "(:maxPrice IS NULL OR p.price <= :maxPrice) AND " +
           "(:minRating IS NULL OR p.averageRating >= :minRating) AND " +
           "(:searchTerm IS NULL OR (LOWER(p.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))))")
    Page<Product> findProductsWithFilters(@Param("category") String category,
                                        @Param("minPrice") BigDecimal minPrice,
                                        @Param("maxPrice") BigDecimal maxPrice,
                                        @Param("minRating") BigDecimal minRating,
                                        @Param("searchTerm") String searchTerm,
                                        Pageable pageable);
    
    // Find top rated products
    @Query("SELECT p FROM Product p WHERE p.active = true ORDER BY p.averageRating DESC, p.totalReviews DESC")
    Page<Product> findTopRatedProducts(Pageable pageable);
    
    // Find products by stock availability
    Page<Product> findByStockQuantityGreaterThanAndActiveTrue(Integer minStock, Pageable pageable);
    
    // Get all categories
    @Query("SELECT DISTINCT p.category FROM Product p WHERE p.active = true")
    List<String> findAllCategories();
} 