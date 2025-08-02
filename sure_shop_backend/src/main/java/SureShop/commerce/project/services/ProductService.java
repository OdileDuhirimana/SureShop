package SureShop.commerce.project.services;

import SureShop.commerce.project.dto.ProductRequest;
import SureShop.commerce.project.dto.ProductResponse;
import SureShop.commerce.project.dto.ProductSearchRequest;
import SureShop.commerce.project.dto.ReviewRequest;
import SureShop.commerce.project.dto.ReviewResponse;
import SureShop.commerce.project.models.Product;
import SureShop.commerce.project.models.Review;
import SureShop.commerce.project.models.User;
import SureShop.commerce.project.repositories.ProductRepository;
import SureShop.commerce.project.repositories.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductService {
    
    private final ProductRepository productRepository;
    private final ReviewRepository reviewRepository;
    private final UserService userService;

    @Autowired
    public ProductService(ProductRepository productRepository, ReviewRepository reviewRepository, UserService userService) {
        this.productRepository = productRepository;
        this.reviewRepository = reviewRepository;
        this.userService = userService;
    }

    // Product CRUD Operations
    public ProductResponse createProduct(ProductRequest request) {
        Product product = Product.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .price(request.getPrice())
                .discount(request.getDiscount() != null ? request.getDiscount() : BigDecimal.ZERO)
                .images(request.getImages())
                .category(request.getCategory())
                .stockQuantity(request.getStockQuantity())
                .active(request.getActive() != null ? request.getActive() : true)
                .build();
        
        Product savedProduct = productRepository.save(product);
        return convertToProductResponse(savedProduct);
    }

    public ProductResponse updateProduct(Long productId, ProductRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        
        if (request.getTitle() != null) product.setTitle(request.getTitle());
        if (request.getDescription() != null) product.setDescription(request.getDescription());
        if (request.getPrice() != null) product.setPrice(request.getPrice());
        if (request.getDiscount() != null) product.setDiscount(request.getDiscount());
        if (request.getImages() != null) product.setImages(request.getImages());
        if (request.getCategory() != null) product.setCategory(request.getCategory());
        if (request.getStockQuantity() != null) product.setStockQuantity(request.getStockQuantity());
        if (request.getActive() != null) product.setActive(request.getActive());
        
        Product updatedProduct = productRepository.save(product);
        return convertToProductResponse(updatedProduct);
    }

    public ProductResponse getProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return convertToProductResponse(product);
    }

    // Internal method to get Product entity
    public Product getProductEntity(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public void deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        product.setActive(false);
        productRepository.save(product);
    }

    // Internal method to update and save a Product entity
    public void updateProduct(Product product) {
        productRepository.save(product);
    }

    // Search and Filter Operations
    public Page<ProductResponse> searchProducts(ProductSearchRequest request) {
        Pageable pageable = createPageable(request);
        
        Page<Product> products = productRepository.findProductsWithFilters(
                request.getCategory(),
                request.getMinPrice(),
                request.getMaxPrice(),
                request.getMinRating(),
                request.getSearchTerm(),
                pageable
        );
        
        return products.map(this::convertToProductResponse);
    }

    public Page<ProductResponse> getAllProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("title").ascending());
        Page<Product> products = productRepository.findByActiveTrue(pageable);
        return products.map(this::convertToProductResponse);
    }

    public Page<ProductResponse> getProductsByCategory(String category, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("title").ascending());
        Page<Product> products = productRepository.findByCategoryAndActiveTrue(category, pageable);
        return products.map(this::convertToProductResponse);
    }

    public Page<ProductResponse> getTopRatedProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productRepository.findTopRatedProducts(pageable);
        return products.map(this::convertToProductResponse);
    }

    public List<String> getAllCategories() {
        return productRepository.findAllCategories();
    }

    // Review Operations
    public ReviewResponse addReview(Long productId, String username, ReviewRequest request) {
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        
        // Check if user already reviewed this product
        Optional<Review> existingReview = reviewRepository.findByUserIdAndProductIdAndActiveTrue(user.getId(), productId);
        if (existingReview.isPresent()) {
            throw new RuntimeException("User has already reviewed this product");
        }
        
        Review review = Review.builder()
                .product(product)
                .user(user)
                .rating(request.getRating())
                .comment(request.getComment())
                .build();
        
        Review savedReview = reviewRepository.save(review);
        updateProductRating(productId);
        
        return convertToReviewResponse(savedReview);
    }

    public Page<ReviewResponse> getProductReviews(Long productId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Review> reviews = reviewRepository.findByProductIdAndActiveTrueOrderByCreatedAtDesc(productId, pageable);
        return reviews.map(this::convertToReviewResponse);
    }

    // Helper Methods
    private void updateProductRating(Long productId) {
        BigDecimal averageRating = reviewRepository.getAverageRatingByProductId(productId);
        Long totalReviews = reviewRepository.countReviewsByProductId(productId);
        
        if (averageRating != null) {
            Product product = productRepository.findById(productId).orElse(null);
            if (product != null) {
                product.setAverageRating(averageRating.setScale(2, RoundingMode.HALF_UP));
                product.setTotalReviews(totalReviews.intValue());
                productRepository.save(product);
            }
        }
    }

    private Pageable createPageable(ProductSearchRequest request) {
        Sort sort = Sort.by(
                request.getSortDirection().equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC,
                request.getSortBy()
        );
        return PageRequest.of(request.getPage(), request.getSize(), sort);
    }

    private ProductResponse convertToProductResponse(Product product) {
        BigDecimal finalPrice = product.getPrice();
        if (product.getDiscount() != null && product.getDiscount().compareTo(BigDecimal.ZERO) > 0) {
            finalPrice = product.getPrice().subtract(
                    product.getPrice().multiply(product.getDiscount()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
            );
        }
        
        return ProductResponse.builder()
                .id(product.getId())
                .title(product.getTitle())
                .description(product.getDescription())
                .price(product.getPrice())
                .discount(product.getDiscount())
                .images(product.getImages())
                .category(product.getCategory())
                .stockQuantity(product.getStockQuantity())
                .averageRating(product.getAverageRating())
                .totalReviews(product.getTotalReviews())
                .active(product.getActive())
                .finalPrice(finalPrice)
                .build();
    }

    private ReviewResponse convertToReviewResponse(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .productId(review.getProduct().getId())
                .username(review.getUser().getUsername())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build();
    }
} 