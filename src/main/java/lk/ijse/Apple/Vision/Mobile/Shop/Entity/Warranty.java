package lk.ijse.Apple.Vision.Mobile.Shop.Entity;

import jakarta.persistence.*;
import lk.ijse.Apple.Vision.Mobile.Shop.Enumeration.WarrantyStatus;
import java.time.LocalDate;

import lombok.*;


@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Warranty {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long warrantyId;
    private String serialNumber;
    private LocalDate startDate ;
    private LocalDate endDate;
    @Enumerated(EnumType.STRING)
    private WarrantyStatus warrantyStatus = WarrantyStatus.ACTIVE;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;
}
