package SureShop.commerce.project.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductRequest {
    private String title;
    private String description;
    private BigDecimal price;
    private BigDecimal discount;
    private List<String> images;
    private String category;
    private Integer stockQuantity;
    private Boolean active;
} 