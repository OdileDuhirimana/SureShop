package SureShop.commerce.project.dto;

import lombok.Data;

@Data
public class PaymentConfirmationRequest {
    private Long orderId;
    private String sessionId;
    private String paymentId;
    private String transactionId;
    private String status; // e.g., success, failed
}