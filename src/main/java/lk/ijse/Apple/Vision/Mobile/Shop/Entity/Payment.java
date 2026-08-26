package lk.ijse.Apple.Vision.Mobile.Shop.Entity;

import jakarta.persistence.*;
import lk.ijse.Apple.Vision.Mobile.Shop.Enumeration.PaymentMethod;
import lk.ijse.Apple.Vision.Mobile.Shop.Enumeration.PaymentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;
    private BigDecimal amount;
    private LocalDateTime paymentDate = LocalDateTime.now();
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "repair_id")
    private Repair repair;

}
