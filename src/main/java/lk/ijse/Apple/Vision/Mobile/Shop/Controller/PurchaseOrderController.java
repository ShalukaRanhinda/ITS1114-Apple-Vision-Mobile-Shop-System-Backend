package lk.ijse.Apple.Vision.Mobile.Shop.Controller;

import lk.ijse.Apple.Vision.Mobile.Shop.Constant.CommonResponse;
import lk.ijse.Apple.Vision.Mobile.Shop.DTO.PurchaseOrderDTO;
import lk.ijse.Apple.Vision.Mobile.Shop.Service.PurchaseOrderService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static lk.ijse.Apple.Vision.Mobile.Shop.Constant.ResponseMessage.SUCCESS_MESSAGE;
import static lk.ijse.Apple.Vision.Mobile.Shop.Constant.ResponseStatusCode.OPERATION_SUCCESS;

@RestController
@RequestMapping("api/purchase-orders")
@CrossOrigin
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;

    public PurchaseOrderController(PurchaseOrderService purchaseOrderService) {
        this.purchaseOrderService = purchaseOrderService;
    }

    @PostMapping(value = "/placePurchaseOrder", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse placePurchaseOrder(@RequestBody PurchaseOrderDTO purchaseOrderDTO) {
        PurchaseOrderDTO savedDTO = purchaseOrderService.placePurchaseOrder(purchaseOrderDTO);
        return new CommonResponse(OPERATION_SUCCESS, savedDTO, SUCCESS_MESSAGE);
    }

    @PutMapping(value = "/cancelPurchaseOrder/{purchaseOrderId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse cancelPurchaseOrder(@PathVariable Long purchaseOrderId) {
        String message = purchaseOrderService.cancelPurchaseOrder(purchaseOrderId);
        return new CommonResponse(OPERATION_SUCCESS, message, SUCCESS_MESSAGE);
    }

    @GetMapping(value = "/getAllPurchaseOrders", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getAllPurchaseOrders() {
        List<PurchaseOrderDTO> list = purchaseOrderService.getAllPurchaseOrders();
        return new CommonResponse(OPERATION_SUCCESS, list, SUCCESS_MESSAGE);
    }

    @GetMapping(value = "/getPurchaseOrder/{purchaseOrderId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getPurchaseOrder(@PathVariable Long purchaseOrderId) {
        PurchaseOrderDTO dto = purchaseOrderService.getPurchaseOrderById(purchaseOrderId);
        return new CommonResponse(OPERATION_SUCCESS, dto, SUCCESS_MESSAGE);
    }

    @GetMapping(value = "/getBySupplier/{supplierId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getPurchaseOrdersBySupplier(@PathVariable Long supplierId) {
        List<PurchaseOrderDTO> list = purchaseOrderService.getPurchaseOrdersBySupplierId(supplierId);
        return new CommonResponse(OPERATION_SUCCESS, list, SUCCESS_MESSAGE);
    }
}