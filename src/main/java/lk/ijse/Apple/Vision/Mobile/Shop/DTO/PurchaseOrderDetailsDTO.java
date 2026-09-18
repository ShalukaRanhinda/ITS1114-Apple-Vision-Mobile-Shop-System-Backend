package lk.ijse.Apple.Vision.Mobile.Shop.DTO;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseOrderDetailsDTO {
    private Long poDetailsId;
    private int quantity;
    private BigDecimal unitPrice;
    private Long variantId;
}