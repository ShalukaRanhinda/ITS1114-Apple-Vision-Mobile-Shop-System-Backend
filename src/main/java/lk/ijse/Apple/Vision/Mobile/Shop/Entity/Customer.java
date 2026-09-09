package lk.ijse.Apple.Vision.Mobile.Shop.Entity;

import jakarta.persistence.*;
import lk.ijse.Apple.Vision.Mobile.Shop.Enumeration.CustomerStatus;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long customerId;
    private String fullName;
    private String email;
    private String phoneNumber;
    @Enumerated(EnumType.STRING)
    private CustomerStatus customerStatus = CustomerStatus.ACTIVE;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "userId")
    private User user;


}
