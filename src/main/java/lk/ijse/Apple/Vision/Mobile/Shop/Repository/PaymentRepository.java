package lk.ijse.Apple.Vision.Mobile.Shop.Repository;

import lk.ijse.Apple.Vision.Mobile.Shop.Entity.Payment;
import lk.ijse.Apple.Vision.Mobile.Shop.Enumeration.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findAllByOrder_OrderId(Long orderId);
    List<Payment> findAllByRepair_RepairId(Long repairId);
    List<Payment> findAllByPaymentStatus(PaymentStatus paymentStatus);
}