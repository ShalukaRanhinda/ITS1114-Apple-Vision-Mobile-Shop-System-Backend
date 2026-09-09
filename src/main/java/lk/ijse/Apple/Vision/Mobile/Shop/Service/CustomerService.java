package lk.ijse.Apple.Vision.Mobile.Shop.Service;

import lk.ijse.Apple.Vision.Mobile.Shop.DTO.CustomerDTO;
import java.util.List;

public interface CustomerService {
    CustomerDTO saveCustomer(CustomerDTO customerDTO);
    CustomerDTO updateCustomer(CustomerDTO customerDTO);
    String deleteCustomer(Long customerId);
    List<CustomerDTO> getAllCustomers();
    CustomerDTO getCustomerById(Long customerId);
}