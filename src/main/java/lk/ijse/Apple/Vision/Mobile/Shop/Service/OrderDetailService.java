package lk.ijse.Apple.Vision.Mobile.Shop.Service;

import lk.ijse.Apple.Vision.Mobile.Shop.DTO.OrderDetailDTO;
import java.util.List;

public interface OrderDetailService {
    OrderDetailDTO saveOrderDetail(Long orderId, OrderDetailDTO orderDetailDTO);
    OrderDetailDTO updateOrderDetail(OrderDetailDTO orderDetailDTO);
    String deleteOrderDetail(Long orderDetailId);
    List<OrderDetailDTO> getAllOrderDetails();
    OrderDetailDTO getOrderDetailById(Long orderDetailId);
    List<OrderDetailDTO> getOrderDetailsByOrderId(Long orderId);
}