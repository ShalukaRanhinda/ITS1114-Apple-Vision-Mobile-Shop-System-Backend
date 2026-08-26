package lk.ijse.Apple.Vision.Mobile.Shop.Entity;

import jakarta.persistence.*;
import lk.ijse.Apple.Vision.Mobile.Shop.Enumeration.CategoryStatus;
import lk.ijse.Apple.Vision.Mobile.Shop.Enumeration.SupplierStatus;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Supplier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long supplierId;
    private String supplierName;
    private String phoneNumber;
    private String email;
    @Enumerated(EnumType.STRING)
    private SupplierStatus supplierStatus = SupplierStatus.ACTIVE;

}
