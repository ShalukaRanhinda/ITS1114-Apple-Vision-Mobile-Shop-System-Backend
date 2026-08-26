package lk.ijse.Apple.Vision.Mobile.Shop.Entity;

import jakarta.persistence.*;
import lk.ijse.Apple.Vision.Mobile.Shop.Enumeration.PurchaseOrderStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PurchaseOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long purchaseOrderId;
    private LocalDateTime purchaseOrderDate = LocalDateTime.now();
    @Enumerated(EnumType.STRING)
    private PurchaseOrderStatus purchaseOrderStatus = PurchaseOrderStatus.PENDING;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;
}
