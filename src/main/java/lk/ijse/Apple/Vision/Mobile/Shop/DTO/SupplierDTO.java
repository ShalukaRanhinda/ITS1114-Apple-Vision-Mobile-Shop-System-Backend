package lk.ijse.Apple.Vision.Mobile.Shop.DTO;

import lk.ijse.Apple.Vision.Mobile.Shop.Enumeration.SupplierStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupplierDTO {
    private Long supplierId;
    private String supplierName;
    private String phoneNumber;
    private String email;
    private SupplierStatus supplierStatus;
}