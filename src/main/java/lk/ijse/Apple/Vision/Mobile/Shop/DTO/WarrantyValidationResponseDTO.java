package lk.ijse.Apple.Vision.Mobile.Shop.DTO;

import lk.ijse.Apple.Vision.Mobile.Shop.Enumeration.WarrantyStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WarrantyValidationResponseDTO {
    private Long warrantyId;
    private String serialNumber;
    private boolean isValid;
    private WarrantyStatus currentStatus;
    private LocalDate startDate;
    private LocalDate endDate;
    private long remainingDays;
    private Long orderId;
    private String customerName;
    private String customerPhone;
    private String message;
}