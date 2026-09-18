package lk.ijse.Apple.Vision.Mobile.Shop.Repository;

import lk.ijse.Apple.Vision.Mobile.Shop.Entity.PurchaseOrder;
import lk.ijse.Apple.Vision.Mobile.Shop.Enumeration.PurchaseOrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {
    List<PurchaseOrder> findAllBySupplier_SupplierId(Long supplierId);
    List<PurchaseOrder> findAllByPurchaseOrderStatus(PurchaseOrderStatus purchaseOrderStatus);
}