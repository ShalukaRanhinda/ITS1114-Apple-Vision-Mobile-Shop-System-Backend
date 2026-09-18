package lk.ijse.Apple.Vision.Mobile.Shop.Service;

import lk.ijse.Apple.Vision.Mobile.Shop.DTO.UserDTO;
import java.util.List;

public interface UserService {
    UserDTO saveUser(UserDTO userDTO);
    UserDTO updateUser(UserDTO userDTO);
    String deleteUser(Long userId);
    List<UserDTO> getAllUsers();
    UserDTO getUserById(Long userId);

    UserDTO getUserDetails(String userName, String password);
}