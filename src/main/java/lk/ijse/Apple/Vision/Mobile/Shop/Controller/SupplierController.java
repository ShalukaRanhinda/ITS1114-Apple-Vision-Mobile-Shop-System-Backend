package lk.ijse.Apple.Vision.Mobile.Shop.Controller;

import lk.ijse.Apple.Vision.Mobile.Shop.Constant.CommonResponse;
import lk.ijse.Apple.Vision.Mobile.Shop.DTO.SupplierDTO;
import lk.ijse.Apple.Vision.Mobile.Shop.Service.SupplierService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static lk.ijse.Apple.Vision.Mobile.Shop.Constant.ResponseMessage.SUCCESS_MESSAGE;
import static lk.ijse.Apple.Vision.Mobile.Shop.Constant.ResponseStatusCode.OPERATION_SUCCESS;

@RestController
@RequestMapping("api/suppliers")
public class SupplierController {

    private final SupplierService supplierService;

    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @PostMapping(value = "/saveSupplier", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse saveSupplier(@RequestBody SupplierDTO supplierDTO) {
        SupplierDTO savedSupplierDTO = supplierService.saveSupplier(supplierDTO);
        return new CommonResponse(OPERATION_SUCCESS, savedSupplierDTO, SUCCESS_MESSAGE);
    }

    @PutMapping(value = "/updateSupplier", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse updateSupplier(@RequestBody SupplierDTO supplierDTO) {
        SupplierDTO updatedSupplierDTO = supplierService.updateSupplier(supplierDTO);
        return new CommonResponse(OPERATION_SUCCESS, updatedSupplierDTO, SUCCESS_MESSAGE);
    }

    @DeleteMapping(value = "/deleteSupplier/{supplierId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse deleteSupplier(@PathVariable Long supplierId) {
        String message = supplierService.deleteSupplier(supplierId);
        return new CommonResponse(OPERATION_SUCCESS, message, SUCCESS_MESSAGE);
    }

    @GetMapping(value = "/getAllSuppliers", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getAllSuppliers() {
        List<SupplierDTO> supplierList = supplierService.getAllSuppliers();
        return new CommonResponse(OPERATION_SUCCESS, supplierList, SUCCESS_MESSAGE);
    }

    @GetMapping(value = "/getSupplier/{supplierId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getSupplier(@PathVariable Long supplierId) {
        SupplierDTO supplierDTO = supplierService.getSupplierById(supplierId);
        return new CommonResponse(OPERATION_SUCCESS, supplierDTO, SUCCESS_MESSAGE);
    }
}