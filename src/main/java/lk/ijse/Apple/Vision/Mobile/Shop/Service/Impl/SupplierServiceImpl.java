package lk.ijse.Apple.Vision.Mobile.Shop.Service.Impl;

import jakarta.transaction.Transactional;
import lk.ijse.Apple.Vision.Mobile.Shop.DTO.SupplierDTO;
import lk.ijse.Apple.Vision.Mobile.Shop.Entity.Supplier;
import lk.ijse.Apple.Vision.Mobile.Shop.Enumeration.SupplierStatus;
import lk.ijse.Apple.Vision.Mobile.Shop.Exception.CustomException;
import lk.ijse.Apple.Vision.Mobile.Shop.Repository.SupplierRepository;
import lk.ijse.Apple.Vision.Mobile.Shop.Service.SupplierService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@Slf4j
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;

    public SupplierServiceImpl(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    @Override
    public SupplierDTO saveSupplier(SupplierDTO supplierDTO) {
        log.info("Execute saveSupplier()");

        if (supplierDTO == null) {
            throw new CustomException(400, "Supplier data cannot be null!");
        }
        if (supplierDTO.getSupplierName() == null || supplierDTO.getSupplierName().trim().isEmpty()) {
            throw new CustomException(400, "Supplier name cannot be empty!");
        }
        if (supplierDTO.getPhoneNumber() == null || supplierDTO.getPhoneNumber().trim().isEmpty()) {
            throw new CustomException(400, "Phone number cannot be empty!");
        }
        if (supplierDTO.getEmail() == null || supplierDTO.getEmail().trim().isEmpty()) {
            throw new CustomException(400, "Email cannot be empty!");
        }

        Supplier supplier = new Supplier();
        supplier.setSupplierName(supplierDTO.getSupplierName().trim());
        supplier.setPhoneNumber(supplierDTO.getPhoneNumber().trim());
        supplier.setEmail(supplierDTO.getEmail().trim());
        supplier.setSupplierStatus(SupplierStatus.ACTIVE);

        Supplier savedSupplier = supplierRepository.save(supplier);
        log.info("Supplier saved successfully with ID: {}", savedSupplier.getSupplierId());

        supplierDTO.setSupplierId(savedSupplier.getSupplierId());
        supplierDTO.setSupplierStatus(savedSupplier.getSupplierStatus());
        return supplierDTO;
    }

    @Override
    public SupplierDTO updateSupplier(SupplierDTO supplierDTO) {
        log.info("Execute updateSupplier()");

        if (supplierDTO == null) {
            throw new CustomException(400, "Supplier update data cannot be null!");
        }
        if (supplierDTO.getSupplierId() == null) {
            throw new CustomException(400, "Supplier ID cannot be null for update!");
        }
        if (supplierDTO.getSupplierName() == null || supplierDTO.getSupplierName().trim().isEmpty()) {
            throw new CustomException(400, "Supplier name cannot be empty!");
        }
        if (supplierDTO.getPhoneNumber() == null || supplierDTO.getPhoneNumber().trim().isEmpty()) {
            throw new CustomException(400, "Phone number cannot be empty!");
        }
        if (supplierDTO.getEmail() == null || supplierDTO.getEmail().trim().isEmpty()) {
            throw new CustomException(400, "Email cannot be empty!");
        }

        Supplier supplier = supplierRepository.findById(supplierDTO.getSupplierId())
                .orElseThrow(() -> new CustomException(404, "Supplier not found with ID: " + supplierDTO.getSupplierId()));

        if (supplier.getSupplierStatus() == SupplierStatus.DELETED) {
            throw new CustomException(400, "Cannot update a deleted supplier!");
        }

        supplier.setSupplierName(supplierDTO.getSupplierName().trim());
        supplier.setPhoneNumber(supplierDTO.getPhoneNumber().trim());
        supplier.setEmail(supplierDTO.getEmail().trim());

        if (supplierDTO.getSupplierStatus() != null) {
            supplier.setSupplierStatus(supplierDTO.getSupplierStatus());
        }

        Supplier updatedSupplier = supplierRepository.save(supplier);
        log.info("Supplier updated successfully with ID: {}", updatedSupplier.getSupplierId());

        SupplierDTO responseDTO = new SupplierDTO();
        responseDTO.setSupplierId(updatedSupplier.getSupplierId());
        responseDTO.setSupplierName(updatedSupplier.getSupplierName());
        responseDTO.setPhoneNumber(updatedSupplier.getPhoneNumber());
        responseDTO.setEmail(updatedSupplier.getEmail());
        responseDTO.setSupplierStatus(updatedSupplier.getSupplierStatus());

        return responseDTO;
    }

    @Override
    public String deleteSupplier(Long supplierId) {
        log.info("Execute deleteSupplier()");

        if (supplierId == null) {
            throw new CustomException(400, "Supplier ID cannot be null!");
        }

        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new CustomException(404, "Supplier not found with ID: " + supplierId));

        if (supplier.getSupplierStatus() == SupplierStatus.DELETED) {
            throw new CustomException(400, "Supplier is already deleted!");
        }

        supplier.setSupplierStatus(SupplierStatus.DELETED);
        supplierRepository.save(supplier);
        log.info("Supplier marked as DELETED for ID: {}", supplierId);

        return "Supplier deleted successfully!";
    }

    @Override
    public List<SupplierDTO> getAllSuppliers() {
        log.info("Execute getAllSuppliers()");

        List<Supplier> supplierList = supplierRepository.findAll();
        List<SupplierDTO> responseList = new ArrayList<>();

        for (Supplier supplier : supplierList) {
            if (supplier.getSupplierStatus() != SupplierStatus.DELETED) {
                SupplierDTO supplierDTO = new SupplierDTO();
                supplierDTO.setSupplierId(supplier.getSupplierId());
                supplierDTO.setSupplierName(supplier.getSupplierName());
                supplierDTO.setPhoneNumber(supplier.getPhoneNumber());
                supplierDTO.setEmail(supplier.getEmail());
                supplierDTO.setSupplierStatus(supplier.getSupplierStatus());

                responseList.add(supplierDTO);
            }
        }
        return responseList;
    }

    @Override
    public SupplierDTO getSupplierById(Long supplierId) {
        log.info("Execute getSupplierById()");

        if (supplierId == null) {
            throw new CustomException(400, "Supplier ID cannot be null!");
        }

        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new CustomException(404, "Supplier not found with ID: " + supplierId));

        if (supplier.getSupplierStatus() == SupplierStatus.DELETED) {
            throw new CustomException(404, "Supplier not found or has been deleted!");
        }

        SupplierDTO supplierDTO = new SupplierDTO();
        supplierDTO.setSupplierId(supplier.getSupplierId());
        supplierDTO.setSupplierName(supplier.getSupplierName());
        supplierDTO.setPhoneNumber(supplier.getPhoneNumber());
        supplierDTO.setEmail(supplier.getEmail());
        supplierDTO.setSupplierStatus(supplier.getSupplierStatus());

        return supplierDTO;
    }
}