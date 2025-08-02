package SureShop.commerce.project.repositories;

import SureShop.commerce.project.models.Order;
import SureShop.commerce.project.models.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    List<Order> findByStatus(OrderStatus status);
    Page<Order> findAllByOrderByCreatedAtDesc(Pageable pageable);
}