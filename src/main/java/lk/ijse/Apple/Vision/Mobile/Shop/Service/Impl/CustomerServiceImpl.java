package lk.ijse.Apple.Vision.Mobile.Shop.Service.Impl;

import jakarta.transaction.Transactional;
import lk.ijse.Apple.Vision.Mobile.Shop.DTO.CustomerDTO;
import lk.ijse.Apple.Vision.Mobile.Shop.Entity.Customer;
import lk.ijse.Apple.Vision.Mobile.Shop.Entity.User;
import lk.ijse.Apple.Vision.Mobile.Shop.Enumeration.CustomerStatus;
import lk.ijse.Apple.Vision.Mobile.Shop.Enumeration.UserRole;
import lk.ijse.Apple.Vision.Mobile.Shop.Enumeration.UserStatus;
import lk.ijse.Apple.Vision.Mobile.Shop.Exception.CustomException;
import lk.ijse.Apple.Vision.Mobile.Shop.Repository.CustomerRepository;
import lk.ijse.Apple.Vision.Mobile.Shop.Repository.UserRepository;
import lk.ijse.Apple.Vision.Mobile.Shop.Service.CustomerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@Slf4j
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository, UserRepository userRepository) {
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
    }

    @Override
    public CustomerDTO saveCustomer(CustomerDTO customerDTO) {
        log.info("Execute saveCustomer()");

        if (customerDTO == null) {
            throw new CustomException(400, "Customer data cannot be null!");
        }
        if (customerDTO.getFullName() == null || customerDTO.getFullName().trim().isEmpty()) {
            throw new CustomException(400, "Full name cannot be empty!");
        }
        if (customerDTO.getEmail() == null || customerDTO.getEmail().trim().isEmpty()) {
            throw new CustomException(400, "Email cannot be empty!");
        }
        if (customerDTO.getPhoneNumber() == null || customerDTO.getPhoneNumber().trim().isEmpty()) {
            throw new CustomException(400, "Phone number cannot be empty!");
        }
        if (customerDTO.getUserName() == null || customerDTO.getUserName().trim().isEmpty()) {
            throw new CustomException(400, "Username cannot be empty!");
        }
        if (customerDTO.getPassword() == null || customerDTO.getPassword().trim().isEmpty()) {
            throw new CustomException(400, "Password cannot be empty!");
        }

        // Username එක දැනටමත් database එකේ තිබේදැයි පරික්ෂා කිරීම
        if (userRepository.findByUserName(customerDTO.getUserName().trim()).isPresent()) {
            throw new CustomException(400, "Username already exists: " + customerDTO.getUserName());
        }

        User user = new User();
        user.setUserName(customerDTO.getUserName().trim());
        user.setPassword(customerDTO.getPassword().trim());
        user.setRole(UserRole.CUSTOMER);
        user.setUserStatus(UserStatus.ACTIVE);

        Customer customer = new Customer();
        customer.setFullName(customerDTO.getFullName().trim());
        customer.setEmail(customerDTO.getEmail().trim());
        customer.setPhoneNumber(customerDTO.getPhoneNumber().trim());
        customer.setCustomerStatus(CustomerStatus.ACTIVE);

        customer.setUser(user);
        user.setCustomer(customer);

        Customer savedCustomer = customerRepository.save(customer);
        log.info("Customer saved successfully with ID: {}", savedCustomer.getCustomerId());

        customerDTO.setCustomerId(savedCustomer.getCustomerId());
        customerDTO.setCustomerStatus(savedCustomer.getCustomerStatus());
        return customerDTO;
    }

    @Override
    public CustomerDTO updateCustomer(CustomerDTO customerDTO) {
        log.info("Execute updateCustomer()");

        if (customerDTO == null) {
            throw new CustomException(400, "Customer update data cannot be null!");
        }
        if (customerDTO.getCustomerId() == null) {
            throw new CustomException(400, "Customer ID cannot be null for update!");
        }
        if (customerDTO.getFullName() == null || customerDTO.getFullName().trim().isEmpty()) {
            throw new CustomException(400, "Full name cannot be empty!");
        }
        if (customerDTO.getEmail() == null || customerDTO.getEmail().trim().isEmpty()) {
            throw new CustomException(400, "Email cannot be empty!");
        }
        if (customerDTO.getPhoneNumber() == null || customerDTO.getPhoneNumber().trim().isEmpty()) {
            throw new CustomException(400, "Phone number cannot be empty!");
        }

        Customer customer = customerRepository.findById(customerDTO.getCustomerId())
                .orElseThrow(() -> new CustomException(404, "Customer not found with ID: " + customerDTO.getCustomerId()));

        if (customer.getCustomerStatus() == CustomerStatus.DELETED) {
            throw new CustomException(400, "Cannot update a deleted customer!");
        }

        customer.setFullName(customerDTO.getFullName().trim());
        customer.setEmail(customerDTO.getEmail().trim());
        customer.setPhoneNumber(customerDTO.getPhoneNumber().trim());

        if (customerDTO.getCustomerStatus() != null) {
            customer.setCustomerStatus(customerDTO.getCustomerStatus());
        }

        // Link වී ඇති User details update කිරීම සහ username validation
        if (customer.getUser() != null) {
            User user = customer.getUser();
            if (customerDTO.getUserName() != null && !customerDTO.getUserName().trim().isEmpty()) {
                String newUserName = customerDTO.getUserName().trim();
                if (!newUserName.equals(user.getUserName()) && userRepository.findByUserName(newUserName).isPresent()) {
                    throw new CustomException(400, "Username already exists: " + newUserName);
                }
                user.setUserName(newUserName);
            }
            if (customerDTO.getPassword() != null && !customerDTO.getPassword().trim().isEmpty()) {
                user.setPassword(customerDTO.getPassword().trim());
            }
            userRepository.save(user);
        }

        Customer updatedCustomer = customerRepository.save(customer);
        log.info("Customer updated successfully with ID: {}", updatedCustomer.getCustomerId());

        CustomerDTO responseDTO = new CustomerDTO();
        responseDTO.setCustomerId(updatedCustomer.getCustomerId());
        responseDTO.setFullName(updatedCustomer.getFullName());
        responseDTO.setEmail(updatedCustomer.getEmail());
        responseDTO.setPhoneNumber(updatedCustomer.getPhoneNumber());
        responseDTO.setCustomerStatus(updatedCustomer.getCustomerStatus());

        if (updatedCustomer.getUser() != null) {
            responseDTO.setUserName(updatedCustomer.getUser().getUserName());
            responseDTO.setPassword(updatedCustomer.getUser().getPassword());
        }

        return responseDTO;
    }

    @Override
    public String deleteCustomer(Long customerId) {
        log.info("Execute deleteCustomer()");

        if (customerId == null) {
            throw new CustomException(400, "Customer ID cannot be null!");
        }

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomException(404, "Customer not found with ID: " + customerId));

        if (customer.getCustomerStatus() == CustomerStatus.DELETED) {
            throw new CustomException(400, "Customer is already deleted!");
        }

        customer.setCustomerStatus(CustomerStatus.DELETED);

        // Link වූ User record එකත් soft delete කිරීම
        if (customer.getUser() != null) {
            customer.getUser().setUserStatus(UserStatus.DELETED);
            userRepository.save(customer.getUser());
        }

        customerRepository.save(customer);
        log.info("Customer marked as DELETED for ID: {}", customerId);

        return "Customer deleted successfully!";
    }

    @Override
    public List<CustomerDTO> getAllCustomers() {
        log.info("Execute getAllCustomers()");

        List<Customer> customerList = customerRepository.findAll();
        List<CustomerDTO> responseList = new ArrayList<>();

        for (Customer customer : customerList) {
            if (customer.getCustomerStatus() != CustomerStatus.DELETED) {
                CustomerDTO customerDTO = new CustomerDTO();
                customerDTO.setCustomerId(customer.getCustomerId());
                customerDTO.setFullName(customer.getFullName());
                customerDTO.setEmail(customer.getEmail());
                customerDTO.setPhoneNumber(customer.getPhoneNumber());
                customerDTO.setCustomerStatus(customer.getCustomerStatus());

                if (customer.getUser() != null) {
                    customerDTO.setUserName(customer.getUser().getUserName());
                    customerDTO.setPassword(customer.getUser().getPassword());
                }

                responseList.add(customerDTO);
            }
        }
        return responseList;
    }

    @Override
    public CustomerDTO getCustomerById(Long customerId) {
        log.info("Execute getCustomerById()");

        if (customerId == null) {
            throw new CustomException(400, "Customer ID cannot be null!");
        }

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomException(404, "Customer not found with ID: " + customerId));

        if (customer.getCustomerStatus() == CustomerStatus.DELETED) {
            throw new CustomException(404, "Customer not found or has been deleted!");
        }

        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setCustomerId(customer.getCustomerId());
        customerDTO.setFullName(customer.getFullName());
        customerDTO.setEmail(customer.getEmail());
        customerDTO.setPhoneNumber(customer.getPhoneNumber());
        customerDTO.setCustomerStatus(customer.getCustomerStatus());

        if (customer.getUser() != null) {
            customerDTO.setUserName(customer.getUser().getUserName());
            customerDTO.setPassword(customer.getUser().getPassword());
        }

        return customerDTO;
    }
}