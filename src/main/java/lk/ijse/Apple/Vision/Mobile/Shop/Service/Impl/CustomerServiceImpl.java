package lk.ijse.Apple.Vision.Mobile.Shop.Service.Impl;

import jakarta.transaction.Transactional;
import lk.ijse.Apple.Vision.Mobile.Shop.DTO.CustomerDTO;
import lk.ijse.Apple.Vision.Mobile.Shop.Entity.Customer;
import lk.ijse.Apple.Vision.Mobile.Shop.Entity.User;
import lk.ijse.Apple.Vision.Mobile.Shop.Enumeration.CustomerStatus;
import lk.ijse.Apple.Vision.Mobile.Shop.Enumeration.UserRole;
import lk.ijse.Apple.Vision.Mobile.Shop.Enumeration.UserStatus;
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

        User user = new User();
        user.setUserName(customerDTO.getUserName());
        user.setPassword(customerDTO.getPassword());
        user.setRole(UserRole.CUSTOMER);
        user.setUserStatus(UserStatus.ACTIVE);

        Customer customer = new Customer();
        customer.setFullName(customerDTO.getFullName());
        customer.setEmail(customerDTO.getEmail());
        customer.setPhoneNumber(customerDTO.getPhoneNumber());
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

        Customer customer = customerRepository.findById(customerDTO.getCustomerId()).orElse(new Customer());

        customer.setFullName(customerDTO.getFullName());
        customer.setEmail(customerDTO.getEmail());
        customer.setPhoneNumber(customerDTO.getPhoneNumber());

        if (customerDTO.getCustomerStatus() != null) {
            customer.setCustomerStatus(customerDTO.getCustomerStatus());
        }

        // Link වී ඇති User details update කිරීම
        if (customer.getUser() != null) {
            User user = customer.getUser();
            if (customerDTO.getUserName() != null && !customerDTO.getUserName().trim().isEmpty()) {
                user.setUserName(customerDTO.getUserName());
            }
            if (customerDTO.getPassword() != null && !customerDTO.getPassword().trim().isEmpty()) {
                user.setPassword(customerDTO.getPassword());
            }
            userRepository.save(user);
        }

        Customer updatedCustomer = customerRepository.save(customer);
        log.info("Customer updated successfully!");

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

        Customer customer = customerRepository.findById(customerId).orElse(null);
        if (customer != null) {
            customer.setCustomerStatus(CustomerStatus.DELETED);

            // User record එකත් soft delete කිරීම
            if (customer.getUser() != null) {
                customer.getUser().setUserStatus(UserStatus.DELETED);
                userRepository.save(customer.getUser());
            }
            customerRepository.save(customer);
        }

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

        Customer customer = customerRepository.findById(customerId).orElse(new Customer());

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