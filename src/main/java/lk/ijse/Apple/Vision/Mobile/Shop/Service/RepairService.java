package lk.ijse.Apple.Vision.Mobile.Shop.Service;

import lk.ijse.Apple.Vision.Mobile.Shop.DTO.RepairDTO;
import java.util.List;

public interface RepairService {
    RepairDTO saveRepair(RepairDTO repairDTO);
    RepairDTO updateRepair(RepairDTO repairDTO);
    String deleteRepair(Long repairId);
    List<RepairDTO> getAllRepairs();
    RepairDTO getRepairById(Long repairId);
    List<RepairDTO> getRepairsByCustomerId(Long customerId);
}