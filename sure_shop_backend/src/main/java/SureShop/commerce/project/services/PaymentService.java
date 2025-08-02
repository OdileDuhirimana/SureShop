package SureShop.commerce.project.services;

import SureShop.commerce.project.dto.PaymentRequest;
import SureShop.commerce.project.dto.PaymentResponse;
import SureShop.commerce.project.dto.PaymentConfirmationRequest;
import SureShop.commerce.project.models.Order;
import SureShop.commerce.project.models.OrderStatus;
import SureShop.commerce.project.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class PaymentService {
    private final OrderRepository orderRepository;

    @Autowired
    public PaymentService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    // Simulate Stripe session creation
    public PaymentResponse createSession(PaymentRequest request) {
        // Validate order
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new RuntimeException("Order is not in a payable state");
        }
        // Simulate session
        String sessionId = UUID.randomUUID().toString();
        String paymentUrl = "https://simulated-stripe.com/pay/" + sessionId;
        return PaymentResponse.builder()
                .sessionId(sessionId)
                .paymentUrl(paymentUrl)
                .status("created")
                .build();
    }

    // Simulate payment confirmation
    public PaymentResponse confirmPayment(PaymentConfirmationRequest request) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));
        if (!"success".equalsIgnoreCase(request.getStatus())) {
            return PaymentResponse.builder()
                    .sessionId(request.getSessionId())
                    .status("failed")
                    .build();
        }
        // Update order with payment info
        order.setPaymentId(request.getPaymentId());
        order.setTransactionId(request.getTransactionId());
        order.setStatus(OrderStatus.CONFIRMED);
        order.updateTimestamp();
        orderRepository.save(order);
        return PaymentResponse.builder()
                .sessionId(request.getSessionId())
                .status("confirmed")
                .build();
    }
}