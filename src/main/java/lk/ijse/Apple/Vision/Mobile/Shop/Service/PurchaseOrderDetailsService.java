package lk.ijse.Apple.Vision.Mobile.Shop.Service;

import lk.ijse.Apple.Vision.Mobile.Shop.DTO.PurchaseOrderDetailsDTO;
import java.util.List;

public interface PurchaseOrderDetailsService {
    PurchaseOrderDetailsDTO savePurchaseOrderDetail(Long purchaseOrderId, PurchaseOrderDetailsDTO dto);
    PurchaseOrderDetailsDTO updatePurchaseOrderDetail(PurchaseOrderDetailsDTO dto);
    String deletePurchaseOrderDetail(Long poDetailsId);
    List<PurchaseOrderDetailsDTO> getAllPurchaseOrderDetails();
    PurchaseOrderDetailsDTO getPurchaseOrderDetailById(Long poDetailsId);
    List<PurchaseOrderDetailsDTO> getDetailsByPurchaseOrderId(Long purchaseOrderId);
}