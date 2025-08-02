package SureShop.commerce.project.controllers;

import SureShop.commerce.project.dto.PaymentRequest;
import SureShop.commerce.project.dto.PaymentResponse;
import SureShop.commerce.project.dto.PaymentConfirmationRequest;
import SureShop.commerce.project.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/create-session")
    public ResponseEntity<PaymentResponse> createSession(@RequestBody PaymentRequest request, Authentication authentication) {
        try {
            // Optionally, check user owns the order
            PaymentResponse response = paymentService.createSession(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/confirm")
    public ResponseEntity<PaymentResponse> confirmPayment(@RequestBody PaymentConfirmationRequest request, Authentication authentication) {
        try {
            PaymentResponse response = paymentService.confirmPayment(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}