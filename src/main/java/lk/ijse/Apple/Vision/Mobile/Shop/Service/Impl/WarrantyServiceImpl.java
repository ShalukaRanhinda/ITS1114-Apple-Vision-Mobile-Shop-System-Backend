package lk.ijse.Apple.Vision.Mobile.Shop.Service.Impl;

import jakarta.transaction.Transactional;
import lk.ijse.Apple.Vision.Mobile.Shop.DTO.WarrantyClaimRequestDTO;
import lk.ijse.Apple.Vision.Mobile.Shop.DTO.WarrantyDTO;
import lk.ijse.Apple.Vision.Mobile.Shop.DTO.WarrantyValidationResponseDTO;
import lk.ijse.Apple.Vision.Mobile.Shop.Entity.Order;
import lk.ijse.Apple.Vision.Mobile.Shop.Entity.Warranty;
import lk.ijse.Apple.Vision.Mobile.Shop.Enumeration.OrderStatus;
import lk.ijse.Apple.Vision.Mobile.Shop.Enumeration.WarrantyStatus;
import lk.ijse.Apple.Vision.Mobile.Shop.Exception.CustomException;
import lk.ijse.Apple.Vision.Mobile.Shop.Repository.OrderRepository;
import lk.ijse.Apple.Vision.Mobile.Shop.Repository.WarrantyRepository;
import lk.ijse.Apple.Vision.Mobile.Shop.Service.WarrantyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@Slf4j
public class WarrantyServiceImpl implements WarrantyService {

    private final WarrantyRepository warrantyRepository;
    private final OrderRepository orderRepository;

