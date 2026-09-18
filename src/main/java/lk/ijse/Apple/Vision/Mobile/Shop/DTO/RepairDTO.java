package lk.ijse.Apple.Vision.Mobile.Shop.DTO;

import lk.ijse.Apple.Vision.Mobile.Shop.Enumeration.RepairStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepairDTO {
    private Long repairId;
    private String deviceModel;
    private String description;
    private RepairStatus repairStatus;
    private LocalDateTime receivedDate;
    private Long customerId;
}