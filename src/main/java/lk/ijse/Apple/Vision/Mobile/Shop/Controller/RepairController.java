package lk.ijse.Apple.Vision.Mobile.Shop.Controller;

import lk.ijse.Apple.Vision.Mobile.Shop.Constant.CommonResponse;
import lk.ijse.Apple.Vision.Mobile.Shop.DTO.RepairDTO;
import lk.ijse.Apple.Vision.Mobile.Shop.Service.RepairService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static lk.ijse.Apple.Vision.Mobile.Shop.Constant.ResponseMessage.SUCCESS_MESSAGE;
import static lk.ijse.Apple.Vision.Mobile.Shop.Constant.ResponseStatusCode.OPERATION_SUCCESS;

@RestController
@RequestMapping("api/repairs")
@CrossOrigin
public class RepairController {

    private final RepairService repairService;

    public RepairController(RepairService repairService) {
        this.repairService = repairService;
    }

    @PostMapping(value = "/saveRepair", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse saveRepair(@RequestBody RepairDTO repairDTO) {
        RepairDTO savedDTO = repairService.saveRepair(repairDTO);
        return new CommonResponse(OPERATION_SUCCESS, savedDTO, SUCCESS_MESSAGE);
    }

    @PutMapping(value = "/updateRepair", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse updateRepair(@RequestBody RepairDTO repairDTO) {
        RepairDTO updatedDTO = repairService.updateRepair(repairDTO);
        return new CommonResponse(OPERATION_SUCCESS, updatedDTO, SUCCESS_MESSAGE);
    }

    @DeleteMapping(value = "/deleteRepair/{repairId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse deleteRepair(@PathVariable Long repairId) {
        String message = repairService.deleteRepair(repairId);
        return new CommonResponse(OPERATION_SUCCESS, message, SUCCESS_MESSAGE);
    }

    @GetMapping(value = "/getAllRepairs", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getAllRepairs() {
        List<RepairDTO> repairList = repairService.getAllRepairs();
        return new CommonResponse(OPERATION_SUCCESS, repairList, SUCCESS_MESSAGE);
    }

    @GetMapping(value = "/getRepair/{repairId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getRepair(@PathVariable Long repairId) {
        RepairDTO repairDTO = repairService.getRepairById(repairId);
        return new CommonResponse(OPERATION_SUCCESS, repairDTO, SUCCESS_MESSAGE);
    }

    @GetMapping(value = "/getByCustomer/{customerId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getRepairsByCustomer(@PathVariable Long customerId) {
        List<RepairDTO> repairList = repairService.getRepairsByCustomerId(customerId);
        return new CommonResponse(OPERATION_SUCCESS, repairList, SUCCESS_MESSAGE);
    }
}