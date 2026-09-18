package lk.ijse.Apple.Vision.Mobile.Shop.Service.Impl;

import jakarta.transaction.Transactional;
import lk.ijse.Apple.Vision.Mobile.Shop.DTO.CustomerDTO;
import lk.ijse.Apple.Vision.Mobile.Shop.DTO.UserDTO;
import lk.ijse.Apple.Vision.Mobile.Shop.Entity.Customer;
import lk.ijse.Apple.Vision.Mobile.Shop.Entity.User;
import lk.ijse.Apple.Vision.Mobile.Shop.Enumeration.CustomerStatus;
import lk.ijse.Apple.Vision.Mobile.Shop.Enumeration.UserRole;
import lk.ijse.Apple.Vision.Mobile.Shop.Enumeration.UserStatus;
import lk.ijse.Apple.Vision.Mobile.Shop.Exception.CustomException;
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
        log.info("Execute saveUser()");

        if (userDTO == null) {
            throw new CustomException(400, "User data cannot be null!");
        }
        if (userDTO.getUserName() == null || userDTO.getUserName().trim().isEmpty()) {
            throw new CustomException(400, "Username cannot be empty!");
        }
        if (userDTO.getPassword() == null || userDTO.getPassword().trim().isEmpty()) {
            throw new CustomException(400, "Password cannot be empty!");
        }
        if (userDTO.getRole() == null) {
            throw new CustomException(400, "User role cannot be null!");
        }

        // Username duplicate check
        if (userRepository.findByUserName(userDTO.getUserName().trim()).isPresent()) {
            throw new CustomException(400, "Username already exists: " + userDTO.getUserName());
        }

        User user = new User();
        user.setUserName(userDTO.getUserName().trim());
        user.setPassword(userDTO.getPassword().trim());
        user.setRole(userDTO.getRole());
        user.setUserStatus(UserStatus.ACTIVE);

        if (userDTO.getRole() == UserRole.CUSTOMER) {
            if (userDTO.getCustomerDTO() == null) {
                throw new CustomException(400, "Customer details are required for role CUSTOMER!");
            }
            if (userDTO.getCustomerDTO().getFullName() == null || userDTO.getCustomerDTO().getFullName().trim().isEmpty()) {
                throw new CustomException(400, "Customer full name cannot be empty!");
            }
            if (userDTO.getCustomerDTO().getEmail() == null || userDTO.getCustomerDTO().getEmail().trim().isEmpty()) {
                throw new CustomException(400, "Customer email cannot be empty!");
            }
            if (userDTO.getCustomerDTO().getPhoneNumber() == null || userDTO.getCustomerDTO().getPhoneNumber().trim().isEmpty()) {
                throw new CustomException(400, "Customer phone number cannot be empty!");
            }

            Customer customer = new Customer();
            customer.setFullName(userDTO.getCustomerDTO().getFullName().trim());
            customer.setEmail(userDTO.getCustomerDTO().getEmail().trim());
            customer.setPhoneNumber(userDTO.getCustomerDTO().getPhoneNumber().trim());
            customer.setCustomerStatus(CustomerStatus.ACTIVE);

            customer.setUser(user);
            user.setCustomer(customer);
        }

        User savedUser = userRepository.save(user);
        log.info("User saved successfully with ID: {}", savedUser.getUserId());

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
        log.info("Execute updateUser()");

        if (userDTO == null) {
            throw new CustomException(400, "User update data cannot be null!");
        }
        if (userDTO.getUserId() == null) {
            throw new CustomException(400, "User ID cannot be null for update!");
        }
        if (userDTO.getUserName() == null || userDTO.getUserName().trim().isEmpty()) {
            throw new CustomException(400, "Username cannot be empty!");
        }
        if (userDTO.getRole() == null) {
            throw new CustomException(400, "User role cannot be null!");
        }

        User user = userRepository.findById(userDTO.getUserId())
                .orElseThrow(() -> new CustomException(404, "User not found with ID: " + userDTO.getUserId()));

        if (user.getUserStatus() == UserStatus.DELETED) {
            throw new CustomException(400, "Cannot update a deleted user!");
        }

        // Check if new username belongs to another account
        String trimmedUserName = userDTO.getUserName().trim();
        if (!trimmedUserName.equals(user.getUserName()) && userRepository.findByUserName(trimmedUserName).isPresent()) {
            throw new CustomException(400, "Username already exists: " + trimmedUserName);
        }

        user.setUserName(trimmedUserName);
        if (userDTO.getPassword() != null && !userDTO.getPassword().trim().isEmpty()) {
            user.setPassword(userDTO.getPassword().trim());
        }
        user.setRole(userDTO.getRole());

        if (userDTO.getUserStatus() != null) {
            user.setUserStatus(userDTO.getUserStatus());
        }

        if (userDTO.getRole() == UserRole.CUSTOMER) {
            if (userDTO.getCustomerDTO() == null) {
                throw new CustomException(400, "Customer details are required for role CUSTOMER!");
            }

            Customer customer = user.getCustomer();
            if (customer == null) {
                customer = new Customer();
                customer.setUser(user);
                user.setCustomer(customer);
            }

            if (userDTO.getCustomerDTO().getFullName() != null && !userDTO.getCustomerDTO().getFullName().trim().isEmpty()) {
                customer.setFullName(userDTO.getCustomerDTO().getFullName().trim());
            }
            if (userDTO.getCustomerDTO().getEmail() != null && !userDTO.getCustomerDTO().getEmail().trim().isEmpty()) {
                customer.setEmail(userDTO.getCustomerDTO().getEmail().trim());
            }
            if (userDTO.getCustomerDTO().getPhoneNumber() != null && !userDTO.getCustomerDTO().getPhoneNumber().trim().isEmpty()) {
                customer.setPhoneNumber(userDTO.getCustomerDTO().getPhoneNumber().trim());
            }
            customer.setCustomerStatus(CustomerStatus.ACTIVE);
        } else {
            // If role changed from CUSTOMER to another role, dissociate customer profile
            if (user.getCustomer() != null) {
                user.getCustomer().setCustomerStatus(CustomerStatus.DELETED);
                user.setCustomer(null);
            }
        }

        User updatedUser = userRepository.save(user);
        log.info("User updated successfully with ID: {}", updatedUser.getUserId());

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
        log.info("Execute deleteUser()");

        if (userId == null) {
            throw new CustomException(400, "User ID cannot be null!");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(404, "User not found with ID: " + userId));

        if (user.getUserStatus() == UserStatus.DELETED) {
            throw new CustomException(400, "User is already deleted!");
        }

        user.setUserStatus(UserStatus.DELETED);
        if (user.getCustomer() != null) {
            user.getCustomer().setCustomerStatus(CustomerStatus.DELETED);
        }
        userRepository.save(user);
        log.info("User marked as DELETED for ID: {}", userId);

        return "User deleted successfully!";
    }

    @Override
    public List<UserDTO> getAllUsers() {
        log.info("Execute getAllUsers()");

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
        log.info("Execute getUserById()");

        if (userId == null) {
            throw new CustomException(400, "User ID cannot be null!");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(404, "User not found with ID: " + userId));

        if (user.getUserStatus() == UserStatus.DELETED) {
            throw new CustomException(404, "User not found or has been deleted!");
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
        return userDTO;
    }
}