package lk.ijse.Apple.Vision.Mobile.Shop.Controller;

import lk.ijse.Apple.Vision.Mobile.Shop.Constant.CommonResponse;
import lk.ijse.Apple.Vision.Mobile.Shop.DTO.WarrantyDTO;
import lk.ijse.Apple.Vision.Mobile.Shop.Service.WarrantyService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static lk.ijse.Apple.Vision.Mobile.Shop.Constant.ResponseMessage.SUCCESS_MESSAGE;
import static lk.ijse.Apple.Vision.Mobile.Shop.Constant.ResponseStatusCode.OPERATION_SUCCESS;

@RestController
@RequestMapping("api/warranties")
@CrossOrigin
public class WarrantyController {

    private final WarrantyService warrantyService;

    public WarrantyController(WarrantyService warrantyService) {
        this.warrantyService = warrantyService;
    }

    @PostMapping(value = "/saveWarranty", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse saveWarranty(@RequestBody WarrantyDTO warrantyDTO) {
        WarrantyDTO savedDTO = warrantyService.saveWarranty(warrantyDTO);
        return new CommonResponse(OPERATION_SUCCESS, savedDTO, SUCCESS_MESSAGE);
    }

    @PutMapping(value = "/updateWarranty", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse updateWarranty(@RequestBody WarrantyDTO warrantyDTO) {
        WarrantyDTO updatedDTO = warrantyService.updateWarranty(warrantyDTO);
        return new CommonResponse(OPERATION_SUCCESS, updatedDTO, SUCCESS_MESSAGE);
    }

    @DeleteMapping(value = "/deleteWarranty/{warrantyId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse deleteWarranty(@PathVariable Long warrantyId) {
        String message = warrantyService.deleteWarranty(warrantyId);
        return new CommonResponse(OPERATION_SUCCESS, message, SUCCESS_MESSAGE);
    }

    @GetMapping(value = "/getAllWarranties", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getAllWarranties() {
        List<WarrantyDTO> list = warrantyService.getAllWarranties();
        return new CommonResponse(OPERATION_SUCCESS, list, SUCCESS_MESSAGE);
    }

    @GetMapping(value = "/getWarranty/{warrantyId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getWarranty(@PathVariable Long warrantyId) {
        WarrantyDTO dto = warrantyService.getWarrantyById(warrantyId);
        return new CommonResponse(OPERATION_SUCCESS, dto, SUCCESS_MESSAGE);
    }

    @GetMapping(value = "/getBySerial/{serialNumber}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getWarrantyBySerial(@PathVariable String serialNumber) {
        WarrantyDTO dto = warrantyService.getWarrantyBySerialNumber(serialNumber);
        return new CommonResponse(OPERATION_SUCCESS, dto, SUCCESS_MESSAGE);
    }

    @GetMapping(value = "/getByOrder/{orderId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getWarrantiesByOrder(@PathVariable Long orderId) {
        List<WarrantyDTO> list = warrantyService.getWarrantiesByOrderId(orderId);
        return new CommonResponse(OPERATION_SUCCESS, list, SUCCESS_MESSAGE);
    }
}