package lk.ijse.Apple.Vision.Mobile.Shop.DTO;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDetailDTO {
    private Long orderDetailId;
    private int quantity;
    private BigDecimal price;
    private Long variantId;
}