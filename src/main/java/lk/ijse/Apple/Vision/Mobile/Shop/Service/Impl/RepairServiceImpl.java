package lk.ijse.Apple.Vision.Mobile.Shop.Service.Impl;

import jakarta.transaction.Transactional;
import lk.ijse.Apple.Vision.Mobile.Shop.DTO.RepairDTO;
import lk.ijse.Apple.Vision.Mobile.Shop.Entity.Customer;
import lk.ijse.Apple.Vision.Mobile.Shop.Entity.Repair;
import lk.ijse.Apple.Vision.Mobile.Shop.Enumeration.RepairStatus;
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

        Customer customer = null;
        if (repairDTO.getCustomerId() != null) {
            customer = customerRepository.findById(repairDTO.getCustomerId()).orElse(null);
        }

        Repair repair = new Repair();
        repair.setDeviceModel(repairDTO.getDeviceModel());
        repair.setDescription(repairDTO.getDescription());
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

        Repair repair = repairRepository.findById(repairDTO.getRepairId()).orElse(new Repair());
        Customer customer = null;
        if (repairDTO.getCustomerId() != null) {
            customer = customerRepository.findById(repairDTO.getCustomerId()).orElse(null);
        }

        repair.setDeviceModel(repairDTO.getDeviceModel());
        repair.setDescription(repairDTO.getDescription());
        repair.setCustomer(customer);

        if (repairDTO.getRepairStatus() != null) {
            repair.setRepairStatus(repairDTO.getRepairStatus());
        }

        Repair updatedRepair = repairRepository.save(repair);
        log.info("Repair updated successfully!");

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

        Repair repair = repairRepository.findById(repairId).orElse(null);
        if (repair != null) {
            repair.setRepairStatus(RepairStatus.CANCELLED);
            repairRepository.save(repair);
        }

        return "Repair cancelled successfully!";
    }

    @Override
    public List<RepairDTO> getAllRepairs() {
        log.info("Execute getAllRepairs()");

        List<Repair> repairList = repairRepository.findAll();
        List<RepairDTO> responseList = new ArrayList<>();

        for (Repair repair : repairList) {
            RepairDTO dto = new RepairDTO();
            dto.setRepairId(repair.getRepairId());
            dto.setDeviceModel(repair.getDeviceModel());
            dto.setDescription(repair.getDescription());
            dto.setRepairStatus(repair.getRepairStatus());
            dto.setReceivedDate(repair.getReceivedDate());
            dto.setCustomerId(repair.getCustomer() != null ? repair.getCustomer().getCustomerId() : null);

            responseList.add(dto);
        }
        return responseList;
    }

    @Override
    public RepairDTO getRepairById(Long repairId) {
        log.info("Execute getRepairById()");

        Repair repair = repairRepository.findById(repairId).orElse(new Repair());

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

        List<Repair> repairList = repairRepository.findAllByCustomer_CustomerId(customerId);
        List<RepairDTO> responseList = new ArrayList<>();

        for (Repair repair : repairList) {
            RepairDTO dto = new RepairDTO();
            dto.setRepairId(repair.getRepairId());
            dto.setDeviceModel(repair.getDeviceModel());
            dto.setDescription(repair.getDescription());
            dto.setRepairStatus(repair.getRepairStatus());
            dto.setReceivedDate(repair.getReceivedDate());
            dto.setCustomerId(repair.getCustomer() != null ? repair.getCustomer().getCustomerId() : null);

            responseList.add(dto);
        }
        return responseList;
    }
}