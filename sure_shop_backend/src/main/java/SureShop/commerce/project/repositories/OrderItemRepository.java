package SureShop.commerce.project.repositories;

import SureShop.commerce.project.models.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}