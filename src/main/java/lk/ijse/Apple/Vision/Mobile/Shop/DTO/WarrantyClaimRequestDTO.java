package lk.ijse.Apple.Vision.Mobile.Shop.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WarrantyClaimRequestDTO {
    private String serialNumber;
    private String claimReason;
}