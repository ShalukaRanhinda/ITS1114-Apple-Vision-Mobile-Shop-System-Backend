package lk.ijse.Apple.Vision.Mobile.Shop.Repository;

import lk.ijse.Apple.Vision.Mobile.Shop.Entity.Warranty;
import lk.ijse.Apple.Vision.Mobile.Shop.Enumeration.WarrantyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WarrantyRepository extends JpaRepository<Warranty, Long> {
    Optional<Warranty> findBySerialNumber(String serialNumber);
    boolean existsBySerialNumber(String serialNumber);
    List<Warranty> findAllByWarrantyStatus(WarrantyStatus warrantyStatus);
    List<Warranty> findAllByOrder_OrderId(Long orderId);
}