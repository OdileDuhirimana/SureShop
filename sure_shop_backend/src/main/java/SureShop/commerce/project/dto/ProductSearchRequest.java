package SureShop.commerce.project.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductSearchRequest {
    private String searchTerm;
    private String category;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private BigDecimal minRating;
    private String sortBy = "title"; // title, price, rating, createdAt
    private String sortDirection = "asc"; // asc, desc
    private Integer page = 0;
    private Integer size = 20;
} 