package SureShop.commerce.project.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ReviewRequest {
    private BigDecimal rating;
    private String comment;
} 