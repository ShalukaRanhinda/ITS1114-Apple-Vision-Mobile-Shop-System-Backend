package lk.ijse.Apple.Vision.Mobile.Shop.Controller;

import lk.ijse.Apple.Vision.Mobile.Shop.Constant.CommonResponse;
import lk.ijse.Apple.Vision.Mobile.Shop.DTO.CustomerDTO;
import lk.ijse.Apple.Vision.Mobile.Shop.Service.CustomerService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static lk.ijse.Apple.Vision.Mobile.Shop.Constant.ResponseMessage.SUCCESS_MESSAGE;
import static lk.ijse.Apple.Vision.Mobile.Shop.Constant.ResponseStatusCode.OPERATION_SUCCESS;

@RestController
@RequestMapping("api/customers")
@CrossOrigin
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping(value = "/saveCustomer", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse saveCustomer(@RequestBody CustomerDTO customerDTO) {
        CustomerDTO savedCustomerDTO = customerService.saveCustomer(customerDTO);
        return new CommonResponse(OPERATION_SUCCESS, savedCustomerDTO, SUCCESS_MESSAGE);
    }

    @PutMapping(value = "/updateCustomer", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse updateCustomer(@RequestBody CustomerDTO customerDTO) {
        CustomerDTO updatedCustomerDTO = customerService.updateCustomer(customerDTO);
        return new CommonResponse(OPERATION_SUCCESS, updatedCustomerDTO, SUCCESS_MESSAGE);
    }

    @DeleteMapping(value = "/deleteCustomer/{customerId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse deleteCustomer(@PathVariable Long customerId) {
        String message = customerService.deleteCustomer(customerId);
        return new CommonResponse(OPERATION_SUCCESS, message, SUCCESS_MESSAGE);
    }

    @GetMapping(value = "/getAllCustomers", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getAllCustomers() {
        List<CustomerDTO> customerList = customerService.getAllCustomers();
        return new CommonResponse(OPERATION_SUCCESS, customerList, SUCCESS_MESSAGE);
    }

    @GetMapping(value = "/getCustomer/{customerId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getCustomer(@PathVariable Long customerId) {
        CustomerDTO customerDTO = customerService.getCustomerById(customerId);
        return new CommonResponse(OPERATION_SUCCESS, customerDTO, SUCCESS_MESSAGE);
    }
}