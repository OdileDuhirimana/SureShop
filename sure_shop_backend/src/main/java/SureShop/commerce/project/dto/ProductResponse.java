package SureShop.commerce.project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse {
    private Long id;
    private String title;
    private String description;
    private BigDecimal price;
    private BigDecimal discount;
    private List<String> images;
    private String category;
    private Integer stockQuantity;
    private BigDecimal averageRating;
    private Integer totalReviews;
    private Boolean active;
    private BigDecimal finalPrice; // Price after discount
} 