package SureShop.commerce.project.services;

import SureShop.commerce.project.dto.*;
import SureShop.commerce.project.models.*;
import SureShop.commerce.project.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserService userService;
    private final ProductService productService;

    @Autowired
    public OrderService(OrderRepository orderRepository,
                        OrderItemRepository orderItemRepository,
                        CartRepository cartRepository,
                        CartItemRepository cartItemRepository,
                        UserService userService,
                        ProductService productService) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.userService = userService;
        this.productService = productService;
    }

    // Checkout: create order from cart
    public OrderResponse checkout(String username, OrderRequest request) {
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Cart cart = cartRepository.findByUserIdWithItems(user.getId())
                .orElseThrow(() -> new RuntimeException("Cart not found"));
        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }
        // Validate stock
        for (CartItem item : cart.getItems()) {
            Product product = item.getProduct();
            if (!product.getActive() || product.getStockQuantity() < item.getQuantity()) {
                throw new RuntimeException("Product out of stock or unavailable: " + product.getTitle());
            }
        }
        // Create order
        Order order = Order.builder()
                .user(user)
                .status(OrderStatus.PENDING)
                .shippingAddress(request.getShippingAddress())
                .notes(request.getNotes())
                .totalPrice(cart.getTotalPrice())
                .build();
        order = orderRepository.save(order);
        // Create order items and update stock
        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(cartItem.getQuantity())
                    .unitPrice(cartItem.getUnitPrice())
                    .totalPrice(cartItem.getTotalPrice())
                    .build();
            orderItemRepository.save(orderItem);
            // Update product stock
            product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
            productService.updateProduct(product);
        }
        // Clear cart
        cart.clearItems();
        cartItemRepository.deleteByCartId(cart.getId());
        cartRepository.save(cart);
        // Reload order with items
        order = orderRepository.findById(order.getId()).orElseThrow();
        return convertToOrderResponse(order);
    }

    // Cancel order (if PENDING)
    public OrderResponse cancelOrder(String username, Long orderId) {
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        if (!order.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new RuntimeException("Order cannot be cancelled");
        }
        order.setStatus(OrderStatus.CANCELLED);
        order.updateTimestamp();
        orderRepository.save(order);
        // Optionally, restock products
        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
            productService.updateProduct(product);
        }
        return convertToOrderResponse(order);
    }

    // User order history
    public Page<OrderResponse> getUserOrders(String username, int page, int size) {
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Pageable pageable = PageRequest.of(page, size);
        Page<Order> orders = orderRepository.findByUserIdOrderByCreatedAtDesc(user.getId(), pageable);
        return orders.map(this::convertToOrderResponse);
    }

    // Admin: all orders
    public Page<OrderResponse> getAllOrders(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Order> orders = orderRepository.findAllByOrderByCreatedAtDesc(pageable);
        return orders.map(this::convertToOrderResponse);
    }

    // Admin: update order status
    public OrderResponse updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(status);
        order.updateTimestamp();
        orderRepository.save(order);
        return convertToOrderResponse(order);
    }

    // Helper methods
    private OrderResponse convertToOrderResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(this::convertToOrderItemResponse)
                .collect(Collectors.toList());
        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUser().getId())
                .items(itemResponses)
                .status(order.getStatus().name())
                .totalPrice(order.getTotalPrice())
                .shippingAddress(order.getShippingAddress())
                .paymentId(order.getPaymentId())
                .transactionId(order.getTransactionId())
                .notes(order.getNotes())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }
    private OrderItemResponse convertToOrderItemResponse(OrderItem item) {
        Product product = item.getProduct();
        String productImage = product.getImages() != null && !product.getImages().isEmpty() ? product.getImages().get(0) : null;
        return OrderItemResponse.builder()
                .id(item.getId())
                .productId(product.getId())
                .productTitle(product.getTitle())
                .productImage(productImage)
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .totalPrice(item.getTotalPrice())
                .build();
    }
}