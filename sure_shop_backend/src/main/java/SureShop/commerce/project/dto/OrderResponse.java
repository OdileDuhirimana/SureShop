package SureShop.commerce.project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {
    private Long id;
    private Long userId;
    private List<OrderItemResponse> items;
    private String status;
    private BigDecimal totalPrice;
    private String shippingAddress;
    private String paymentId;
    private String transactionId;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}