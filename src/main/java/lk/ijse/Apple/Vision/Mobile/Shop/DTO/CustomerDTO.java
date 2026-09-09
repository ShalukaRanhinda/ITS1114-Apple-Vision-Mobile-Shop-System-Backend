package lk.ijse.Apple.Vision.Mobile.Shop.DTO;

import lk.ijse.Apple.Vision.Mobile.Shop.Enumeration.CustomerStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerDTO {
    private Long customerId;
    private String fullName;
    private String email;
    private String phoneNumber;
    private CustomerStatus customerStatus;

    private String userName;
    private String password;
}