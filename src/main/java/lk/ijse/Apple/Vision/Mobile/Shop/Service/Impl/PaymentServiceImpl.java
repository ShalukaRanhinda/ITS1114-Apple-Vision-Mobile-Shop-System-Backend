package lk.ijse.Apple.Vision.Mobile.Shop.Service.Impl;

import jakarta.transaction.Transactional;
import lk.ijse.Apple.Vision.Mobile.Shop.DTO.PaymentDTO;
import lk.ijse.Apple.Vision.Mobile.Shop.Entity.Order;
import lk.ijse.Apple.Vision.Mobile.Shop.Entity.Payment;
import lk.ijse.Apple.Vision.Mobile.Shop.Entity.Repair;
import lk.ijse.Apple.Vision.Mobile.Shop.Enumeration.PaymentStatus;
import lk.ijse.Apple.Vision.Mobile.Shop.Exception.CustomException;
import lk.ijse.Apple.Vision.Mobile.Shop.Repository.OrderRepository;
import lk.ijse.Apple.Vision.Mobile.Shop.Repository.PaymentRepository;
import lk.ijse.Apple.Vision.Mobile.Shop.Repository.RepairRepository;
import lk.ijse.Apple.Vision.Mobile.Shop.Service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final RepairRepository repairRepository;

    public PaymentServiceImpl(PaymentRepository paymentRepository,
                              OrderRepository orderRepository,
                              RepairRepository repairRepository) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.repairRepository = repairRepository;
    }

    @Override
    public PaymentDTO savePayment(PaymentDTO paymentDTO) {
        log.info("Execute savePayment()");

        if (paymentDTO == null) {
            throw new CustomException(400, "Payment data cannot be null!");
        }
        if (paymentDTO.getAmount() == null || paymentDTO.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new CustomException(400, "Payment amount must be greater than zero!");
        }
        if (paymentDTO.getPaymentMethod() == null) {
            throw new CustomException(400, "Payment method cannot be null!");
        }
        if (paymentDTO.getOrderId() == null && paymentDTO.getRepairId() == null) {
            throw new CustomException(400, "Payment must be linked to either an Order or a Repair!");
        }

        Order order = null;
        if (paymentDTO.getOrderId() != null) {
            order = orderRepository.findById(paymentDTO.getOrderId())
                    .orElseThrow(() -> new CustomException(404, "Order not found with ID: " + paymentDTO.getOrderId()));
        }

        Repair repair = null;
        if (paymentDTO.getRepairId() != null) {
            repair = repairRepository.findById(paymentDTO.getRepairId())
                    .orElseThrow(() -> new CustomException(404, "Repair not found with ID: " + paymentDTO.getRepairId()));
        }

        Payment payment = new Payment();
        payment.setAmount(paymentDTO.getAmount());
        payment.setPaymentDate(LocalDateTime.now());
        payment.setPaymentMethod(paymentDTO.getPaymentMethod());
        payment.setPaymentStatus(paymentDTO.getPaymentStatus() != null ? paymentDTO.getPaymentStatus() : PaymentStatus.PAID);
        payment.setOrder(order);
        payment.setRepair(repair);

        Payment savedPayment = paymentRepository.save(payment);
        log.info("Payment saved successfully with ID: {}", savedPayment.getPaymentId());

        paymentDTO.setPaymentId(savedPayment.getPaymentId());
        paymentDTO.setPaymentDate(savedPayment.getPaymentDate());
        paymentDTO.setPaymentStatus(savedPayment.getPaymentStatus());
        return paymentDTO;
    }

    @Override
    public PaymentDTO updatePayment(PaymentDTO paymentDTO) {
        log.info("Execute updatePayment()");

        if (paymentDTO == null) {
            throw new CustomException(400, "Payment update data cannot be null!");
        }
        if (paymentDTO.getPaymentId() == null) {
            throw new CustomException(400, "Payment ID cannot be null for update!");
        }

        Payment payment = paymentRepository.findById(paymentDTO.getPaymentId())
                .orElseThrow(() -> new CustomException(404, "Payment not found with ID: " + paymentDTO.getPaymentId()));

        if (payment.getPaymentStatus() == PaymentStatus.DELETED) {
            throw new CustomException(400, "Cannot update a deleted payment!");
        }

        if (paymentDTO.getAmount() != null) {
            if (paymentDTO.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                throw new CustomException(400, "Payment amount must be greater than zero!");
            }
            payment.setAmount(paymentDTO.getAmount());
        }

        if (paymentDTO.getPaymentMethod() != null) {
            payment.setPaymentMethod(paymentDTO.getPaymentMethod());
        }

        if (paymentDTO.getPaymentStatus() != null) {
            payment.setPaymentStatus(paymentDTO.getPaymentStatus());
        }

        Payment updatedPayment = paymentRepository.save(payment);
        log.info("Payment updated successfully with ID: {}", updatedPayment.getPaymentId());

        PaymentDTO responseDTO = new PaymentDTO();
        responseDTO.setPaymentId(updatedPayment.getPaymentId());
        responseDTO.setAmount(updatedPayment.getAmount());
        responseDTO.setPaymentDate(updatedPayment.getPaymentDate());
        responseDTO.setPaymentMethod(updatedPayment.getPaymentMethod());
        responseDTO.setPaymentStatus(updatedPayment.getPaymentStatus());
        responseDTO.setOrderId(updatedPayment.getOrder() != null ? updatedPayment.getOrder().getOrderId() : null);
        responseDTO.setRepairId(updatedPayment.getRepair() != null ? updatedPayment.getRepair().getRepairId() : null);

        return responseDTO;
    }

    @Override
    public String deletePayment(Long paymentId) {
        log.info("Execute deletePayment()");

        if (paymentId == null) {
            throw new CustomException(400, "Payment ID cannot be null!");
        }

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new CustomException(404, "Payment not found with ID: " + paymentId));

        if (payment.getPaymentStatus() == PaymentStatus.DELETED) {
            throw new CustomException(400, "Payment is already deleted!");
        }

        payment.setPaymentStatus(PaymentStatus.DELETED);
        paymentRepository.save(payment);
        log.info("Payment marked as DELETED for ID: {}", paymentId);

        return "Payment deleted successfully!";
    }

    @Override
    public List<PaymentDTO> getAllPayments() {
        log.info("Execute getAllPayments()");

        List<Payment> paymentList = paymentRepository.findAll();
        List<PaymentDTO> responseList = new ArrayList<>();

        for (Payment payment : paymentList) {
            if (payment.getPaymentStatus() != PaymentStatus.DELETED) {
                PaymentDTO dto = new PaymentDTO();
                dto.setPaymentId(payment.getPaymentId());
                dto.setAmount(payment.getAmount());
                dto.setPaymentDate(payment.getPaymentDate());
                dto.setPaymentMethod(payment.getPaymentMethod());
                dto.setPaymentStatus(payment.getPaymentStatus());
                dto.setOrderId(payment.getOrder() != null ? payment.getOrder().getOrderId() : null);
                dto.setRepairId(payment.getRepair() != null ? payment.getRepair().getRepairId() : null);

                responseList.add(dto);
            }
        }
        return responseList;
    }

    @Override
    public PaymentDTO getPaymentById(Long paymentId) {
        log.info("Execute getPaymentById()");

        if (paymentId == null) {
            throw new CustomException(400, "Payment ID cannot be null!");
        }

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new CustomException(404, "Payment not found with ID: " + paymentId));

        if (payment.getPaymentStatus() == PaymentStatus.DELETED) {
            throw new CustomException(404, "Payment not found or has been deleted!");
        }

        PaymentDTO dto = new PaymentDTO();
        dto.setPaymentId(payment.getPaymentId());
        dto.setAmount(payment.getAmount());
        dto.setPaymentDate(payment.getPaymentDate());
        dto.setPaymentMethod(payment.getPaymentMethod());
        dto.setPaymentStatus(payment.getPaymentStatus());
        dto.setOrderId(payment.getOrder() != null ? payment.getOrder().getOrderId() : null);
        dto.setRepairId(payment.getRepair() != null ? payment.getRepair().getRepairId() : null);

        return dto;
    }

    @Override
    public List<PaymentDTO> getPaymentsByOrderId(Long orderId) {
        log.info("Execute getPaymentsByOrderId()");

        if (orderId == null) {
            throw new CustomException(400, "Order ID cannot be null!");
        }

        if (!orderRepository.existsById(orderId)) {
            throw new CustomException(404, "Order not found with ID: " + orderId);
        }

        List<Payment> paymentList = paymentRepository.findAllByOrder_OrderId(orderId);
        List<PaymentDTO> responseList = new ArrayList<>();

        for (Payment payment : paymentList) {
            if (payment.getPaymentStatus() != PaymentStatus.DELETED) {
                PaymentDTO dto = new PaymentDTO();
                dto.setPaymentId(payment.getPaymentId());
                dto.setAmount(payment.getAmount());
                dto.setPaymentDate(payment.getPaymentDate());
                dto.setPaymentMethod(payment.getPaymentMethod());
                dto.setPaymentStatus(payment.getPaymentStatus());
                dto.setOrderId(payment.getOrder() != null ? payment.getOrder().getOrderId() : null);
                dto.setRepairId(payment.getRepair() != null ? payment.getRepair().getRepairId() : null);

                responseList.add(dto);
            }
        }
        return responseList;
    }

    @Override
    public List<PaymentDTO> getPaymentsByRepairId(Long repairId) {
        log.info("Execute getPaymentsByRepairId()");

        if (repairId == null) {
            throw new CustomException(400, "Repair ID cannot be null!");
        }

        if (!repairRepository.existsById(repairId)) {
            throw new CustomException(404, "Repair not found with ID: " + repairId);
        }

        List<Payment> paymentList = paymentRepository.findAllByRepair_RepairId(repairId);
        List<PaymentDTO> responseList = new ArrayList<>();

        for (Payment payment : paymentList) {
            if (payment.getPaymentStatus() != PaymentStatus.DELETED) {
                PaymentDTO dto = new PaymentDTO();
                dto.setPaymentId(payment.getPaymentId());
                dto.setAmount(payment.getAmount());
                dto.setPaymentDate(payment.getPaymentDate());
                dto.setPaymentMethod(payment.getPaymentMethod());
                dto.setPaymentStatus(payment.getPaymentStatus());
                dto.setOrderId(payment.getOrder() != null ? payment.getOrder().getOrderId() : null);
                dto.setRepairId(payment.getRepair() != null ? payment.getRepair().getRepairId() : null);

                responseList.add(dto);
            }
        }
        return responseList;
    }
}