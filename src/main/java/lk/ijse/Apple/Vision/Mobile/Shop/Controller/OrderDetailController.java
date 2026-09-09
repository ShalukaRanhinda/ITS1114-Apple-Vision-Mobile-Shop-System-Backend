package lk.ijse.Apple.Vision.Mobile.Shop.Controller;

import lk.ijse.Apple.Vision.Mobile.Shop.Constant.CommonResponse;
import lk.ijse.Apple.Vision.Mobile.Shop.DTO.OrderDetailDTO;
import lk.ijse.Apple.Vision.Mobile.Shop.Service.OrderDetailService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static lk.ijse.Apple.Vision.Mobile.Shop.Constant.ResponseMessage.SUCCESS_MESSAGE;
import static lk.ijse.Apple.Vision.Mobile.Shop.Constant.ResponseStatusCode.OPERATION_SUCCESS;

@RestController
@RequestMapping("api/order-details")
public class OrderDetailController {

    private final OrderDetailService orderDetailService;

    public OrderDetailController(OrderDetailService orderDetailService) {
        this.orderDetailService = orderDetailService;
    }

    @PostMapping(value = "/saveOrderDetail/{orderId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse saveOrderDetail(@PathVariable Long orderId, @RequestBody OrderDetailDTO orderDetailDTO) {
        OrderDetailDTO savedDTO = orderDetailService.saveOrderDetail(orderId, orderDetailDTO);
        return new CommonResponse(OPERATION_SUCCESS, savedDTO, SUCCESS_MESSAGE);
    }

    @PutMapping(value = "/updateOrderDetail", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse updateOrderDetail(@RequestBody OrderDetailDTO orderDetailDTO) {
        OrderDetailDTO updatedDTO = orderDetailService.updateOrderDetail(orderDetailDTO);
        return new CommonResponse(OPERATION_SUCCESS, updatedDTO, SUCCESS_MESSAGE);
    }

    @DeleteMapping(value = "/deleteOrderDetail/{orderDetailId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse deleteOrderDetail(@PathVariable Long orderDetailId) {
        String message = orderDetailService.deleteOrderDetail(orderDetailId);
        return new CommonResponse(OPERATION_SUCCESS, message, SUCCESS_MESSAGE);
    }

    @GetMapping(value = "/getAllOrderDetails", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getAllOrderDetails() {
        List<OrderDetailDTO> dtoList = orderDetailService.getAllOrderDetails();
        return new CommonResponse(OPERATION_SUCCESS, dtoList, SUCCESS_MESSAGE);
    }

    @GetMapping(value = "/getOrderDetail/{orderDetailId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getOrderDetail(@PathVariable Long orderDetailId) {
        OrderDetailDTO dto = orderDetailService.getOrderDetailById(orderDetailId);
        return new CommonResponse(OPERATION_SUCCESS, dto, SUCCESS_MESSAGE);
    }

    @GetMapping(value = "/getByOrder/{orderId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getOrderDetailsByOrder(@PathVariable Long orderId) {
        List<OrderDetailDTO> dtoList = orderDetailService.getOrderDetailsByOrderId(orderId);
        return new CommonResponse(OPERATION_SUCCESS, dtoList, SUCCESS_MESSAGE);
    }
}