    public WarrantyServiceImpl(WarrantyRepository warrantyRepository, OrderRepository orderRepository) {
        this.warrantyRepository = warrantyRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public WarrantyDTO saveWarranty(WarrantyDTO warrantyDTO) {
        log.info("Execute saveWarranty()");

        if (warrantyDTO == null) {
            throw new CustomException(400, "Warranty data cannot be null!");
        }
        if (warrantyDTO.getOrderId() == null) {
            throw new CustomException(400, "Order ID cannot be null!");
        }
        if (warrantyDTO.getSerialNumber() == null || warrantyDTO.getSerialNumber().trim().isEmpty()) {
            throw new CustomException(400, "Serial number cannot be empty!");
        }
        if (warrantyDTO.getStartDate() == null) {
            throw new CustomException(400, "Warranty start date cannot be null!");
        }
        if (warrantyDTO.getEndDate() == null) {
            throw new CustomException(400, "Warranty end date cannot be null!");
        }
        if (warrantyDTO.getEndDate().isBefore(warrantyDTO.getStartDate())) {
            throw new CustomException(400, "Warranty end date cannot be before start date!");
        }

        String trimmedSerialNumber = warrantyDTO.getSerialNumber().trim();
        if (warrantyRepository.findBySerialNumber(trimmedSerialNumber).isPresent()) {
            throw new CustomException(400, "Warranty already exists with serial number: " + trimmedSerialNumber);
        }

        Order order = orderRepository.findById(warrantyDTO.getOrderId())
                .orElseThrow(() -> new CustomException(404, "Order not found with ID: " + warrantyDTO.getOrderId()));

        if (order.getOrderStatus() == OrderStatus.CANCELLED) {
            throw new CustomException(400, "Cannot create warranty for a cancelled order!");
        }

        Warranty warranty = new Warranty();
        warranty.setSerialNumber(trimmedSerialNumber);
        warranty.setStartDate(warrantyDTO.getStartDate());
        warranty.setEndDate(warrantyDTO.getEndDate());
        warranty.setWarrantyStatus(warrantyDTO.getWarrantyStatus() != null ? warrantyDTO.getWarrantyStatus() : WarrantyStatus.ACTIVE);
        warranty.setOrder(order);

        Warranty savedWarranty = warrantyRepository.save(warranty);
        log.info("Warranty saved successfully with ID: {}", savedWarranty.getWarrantyId());

        warrantyDTO.setWarrantyId(savedWarranty.getWarrantyId());
        warrantyDTO.setSerialNumber(savedWarranty.getSerialNumber());
        warrantyDTO.setWarrantyStatus(savedWarranty.getWarrantyStatus());
        return warrantyDTO;
    }

    @Override
    public WarrantyDTO updateWarranty(WarrantyDTO warrantyDTO) {
        log.info("Execute updateWarranty()");

        if (warrantyDTO == null) {
            throw new CustomException(400, "Warranty update data cannot be null!");
        }
        if (warrantyDTO.getWarrantyId() == null) {
            throw new CustomException(400, "Warranty ID cannot be null for update!");
        }
        if (warrantyDTO.getSerialNumber() == null || warrantyDTO.getSerialNumber().trim().isEmpty()) {
            throw new CustomException(400, "Serial number cannot be empty!");
        }
        if (warrantyDTO.getStartDate() == null) {
            throw new CustomException(400, "Warranty start date cannot be null!");
        }
        if (warrantyDTO.getEndDate() == null) {
            throw new CustomException(400, "Warranty end date cannot be null!");
        }
        if (warrantyDTO.getEndDate().isBefore(warrantyDTO.getStartDate())) {
            throw new CustomException(400, "Warranty end date cannot be before start date!");
        }

        Warranty warranty = warrantyRepository.findById(warrantyDTO.getWarrantyId())
                .orElseThrow(() -> new CustomException(404, "Warranty not found with ID: " + warrantyDTO.getWarrantyId()));

        if (warranty.getWarrantyStatus() == WarrantyStatus.EXPIRED) {
            throw new CustomException(400, "Cannot update an expired warranty!");
        }

        String trimmedSerialNumber = warrantyDTO.getSerialNumber().trim();
        if (!trimmedSerialNumber.equals(warranty.getSerialNumber()) && warrantyRepository.findBySerialNumber(trimmedSerialNumber).isPresent()) {
            throw new CustomException(400, "Warranty with serial number already exists: " + trimmedSerialNumber);
        }

        if (warrantyDTO.getOrderId() != null) {
            Order order = orderRepository.findById(warrantyDTO.getOrderId())
                    .orElseThrow(() -> new CustomException(404, "Order not found with ID: " + warrantyDTO.getOrderId()));

            if (order.getOrderStatus() == OrderStatus.CANCELLED) {
                throw new CustomException(400, "Cannot link warranty to a cancelled order!");
            }
            warranty.setOrder(order);
        }

        warranty.setSerialNumber(trimmedSerialNumber);
        warranty.setStartDate(warrantyDTO.getStartDate());
        warranty.setEndDate(warrantyDTO.getEndDate());

        if (warrantyDTO.getWarrantyStatus() != null) {
            warranty.setWarrantyStatus(warrantyDTO.getWarrantyStatus());
        }

        Warranty updatedWarranty = warrantyRepository.save(warranty);
        log.info("Warranty updated successfully with ID: {}", updatedWarranty.getWarrantyId());

        WarrantyDTO responseDTO = new WarrantyDTO();
        responseDTO.setWarrantyId(updatedWarranty.getWarrantyId());
        responseDTO.setSerialNumber(updatedWarranty.getSerialNumber());
        responseDTO.setStartDate(updatedWarranty.getStartDate());
        responseDTO.setEndDate(updatedWarranty.getEndDate());
        responseDTO.setWarrantyStatus(updatedWarranty.getWarrantyStatus());
        responseDTO.setOrderId(updatedWarranty.getOrder() != null ? updatedWarranty.getOrder().getOrderId() : null);

        return responseDTO;
    }

    @Override
    public String deleteWarranty(Long warrantyId) {
        log.info("Execute deleteWarranty()");

        if (warrantyId == null) {
            throw new CustomException(400, "Warranty ID cannot be null!");
        }

        Warranty warranty = warrantyRepository.findById(warrantyId)
                .orElseThrow(() -> new CustomException(404, "Warranty not found with ID: " + warrantyId));

        if (warranty.getWarrantyStatus() == WarrantyStatus.EXPIRED) {
            throw new CustomException(400, "Warranty is already marked as EXPIRED!");
        }

        warranty.setWarrantyStatus(WarrantyStatus.EXPIRED);
        warrantyRepository.save(warranty);
        log.info("Warranty marked as EXPIRED for ID: {}", warrantyId);

        return "Warranty marked as EXPIRED successfully!";
    }

    @Override
    public List<WarrantyDTO> getAllWarranties() {
        log.info("Execute getAllWarranties()");

        List<Warranty> warrantyList = warrantyRepository.findAll();
        List<WarrantyDTO> responseList = new ArrayList<>();

        for (Warranty warranty : warrantyList) {
            WarrantyDTO dto = new WarrantyDTO();
            dto.setWarrantyId(warranty.getWarrantyId());
            dto.setSerialNumber(warranty.getSerialNumber());
            dto.setStartDate(warranty.getStartDate());
            dto.setEndDate(warranty.getEndDate());
            dto.setWarrantyStatus(warranty.getWarrantyStatus());
            dto.setOrderId(warranty.getOrder() != null ? warranty.getOrder().getOrderId() : null);

            responseList.add(dto);
        }
        return responseList;
    }

    @Override
    public WarrantyDTO getWarrantyById(Long warrantyId) {
        log.info("Execute getWarrantyById()");

        if (warrantyId == null) {
            throw new CustomException(400, "Warranty ID cannot be null!");
        }

        Warranty warranty = warrantyRepository.findById(warrantyId)
                .orElseThrow(() -> new CustomException(404, "Warranty not found with ID: " + warrantyId));

        WarrantyDTO dto = new WarrantyDTO();
        dto.setWarrantyId(warranty.getWarrantyId());
        dto.setSerialNumber(warranty.getSerialNumber());
        dto.setStartDate(warranty.getStartDate());
        dto.setEndDate(warranty.getEndDate());
        dto.setWarrantyStatus(warranty.getWarrantyStatus());
        dto.setOrderId(warranty.getOrder() != null ? warranty.getOrder().getOrderId() : null);

        return dto;
    }

    @Override
    public WarrantyDTO getWarrantyBySerialNumber(String serialNumber) {
        log.info("Execute getWarrantyBySerialNumber()");

        if (serialNumber == null || serialNumber.trim().isEmpty()) {
            throw new CustomException(400, "Serial number cannot be empty!");
        }

        Warranty warranty = warrantyRepository.findBySerialNumber(serialNumber.trim())
                .orElseThrow(() -> new CustomException(404, "Warranty not found for serial number: " + serialNumber.trim()));

        WarrantyDTO dto = new WarrantyDTO();
        dto.setWarrantyId(warranty.getWarrantyId());
        dto.setSerialNumber(warranty.getSerialNumber());
        dto.setStartDate(warranty.getStartDate());
        dto.setEndDate(warranty.getEndDate());
        dto.setWarrantyStatus(warranty.getWarrantyStatus());
        dto.setOrderId(warranty.getOrder() != null ? warranty.getOrder().getOrderId() : null);

        return dto;
    }

    @Override
    public List<WarrantyDTO> getWarrantiesByOrderId(Long orderId) {
        log.info("Execute getWarrantiesByOrderId()");

        if (orderId == null) {
            throw new CustomException(400, "Order ID cannot be null!");
        }

        if (!orderRepository.existsById(orderId)) {
            throw new CustomException(404, "Order not found with ID: " + orderId);
        }

        List<Warranty> warrantyList = warrantyRepository.findAllByOrder_OrderId(orderId);
        List<WarrantyDTO> responseList = new ArrayList<>();

        for (Warranty warranty : warrantyList) {
            WarrantyDTO dto = new WarrantyDTO();
            dto.setWarrantyId(warranty.getWarrantyId());
            dto.setSerialNumber(warranty.getSerialNumber());
            dto.setStartDate(warranty.getStartDate());
            dto.setEndDate(warranty.getEndDate());
            dto.setWarrantyStatus(warranty.getWarrantyStatus());
            dto.setOrderId(warranty.getOrder() != null ? warranty.getOrder().getOrderId() : null);

            responseList.add(dto);
        }
        return responseList;
    }
    @Override
    public WarrantyValidationResponseDTO validateWarrantyBySerial(String serialNumber) {
        log.info("Execute validateWarrantyBySerial() for SN: {}", serialNumber);

        if (serialNumber == null || serialNumber.trim().isEmpty()) {
            throw new CustomException(400, "Serial number cannot be empty!");
        }

        Warranty warranty = warrantyRepository.findBySerialNumber(serialNumber.trim())
                .orElseThrow(() -> new CustomException(404, "No warranty record registered for serial number: " + serialNumber.trim()));

        LocalDate today = LocalDate.now();
        long remainingDays = java.time.temporal.ChronoUnit.DAYS.between(today, warranty.getEndDate());

        if (warranty.getWarrantyStatus() == WarrantyStatus.EXPIRED || remainingDays < 0) {
            if (warranty.getWarrantyStatus() != WarrantyStatus.EXPIRED) {
                warranty.setWarrantyStatus(WarrantyStatus.EXPIRED);
                warrantyRepository.save(warranty);
            }
            return new WarrantyValidationResponseDTO(
                    warranty.getWarrantyId(),
                    warranty.getSerialNumber(),
                    false,
                    WarrantyStatus.EXPIRED,
                    warranty.getStartDate(),
                    warranty.getEndDate(),
                    0,
                    warranty.getOrder() != null ? warranty.getOrder().getOrderId() : null,
                    warranty.getOrder() != null && warranty.getOrder().getCustomer() != null ? warranty.getOrder().getCustomer().getFullName() : null,
                    warranty.getOrder() != null && warranty.getOrder().getCustomer() != null ? warranty.getOrder().getCustomer().getPhoneNumber() : null,
                    "Warranty has expired on " + warranty.getEndDate()
            );
        }

        return new WarrantyValidationResponseDTO(
                warranty.getWarrantyId(),
                warranty.getSerialNumber(),
                true,
                warranty.getWarrantyStatus(),
                warranty.getStartDate(),
                warranty.getEndDate(),
                remainingDays,
                warranty.getOrder() != null ? warranty.getOrder().getOrderId() : null,
                warranty.getOrder() != null && warranty.getOrder().getCustomer() != null ? warranty.getOrder().getCustomer().getFullName() : null,
                warranty.getOrder() != null && warranty.getOrder().getCustomer() != null ? warranty.getOrder().getCustomer().getPhoneNumber() : null,
                "Warranty is ACTIVE. " + remainingDays + " days remaining."
        );
    }

    @Override
    public WarrantyValidationResponseDTO claimWarranty(WarrantyClaimRequestDTO claimRequestDTO) {
        log.info("Execute claimWarranty() for SN: {}", claimRequestDTO.getSerialNumber());

        if (claimRequestDTO == null) {
            throw new CustomException(400, "Claim request data cannot be null!");
        }
        if (claimRequestDTO.getClaimReason() == null || claimRequestDTO.getClaimReason().trim().isEmpty()) {
            throw new CustomException(400, "Claim reason cannot be empty!");
        }

        WarrantyValidationResponseDTO validation = validateWarrantyBySerial(claimRequestDTO.getSerialNumber());

        if (!validation.isValid()) {
            throw new CustomException(400, "Cannot process claim! " + validation.getMessage());
        }

        log.info("Warranty claim approved for Serial: {}. Reason: {}", claimRequestDTO.getSerialNumber(), claimRequestDTO.getClaimReason());

        validation.setMessage("Claim verified successfully! You may proceed with repair/replacement.");
        return validation;
    }
}