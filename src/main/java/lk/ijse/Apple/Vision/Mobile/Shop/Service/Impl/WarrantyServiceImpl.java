package lk.ijse.Apple.Vision.Mobile.Shop.Service.Impl;

import jakarta.transaction.Transactional;
import lk.ijse.Apple.Vision.Mobile.Shop.DTO.WarrantyDTO;
import lk.ijse.Apple.Vision.Mobile.Shop.Entity.Order;
import lk.ijse.Apple.Vision.Mobile.Shop.Entity.Warranty;
import lk.ijse.Apple.Vision.Mobile.Shop.Enumeration.WarrantyStatus;
import lk.ijse.Apple.Vision.Mobile.Shop.Repository.OrderRepository;
import lk.ijse.Apple.Vision.Mobile.Shop.Repository.WarrantyRepository;
import lk.ijse.Apple.Vision.Mobile.Shop.Service.WarrantyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

        Order order = null;
        if (warrantyDTO.getOrderId() != null) {
            order = orderRepository.findById(warrantyDTO.getOrderId()).orElse(null);
        }

        Warranty warranty = new Warranty();
        warranty.setSerialNumber(warrantyDTO.getSerialNumber());
        warranty.setStartDate(warrantyDTO.getStartDate());
        warranty.setEndDate(warrantyDTO.getEndDate());
        warranty.setWarrantyStatus(warrantyDTO.getWarrantyStatus() != null ? warrantyDTO.getWarrantyStatus() : WarrantyStatus.ACTIVE);
        warranty.setOrder(order);

        Warranty savedWarranty = warrantyRepository.save(warranty);
        log.info("Warranty saved successfully with ID: {}", savedWarranty.getWarrantyId());

        warrantyDTO.setWarrantyId(savedWarranty.getWarrantyId());
        warrantyDTO.setWarrantyStatus(savedWarranty.getWarrantyStatus());
        return warrantyDTO;
    }

    @Override
    public WarrantyDTO updateWarranty(WarrantyDTO warrantyDTO) {
        log.info("Execute updateWarranty()");

        Warranty warranty = warrantyRepository.findById(warrantyDTO.getWarrantyId()).orElse(new Warranty());
        Order order = null;
        if (warrantyDTO.getOrderId() != null) {
            order = orderRepository.findById(warrantyDTO.getOrderId()).orElse(null);
        }

        warranty.setSerialNumber(warrantyDTO.getSerialNumber());
        warranty.setStartDate(warrantyDTO.getStartDate());
        warranty.setEndDate(warrantyDTO.getEndDate());
        warranty.setOrder(order);

        if (warrantyDTO.getWarrantyStatus() != null) {
            warranty.setWarrantyStatus(warrantyDTO.getWarrantyStatus());
        }

        Warranty updatedWarranty = warrantyRepository.save(warranty);
        log.info("Warranty updated successfully!");

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

        Warranty warranty = warrantyRepository.findById(warrantyId).orElse(null);
        if (warranty != null) {
            warranty.setWarrantyStatus(WarrantyStatus.EXPIRED);
            warrantyRepository.save(warranty);
        }

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

        Warranty warranty = warrantyRepository.findById(warrantyId).orElse(new Warranty());

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

        Warranty warranty = warrantyRepository.findBySerialNumber(serialNumber).orElse(new Warranty());

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
}