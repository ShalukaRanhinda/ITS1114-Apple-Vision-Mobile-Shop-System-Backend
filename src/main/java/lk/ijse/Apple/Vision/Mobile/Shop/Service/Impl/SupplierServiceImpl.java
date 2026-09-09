package lk.ijse.Apple.Vision.Mobile.Shop.Service.Impl;

import jakarta.transaction.Transactional;
import lk.ijse.Apple.Vision.Mobile.Shop.DTO.SupplierDTO;
import lk.ijse.Apple.Vision.Mobile.Shop.Entity.Supplier;
import lk.ijse.Apple.Vision.Mobile.Shop.Enumeration.SupplierStatus;
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

        Supplier supplier = new Supplier();
        supplier.setSupplierName(supplierDTO.getSupplierName());
        supplier.setPhoneNumber(supplierDTO.getPhoneNumber());
        supplier.setEmail(supplierDTO.getEmail());
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

        Supplier supplier = supplierRepository.findById(supplierDTO.getSupplierId()).orElse(new Supplier());

        supplier.setSupplierName(supplierDTO.getSupplierName());
        supplier.setPhoneNumber(supplierDTO.getPhoneNumber());
        supplier.setEmail(supplierDTO.getEmail());

        if (supplierDTO.getSupplierStatus() != null) {
            supplier.setSupplierStatus(supplierDTO.getSupplierStatus());
        }

        Supplier updatedSupplier = supplierRepository.save(supplier);
        log.info("Supplier updated successfully!");

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

        Supplier supplier = supplierRepository.findById(supplierId).orElse(null);
        if (supplier != null) {
            supplier.setSupplierStatus(SupplierStatus.DELETED);
            supplierRepository.save(supplier);
        }

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

        Supplier supplier = supplierRepository.findById(supplierId).orElse(new Supplier());

        SupplierDTO supplierDTO = new SupplierDTO();
        supplierDTO.setSupplierId(supplier.getSupplierId());
        supplierDTO.setSupplierName(supplier.getSupplierName());
        supplierDTO.setPhoneNumber(supplier.getPhoneNumber());
        supplierDTO.setEmail(supplier.getEmail());
        supplierDTO.setSupplierStatus(supplier.getSupplierStatus());

        return supplierDTO;
    }
}