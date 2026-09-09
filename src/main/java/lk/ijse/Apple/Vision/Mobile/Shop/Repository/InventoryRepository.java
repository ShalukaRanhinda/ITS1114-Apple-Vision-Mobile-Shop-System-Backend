package lk.ijse.Apple.Vision.Mobile.Shop.Repository;

import lk.ijse.Apple.Vision.Mobile.Shop.Entity.Inventory;
import lk.ijse.Apple.Vision.Mobile.Shop.Enumeration.InventoryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Optional<Inventory> findByProductVariant_VariantId(Long variantId);
    List<Inventory> findAllByInventoryStatus(InventoryStatus inventoryStatus);
    List<Inventory> findAllByQuantityLessThanEqual(int quantity);
}