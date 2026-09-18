package lk.ijse.Apple.Vision.Mobile.Shop.Service.Impl;

import jakarta.transaction.Transactional;
import lk.ijse.Apple.Vision.Mobile.Shop.DTO.RepairDTO;
import lk.ijse.Apple.Vision.Mobile.Shop.Entity.Customer;
import lk.ijse.Apple.Vision.Mobile.Shop.Entity.Repair;
import lk.ijse.Apple.Vision.Mobile.Shop.Enumeration.CustomerStatus;
import lk.ijse.Apple.Vision.Mobile.Shop.Enumeration.RepairStatus;
import lk.ijse.Apple.Vision.Mobile.Shop.Exception.CustomException;
import lk.ijse.Apple.Vision.Mobile.Shop.Repository.CustomerRepository;
import lk.ijse.Apple.Vision.Mobile.Shop.Repository.RepairRepository;
import lk.ijse.Apple.Vision.Mobile.Shop.Service.RepairService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@Slf4j
public class RepairServiceImpl implements RepairService {

    private final RepairRepository repairRepository;
    private final CustomerRepository customerRepository;

    public RepairServiceImpl(RepairRepository repairRepository, CustomerRepository customerRepository) {
        this.repairRepository = repairRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    public RepairDTO saveRepair(RepairDTO repairDTO) {
        log.info("Execute saveRepair()");

        if (repairDTO == null) {
            throw new CustomException(400, "Repair data cannot be null!");
        }
        if (repairDTO.getCustomerId() == null) {
            throw new CustomException(400, "Customer ID cannot be null!");
        }
        if (repairDTO.getDeviceModel() == null || repairDTO.getDeviceModel().trim().isEmpty()) {
            throw new CustomException(400, "Device model cannot be empty!");
        }
        if (repairDTO.getDescription() == null || repairDTO.getDescription().trim().isEmpty()) {
            throw new CustomException(400, "Repair description cannot be empty!");
        }

        Customer customer = customerRepository.findById(repairDTO.getCustomerId())
                .orElseThrow(() -> new CustomException(404, "Customer not found with ID: " + repairDTO.getCustomerId()));

        if (customer.getCustomerStatus() == CustomerStatus.DELETED) {
            throw new CustomException(400, "Cannot create a repair request for an inactive or deleted customer!");
        }

        Repair repair = new Repair();
        repair.setDeviceModel(repairDTO.getDeviceModel().trim());
        repair.setDescription(repairDTO.getDescription().trim());
        repair.setRepairStatus(repairDTO.getRepairStatus() != null ? repairDTO.getRepairStatus() : RepairStatus.PENDING);
        repair.setReceivedDate(LocalDateTime.now());
        repair.setCustomer(customer);

        Repair savedRepair = repairRepository.save(repair);
        log.info("Repair saved successfully with ID: {}", savedRepair.getRepairId());

        repairDTO.setRepairId(savedRepair.getRepairId());
        repairDTO.setReceivedDate(savedRepair.getReceivedDate());
        repairDTO.setRepairStatus(savedRepair.getRepairStatus());
        return repairDTO;
    }

    @Override
    public RepairDTO updateRepair(RepairDTO repairDTO) {
        log.info("Execute updateRepair()");

        if (repairDTO == null) {
            throw new CustomException(400, "Repair update data cannot be null!");
        }
        if (repairDTO.getRepairId() == null) {
            throw new CustomException(400, "Repair ID cannot be null for update!");
        }
        if (repairDTO.getDeviceModel() == null || repairDTO.getDeviceModel().trim().isEmpty()) {
            throw new CustomException(400, "Device model cannot be empty!");
        }
        if (repairDTO.getDescription() == null || repairDTO.getDescription().trim().isEmpty()) {
            throw new CustomException(400, "Repair description cannot be empty!");
        }

        Repair repair = repairRepository.findById(repairDTO.getRepairId())
                .orElseThrow(() -> new CustomException(404, "Repair not found with ID: " + repairDTO.getRepairId()));

        if (repair.getRepairStatus() == RepairStatus.CANCELLED) {
            throw new CustomException(400, "Cannot update a cancelled repair!");
        }

        if (repairDTO.getCustomerId() != null) {
            Customer customer = customerRepository.findById(repairDTO.getCustomerId())
                    .orElseThrow(() -> new CustomException(404, "Customer not found with ID: " + repairDTO.getCustomerId()));

            if (customer.getCustomerStatus() == CustomerStatus.DELETED) {
                throw new CustomException(400, "Cannot link repair to an inactive or deleted customer!");
            }
            repair.setCustomer(customer);
        }

        repair.setDeviceModel(repairDTO.getDeviceModel().trim());
        repair.setDescription(repairDTO.getDescription().trim());

        if (repairDTO.getRepairStatus() != null) {
            repair.setRepairStatus(repairDTO.getRepairStatus());
        }

        Repair updatedRepair = repairRepository.save(repair);
        log.info("Repair updated successfully with ID: {}", updatedRepair.getRepairId());

        RepairDTO responseDTO = new RepairDTO();
        responseDTO.setRepairId(updatedRepair.getRepairId());
        responseDTO.setDeviceModel(updatedRepair.getDeviceModel());
        responseDTO.setDescription(updatedRepair.getDescription());
        responseDTO.setRepairStatus(updatedRepair.getRepairStatus());
        responseDTO.setReceivedDate(updatedRepair.getReceivedDate());
        responseDTO.setCustomerId(updatedRepair.getCustomer() != null ? updatedRepair.getCustomer().getCustomerId() : null);

        return responseDTO;
    }

    @Override
    public String deleteRepair(Long repairId) {
        log.info("Execute deleteRepair()");

        if (repairId == null) {
            throw new CustomException(400, "Repair ID cannot be null!");
        }

        Repair repair = repairRepository.findById(repairId)
                .orElseThrow(() -> new CustomException(404, "Repair not found with ID: " + repairId));

        if (repair.getRepairStatus() == RepairStatus.CANCELLED) {
            throw new CustomException(400, "Repair is already cancelled!");
        }

        repair.setRepairStatus(RepairStatus.CANCELLED);
        repairRepository.save(repair);
        log.info("Repair marked as CANCELLED for ID: {}", repairId);

        return "Repair cancelled successfully!";
    }

    @Override
    public List<RepairDTO> getAllRepairs() {
        log.info("Execute getAllRepairs()");

        List<Repair> repairList = repairRepository.findAll();
        List<RepairDTO> responseList = new ArrayList<>();

        for (Repair repair : repairList) {
            if (repair.getRepairStatus() != RepairStatus.CANCELLED) {
                RepairDTO dto = new RepairDTO();
                dto.setRepairId(repair.getRepairId());
                dto.setDeviceModel(repair.getDeviceModel());
                dto.setDescription(repair.getDescription());
                dto.setRepairStatus(repair.getRepairStatus());
                dto.setReceivedDate(repair.getReceivedDate());
                dto.setCustomerId(repair.getCustomer() != null ? repair.getCustomer().getCustomerId() : null);

                responseList.add(dto);
            }
        }
        return responseList;
    }

    @Override
    public RepairDTO getRepairById(Long repairId) {
        log.info("Execute getRepairById()");

        if (repairId == null) {
            throw new CustomException(400, "Repair ID cannot be null!");
        }

        Repair repair = repairRepository.findById(repairId)
                .orElseThrow(() -> new CustomException(404, "Repair not found with ID: " + repairId));

        if (repair.getRepairStatus() == RepairStatus.CANCELLED) {
            throw new CustomException(404, "Repair not found or has been cancelled!");
        }

        RepairDTO dto = new RepairDTO();
        dto.setRepairId(repair.getRepairId());
        dto.setDeviceModel(repair.getDeviceModel());
        dto.setDescription(repair.getDescription());
        dto.setRepairStatus(repair.getRepairStatus());
        dto.setReceivedDate(repair.getReceivedDate());
        dto.setCustomerId(repair.getCustomer() != null ? repair.getCustomer().getCustomerId() : null);

        return dto;
    }

    @Override
    public List<RepairDTO> getRepairsByCustomerId(Long customerId) {
        log.info("Execute getRepairsByCustomerId()");

        if (customerId == null) {
            throw new CustomException(400, "Customer ID cannot be null!");
        }

        if (!customerRepository.existsById(customerId)) {
            throw new CustomException(404, "Customer not found with ID: " + customerId);
        }

        List<Repair> repairList = repairRepository.findAllByCustomer_CustomerId(customerId);
        List<RepairDTO> responseList = new ArrayList<>();

        for (Repair repair : repairList) {
            if (repair.getRepairStatus() != RepairStatus.CANCELLED) {
                RepairDTO dto = new RepairDTO();
                dto.setRepairId(repair.getRepairId());
                dto.setDeviceModel(repair.getDeviceModel());
                dto.setDescription(repair.getDescription());
                dto.setRepairStatus(repair.getRepairStatus());
                dto.setReceivedDate(repair.getReceivedDate());
                dto.setCustomerId(repair.getCustomer() != null ? repair.getCustomer().getCustomerId() : null);

                responseList.add(dto);
            }
        }
        return responseList;
    }
}