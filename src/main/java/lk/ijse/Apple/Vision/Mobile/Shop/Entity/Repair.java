package lk.ijse.Apple.Vision.Mobile.Shop.Entity;

import jakarta.persistence.*;
import lk.ijse.Apple.Vision.Mobile.Shop.Enumeration.RepairStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Repair {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long repairId;
    private String deviceModel;
    private String description;
    @Enumerated(EnumType.STRING)
    private RepairStatus repairStatus = RepairStatus.PENDING;
    private LocalDateTime receivedDate;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;
    @OneToMany(mappedBy = "repair", cascade = CascadeType.ALL)
    private List<Payment> payments;
}
