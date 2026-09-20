package lk.ijse.Apple.Vision.Mobile.Shop.Service;

import lk.ijse.Apple.Vision.Mobile.Shop.DTO.WarrantyClaimRequestDTO;
import lk.ijse.Apple.Vision.Mobile.Shop.DTO.WarrantyDTO;
import lk.ijse.Apple.Vision.Mobile.Shop.DTO.WarrantyValidationResponseDTO;

import java.util.List;

public interface WarrantyService {
    WarrantyDTO saveWarranty(WarrantyDTO warrantyDTO);
    WarrantyDTO updateWarranty(WarrantyDTO warrantyDTO);
    String deleteWarranty(Long warrantyId);
    List<WarrantyDTO> getAllWarranties();
    WarrantyDTO getWarrantyById(Long warrantyId);
    WarrantyDTO getWarrantyBySerialNumber(String serialNumber);
    List<WarrantyDTO> getWarrantiesByOrderId(Long orderId);

    WarrantyValidationResponseDTO validateWarrantyBySerial(String serialNumber);

    WarrantyValidationResponseDTO claimWarranty(WarrantyClaimRequestDTO claimRequestDTO);
}