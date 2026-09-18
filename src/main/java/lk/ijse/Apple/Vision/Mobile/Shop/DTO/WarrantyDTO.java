package lk.ijse.Apple.Vision.Mobile.Shop.DTO;

import lk.ijse.Apple.Vision.Mobile.Shop.Enumeration.WarrantyStatus;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WarrantyDTO {
    private Long warrantyId;
    private String serialNumber;
    private LocalDate startDate;
    private LocalDate endDate;
    private WarrantyStatus warrantyStatus;
    private Long orderId;
}