package lk.ijse.Apple.Vision.Mobile.Shop.Controller;

import lk.ijse.Apple.Vision.Mobile.Shop.Constant.CommonResponse;
import lk.ijse.Apple.Vision.Mobile.Shop.DTO.PurchaseOrderDetailsDTO;
import lk.ijse.Apple.Vision.Mobile.Shop.Service.PurchaseOrderDetailsService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static lk.ijse.Apple.Vision.Mobile.Shop.Constant.ResponseMessage.SUCCESS_MESSAGE;
import static lk.ijse.Apple.Vision.Mobile.Shop.Constant.ResponseStatusCode.OPERATION_SUCCESS;

@RestController
@RequestMapping("api/purchase-order-details")
@CrossOrigin
public class PurchaseOrderDetailsController {

    private final PurchaseOrderDetailsService purchaseOrderDetailsService;

    public PurchaseOrderDetailsController(PurchaseOrderDetailsService purchaseOrderDetailsService) {
        this.purchaseOrderDetailsService = purchaseOrderDetailsService;
    }

    @PostMapping(value = "/saveDetail/{purchaseOrderId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse savePurchaseOrderDetail(@PathVariable Long purchaseOrderId, @RequestBody PurchaseOrderDetailsDTO dto) {
        PurchaseOrderDetailsDTO savedDTO = purchaseOrderDetailsService.savePurchaseOrderDetail(purchaseOrderId, dto);
        return new CommonResponse(OPERATION_SUCCESS, savedDTO, SUCCESS_MESSAGE);
    }

    @PutMapping(value = "/updateDetail", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse updatePurchaseOrderDetail(@RequestBody PurchaseOrderDetailsDTO dto) {
        PurchaseOrderDetailsDTO updatedDTO = purchaseOrderDetailsService.updatePurchaseOrderDetail(dto);
        return new CommonResponse(OPERATION_SUCCESS, updatedDTO, SUCCESS_MESSAGE);
    }

    @DeleteMapping(value = "/deleteDetail/{poDetailsId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse deletePurchaseOrderDetail(@PathVariable Long poDetailsId) {
        String message = purchaseOrderDetailsService.deletePurchaseOrderDetail(poDetailsId);
        return new CommonResponse(OPERATION_SUCCESS, message, SUCCESS_MESSAGE);
    }

    @GetMapping(value = "/getAllDetails", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getAllPurchaseOrderDetails() {
        List<PurchaseOrderDetailsDTO> list = purchaseOrderDetailsService.getAllPurchaseOrderDetails();
        return new CommonResponse(OPERATION_SUCCESS, list, SUCCESS_MESSAGE);
    }

    @GetMapping(value = "/getDetail/{poDetailsId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getPurchaseOrderDetail(@PathVariable Long poDetailsId) {
        PurchaseOrderDetailsDTO dto = purchaseOrderDetailsService.getPurchaseOrderDetailById(poDetailsId);
        return new CommonResponse(OPERATION_SUCCESS, dto, SUCCESS_MESSAGE);
    }

    @GetMapping(value = "/getByPurchaseOrder/{purchaseOrderId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getDetailsByPurchaseOrder(@PathVariable Long purchaseOrderId) {
        List<PurchaseOrderDetailsDTO> list = purchaseOrderDetailsService.getDetailsByPurchaseOrderId(purchaseOrderId);
        return new CommonResponse(OPERATION_SUCCESS, list, SUCCESS_MESSAGE);
    }
}