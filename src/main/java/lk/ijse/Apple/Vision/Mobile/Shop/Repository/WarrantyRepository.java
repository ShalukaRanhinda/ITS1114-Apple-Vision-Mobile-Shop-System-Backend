package lk.ijse.Apple.Vision.Mobile.Shop.Repository;

import lk.ijse.Apple.Vision.Mobile.Shop.Entity.Warranty;
import lk.ijse.Apple.Vision.Mobile.Shop.Enumeration.WarrantyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface WarrantyRepository extends JpaRepository<Warranty, Long> {

    Optional<Warranty> findBySerialNumber(String serialNumber);

    List<Warranty> findAllByOrder_OrderId(Long orderId);

    List<Warranty> findAllByWarrantyStatusAndEndDateBefore(WarrantyStatus status, LocalDate date);

    @Modifying
    @Query("UPDATE Warranty w SET w.warrantyStatus = :expiredStatus WHERE w.warrantyStatus = :activeStatus AND w.endDate < :today")
    int expireOutdatedWarranties(@Param("activeStatus") WarrantyStatus activeStatus,
                                 @Param("expiredStatus") WarrantyStatus expiredStatus,
                                 @Param("today") LocalDate today);
}