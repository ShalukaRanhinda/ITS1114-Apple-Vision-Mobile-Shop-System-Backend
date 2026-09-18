package lk.ijse.Apple.Vision.Mobile.Shop.Service;

import lk.ijse.Apple.Vision.Mobile.Shop.DTO.PaymentDTO;
import java.util.List;

public interface PaymentService {
    PaymentDTO savePayment(PaymentDTO paymentDTO);
    PaymentDTO updatePayment(PaymentDTO paymentDTO);
    String deletePayment(Long paymentId);
    List<PaymentDTO> getAllPayments();
    PaymentDTO getPaymentById(Long paymentId);
    List<PaymentDTO> getPaymentsByOrderId(Long orderId);
    List<PaymentDTO> getPaymentsByRepairId(Long repairId);
}