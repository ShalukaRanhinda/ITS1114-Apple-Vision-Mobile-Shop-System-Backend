package lk.ijse.Apple.Vision.Mobile.Shop.Service.Impl;

import jakarta.transaction.Transactional;
import lk.ijse.Apple.Vision.Mobile.Shop.DTO.CustomerDTO;
import lk.ijse.Apple.Vision.Mobile.Shop.DTO.UserDTO;
import lk.ijse.Apple.Vision.Mobile.Shop.Entity.Customer;
import lk.ijse.Apple.Vision.Mobile.Shop.Entity.User;
import lk.ijse.Apple.Vision.Mobile.Shop.Enumeration.CustomerStatus;
import lk.ijse.Apple.Vision.Mobile.Shop.Enumeration.UserRole;
import lk.ijse.Apple.Vision.Mobile.Shop.Enumeration.UserStatus;
import lk.ijse.Apple.Vision.Mobile.Shop.Repository.UserRepository;
import lk.ijse.Apple.Vision.Mobile.Shop.Service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDTO saveUser(UserDTO userDTO) {
        log.info("Execute Save User!");

        User user = new User();
        user.setUserName(userDTO.getUserName());
        user.setPassword(userDTO.getPassword());
        user.setRole(userDTO.getRole());
        user.setUserStatus(UserStatus.ACTIVE);

        if (userDTO.getRole() == UserRole.CUSTOMER && userDTO.getCustomerDTO() != null) {
            Customer customer = new Customer();
            customer.setFullName(userDTO.getCustomerDTO().getFullName());
            customer.setEmail(userDTO.getCustomerDTO().getEmail());
            customer.setPhoneNumber(userDTO.getCustomerDTO().getPhoneNumber());
            customer.setCustomerStatus(CustomerStatus.ACTIVE);

            customer.setUser(user);
            user.setCustomer(customer);
        }

        User savedUser = userRepository.save(user);
        log.info("User saved!");

        userDTO.setUserId(savedUser.getUserId());
        userDTO.setUserStatus(savedUser.getUserStatus());
        if (savedUser.getCustomer() != null && userDTO.getCustomerDTO() != null) {
            userDTO.getCustomerDTO().setCustomerId(savedUser.getCustomer().getCustomerId());
            userDTO.getCustomerDTO().setCustomerStatus(savedUser.getCustomer().getCustomerStatus());
        }
        return userDTO;
    }

    @Override
    public UserDTO updateUser(UserDTO userDTO) {
        log.info("Execute Update User!");

        User user = userRepository.findById(userDTO.getUserId()).orElse(new User());

        user.setUserName(userDTO.getUserName());
        user.setPassword(userDTO.getPassword());
        user.setRole(userDTO.getRole());
        if (userDTO.getUserStatus() != null) {
            user.setUserStatus(userDTO.getUserStatus());
        }

        if (userDTO.getRole() == UserRole.CUSTOMER && userDTO.getCustomerDTO() != null) {
            Customer customer = user.getCustomer();

            if (customer == null) {
                customer = new Customer();
                customer.setUser(user);
                user.setCustomer(customer);
            }

            customer.setFullName(userDTO.getCustomerDTO().getFullName());
            customer.setEmail(userDTO.getCustomerDTO().getEmail());
            customer.setPhoneNumber(userDTO.getCustomerDTO().getPhoneNumber());
            customer.setCustomerStatus(CustomerStatus.ACTIVE);
        } else {
            user.setCustomer(null);
        }

        User updatedUser = userRepository.save(user);

        UserDTO responseDTO = new UserDTO();
        responseDTO.setUserId(updatedUser.getUserId());
        responseDTO.setUserName(updatedUser.getUserName());
        responseDTO.setPassword(updatedUser.getPassword());
        responseDTO.setRole(updatedUser.getRole());
        responseDTO.setUserStatus(updatedUser.getUserStatus());

        if (updatedUser.getRole() == UserRole.CUSTOMER && updatedUser.getCustomer() != null) {
            CustomerDTO customerDTO = new CustomerDTO();
            customerDTO.setCustomerId(updatedUser.getCustomer().getCustomerId());
            customerDTO.setFullName(updatedUser.getCustomer().getFullName());
            customerDTO.setEmail(updatedUser.getCustomer().getEmail());
            customerDTO.setPhoneNumber(updatedUser.getCustomer().getPhoneNumber());
            customerDTO.setCustomerStatus(updatedUser.getCustomer().getCustomerStatus());

            responseDTO.setCustomerDTO(customerDTO);
        }

        return responseDTO;
    }

    @Override
    public String deleteUser(Long userId) {
        log.info("Execute Delete User");

        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            user.setUserStatus(UserStatus.DELETED);
            if (user.getCustomer() != null) {
                user.getCustomer().setCustomerStatus(CustomerStatus.DELETED);
            }
            userRepository.save(user);
        }

        return "User deleted successfully!";
    }

    @Override
    public List<UserDTO> getAllUsers() {
        log.info("Execute Get All Users");
        List<UserDTO> responseList = new ArrayList<>();
        List<User> usersList = userRepository.findAll();

        for (User user : usersList) {
            if (user.getUserStatus() == UserStatus.DELETED) {
                continue;
            }

            UserDTO userDTO = new UserDTO();
            userDTO.setUserId(user.getUserId());
            userDTO.setUserName(user.getUserName());
            userDTO.setPassword(user.getPassword());
            userDTO.setRole(user.getRole());
            userDTO.setUserStatus(user.getUserStatus());

            if (user.getRole() == UserRole.CUSTOMER && user.getCustomer() != null) {
                Customer customer = user.getCustomer();
                if (customer.getCustomerStatus() != CustomerStatus.DELETED) {
                    CustomerDTO customerDTO = new CustomerDTO();
                    customerDTO.setCustomerId(customer.getCustomerId());
                    customerDTO.setFullName(customer.getFullName());
                    customerDTO.setEmail(customer.getEmail());
                    customerDTO.setPhoneNumber(customer.getPhoneNumber());
                    customerDTO.setCustomerStatus(customer.getCustomerStatus());

                    userDTO.setCustomerDTO(customerDTO);
                }
            }
            responseList.add(userDTO);
        }
        return responseList;
    }

    @Override
    public UserDTO getUserById(Long userId) {
        log.info("Execute Get User");

        User user = userRepository.findById(userId).orElse(new User());

        UserDTO userDTO = new UserDTO();
        userDTO.setUserId(user.getUserId());
        userDTO.setUserName(user.getUserName());
        userDTO.setPassword(user.getPassword());
        userDTO.setRole(user.getRole());
        userDTO.setUserStatus(user.getUserStatus());

        if (user.getRole() == UserRole.CUSTOMER && user.getCustomer() != null) {
            Customer customer = user.getCustomer();
            if (customer.getCustomerStatus() != CustomerStatus.DELETED) {
                CustomerDTO customerDTO = new CustomerDTO();
                customerDTO.setCustomerId(customer.getCustomerId());
                customerDTO.setFullName(customer.getFullName());
                customerDTO.setEmail(customer.getEmail());
                customerDTO.setPhoneNumber(customer.getPhoneNumber());
                customerDTO.setCustomerStatus(customer.getCustomerStatus());

                userDTO.setCustomerDTO(customerDTO);
            }
        }
        return userDTO;
    }
}