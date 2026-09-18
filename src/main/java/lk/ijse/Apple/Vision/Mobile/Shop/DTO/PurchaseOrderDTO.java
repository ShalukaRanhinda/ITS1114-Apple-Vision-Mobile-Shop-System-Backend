package lk.ijse.Apple.Vision.Mobile.Shop.DTO;

import lk.ijse.Apple.Vision.Mobile.Shop.Enumeration.PurchaseOrderStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseOrderDTO {
    private Long purchaseOrderId;
    private LocalDateTime purchaseOrderDate;
    private PurchaseOrderStatus purchaseOrderStatus;
    private Long supplierId;
    private List<PurchaseOrderDetailsDTO> purchaseOrderDetails;
}