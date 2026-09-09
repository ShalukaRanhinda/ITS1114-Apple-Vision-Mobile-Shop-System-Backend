package lk.ijse.Apple.Vision.Mobile.Shop.Service;

import lk.ijse.Apple.Vision.Mobile.Shop.DTO.OrderDTO;
import java.util.List;

public interface OrderService {
    OrderDTO placeOrder(OrderDTO orderDTO);
    String cancelOrder(Long orderId);
    List<OrderDTO> getAllOrders();
    OrderDTO getOrderById(Long orderId);
    List<OrderDTO> getOrdersByCustomerId(Long customerId);
}