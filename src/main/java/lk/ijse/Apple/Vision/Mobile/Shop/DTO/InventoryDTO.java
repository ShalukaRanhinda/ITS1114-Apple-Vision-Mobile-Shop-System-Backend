package lk.ijse.Apple.Vision.Mobile.Shop.DTO;

import lk.ijse.Apple.Vision.Mobile.Shop.Enumeration.InventoryStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryDTO {
    private Long inventoryId;
    private Long variantId;
    private int quantity;
    private InventoryStatus inventoryStatus;
}