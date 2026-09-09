package lk.ijse.Apple.Vision.Mobile.Shop.Repository;

import lk.ijse.Apple.Vision.Mobile.Shop.Entity.Order;
import lk.ijse.Apple.Vision.Mobile.Shop.Enumeration.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByOrderStatus(OrderStatus orderStatus);
    List<Order> findAllByCustomer_CustomerId(Long customerId);
    List<Order> findAllByOrderDateBetween(LocalDateTime startDate, LocalDateTime endDate);
}