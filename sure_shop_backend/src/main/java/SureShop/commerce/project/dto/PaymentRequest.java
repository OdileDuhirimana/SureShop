package SureShop.commerce.project.dto;

import lombok.Data;

@Data
public class PaymentRequest {
    private Long orderId;
    private String currency;
    private String paymentMethod; // e.g., card, bank_transfer (for simulation)
}