package lk.ijse.Apple.Vision.Mobile.Shop.Service;

import lk.ijse.Apple.Vision.Mobile.Shop.DTO.SupplierDTO;
import java.util.List;

public interface SupplierService {
    SupplierDTO saveSupplier(SupplierDTO supplierDTO);
    SupplierDTO updateSupplier(SupplierDTO supplierDTO);
    String deleteSupplier(Long supplierId);
    List<SupplierDTO> getAllSuppliers();
    SupplierDTO getSupplierById(Long supplierId);
}