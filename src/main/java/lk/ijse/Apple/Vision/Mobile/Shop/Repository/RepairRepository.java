package lk.ijse.Apple.Vision.Mobile.Shop.Repository;

import lk.ijse.Apple.Vision.Mobile.Shop.Entity.Repair;
import lk.ijse.Apple.Vision.Mobile.Shop.Enumeration.RepairStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepairRepository extends JpaRepository<Repair, Long> {
    List<Repair> findAllByCustomer_CustomerId(Long customerId);
    List<Repair> findAllByRepairStatus(RepairStatus repairStatus);
}