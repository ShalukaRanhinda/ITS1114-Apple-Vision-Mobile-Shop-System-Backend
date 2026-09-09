package lk.ijse.Apple.Vision.Mobile.Shop.Service;

import lk.ijse.Apple.Vision.Mobile.Shop.DTO.InventoryDTO;
import java.util.List;

public interface InventoryService {
    InventoryDTO saveInventory(InventoryDTO inventoryDTO);
    InventoryDTO updateInventory(InventoryDTO inventoryDTO);
    String deleteInventory(Long inventoryId);
    List<InventoryDTO> getAllInventories();
    InventoryDTO getInventoryById(Long inventoryId);
    InventoryDTO getInventoryByVariantId(Long variantId);
}