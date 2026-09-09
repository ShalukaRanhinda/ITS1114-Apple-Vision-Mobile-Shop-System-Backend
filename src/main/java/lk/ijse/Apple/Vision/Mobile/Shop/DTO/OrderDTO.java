package lk.ijse.Apple.Vision.Mobile.Shop.DTO;

import lk.ijse.Apple.Vision.Mobile.Shop.Enumeration.OrderStatus;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDTO {
    private Long orderId;
    private BigDecimal totalAmount;
    private LocalDateTime orderDate;
    private OrderStatus orderStatus;
    private Long customerId;
    private List<OrderDetailDTO> orderDetails;
}