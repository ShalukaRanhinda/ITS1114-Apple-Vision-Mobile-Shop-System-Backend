package lk.ijse.Apple.Vision.Mobile.Shop.Service;

import lk.ijse.Apple.Vision.Mobile.Shop.DTO.PurchaseOrderDTO;
import java.util.List;

public interface PurchaseOrderService {
    PurchaseOrderDTO placePurchaseOrder(PurchaseOrderDTO purchaseOrderDTO);
    String cancelPurchaseOrder(Long purchaseOrderId);
    List<PurchaseOrderDTO> getAllPurchaseOrders();
    PurchaseOrderDTO getPurchaseOrderById(Long purchaseOrderId);
    List<PurchaseOrderDTO> getPurchaseOrdersBySupplierId(Long supplierId);
}