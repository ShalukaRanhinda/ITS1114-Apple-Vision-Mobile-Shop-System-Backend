package lk.ijse.Apple.Vision.Mobile.Shop.Controller;

import lk.ijse.Apple.Vision.Mobile.Shop.Constant.CommonResponse;
import lk.ijse.Apple.Vision.Mobile.Shop.DTO.AuthDTO;
import lk.ijse.Apple.Vision.Mobile.Shop.DTO.UserDTO;
import lk.ijse.Apple.Vision.Mobile.Shop.DTO.UserDataDTO;
import lk.ijse.Apple.Vision.Mobile.Shop.Security.JwtUtil;
import lk.ijse.Apple.Vision.Mobile.Shop.Service.UserService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static lk.ijse.Apple.Vision.Mobile.Shop.Constant.ResponseMessage.SUCCESS_MESSAGE;
import static lk.ijse.Apple.Vision.Mobile.Shop.Constant.ResponseStatusCode.OPERATION_SUCCESS;

@RestController
@RequestMapping("api/users")
@CrossOrigin
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping(value = "/saveUser", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse saveUser(@RequestBody UserDTO userDTO) {
        UserDTO savedUserDTO = userService.saveUser(userDTO);
        return new CommonResponse(OPERATION_SUCCESS, savedUserDTO, SUCCESS_MESSAGE);
    }

    @PutMapping(value = "/updateUser", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse updateUser(@RequestBody UserDTO userDTO) {
        UserDTO updatedUserDTO = userService.updateUser(userDTO);
        return new CommonResponse(OPERATION_SUCCESS, updatedUserDTO, SUCCESS_MESSAGE);
    }

    @DeleteMapping(value = "/deleteUser/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse deleteUser(@PathVariable Long userId) {
        String deleteUser = userService.deleteUser(userId);
        return new CommonResponse(OPERATION_SUCCESS, deleteUser, SUCCESS_MESSAGE);
    }

    @GetMapping(value = "/getAllUsers", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getAllUsers() {
        List<UserDTO> userDTOList = userService.getAllUsers();
        return new CommonResponse(OPERATION_SUCCESS, userDTOList, SUCCESS_MESSAGE);
    }

    @GetMapping(value = "/getUser/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getUser(@PathVariable Long userId) {
        UserDTO userDTO = userService.getUserById(userId);
        return new CommonResponse(OPERATION_SUCCESS, userDTO, SUCCESS_MESSAGE);
    }
    @PostMapping(value = "/login",produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse login(@RequestBody AuthDTO authDTO) {
        UserDTO userDetails = userService.getUserDetails(authDTO.getUserName(), authDTO.getPassword());
        System.out.println("Login API called for email : " + authDTO.getUserName());
        String token = jwtUtil.generateToken(userDetails);

        UserDataDTO userDataDTO = new UserDataDTO();
        userDataDTO.setUserId(userDetails.getUserId());
        userDataDTO.setToken(token);

        return new CommonResponse(0, userDataDTO, "JWT Token");
    }
}