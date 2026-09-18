package lk.ijse.Apple.Vision.Mobile.Shop.Repository;

import lk.ijse.Apple.Vision.Mobile.Shop.Entity.PurchaseOrderDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseOrderDetailsRepository extends JpaRepository<PurchaseOrderDetails, Long> {
    List<PurchaseOrderDetails> findAllByPurchaseOrder_PurchaseOrderId(Long purchaseOrderId);
}