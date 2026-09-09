package lk.ijse.Apple.Vision.Mobile.Shop.DTO;

import lk.ijse.Apple.Vision.Mobile.Shop.Enumeration.UserRole;
import lk.ijse.Apple.Vision.Mobile.Shop.Enumeration.UserStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long userId;
    private String userName;
    private String password;
    private UserRole role;
    private UserStatus userStatus;

    private CustomerDTO customerDTO;
}