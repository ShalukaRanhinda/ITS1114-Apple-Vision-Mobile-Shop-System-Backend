package lk.ijse.Apple.Vision.Mobile.Shop.Controller;

import lk.ijse.Apple.Vision.Mobile.Shop.Constant.CommonResponse;
import lk.ijse.Apple.Vision.Mobile.Shop.DTO.OrderDTO;
import lk.ijse.Apple.Vision.Mobile.Shop.Service.OrderService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static lk.ijse.Apple.Vision.Mobile.Shop.Constant.ResponseMessage.SUCCESS_MESSAGE;
import static lk.ijse.Apple.Vision.Mobile.Shop.Constant.ResponseStatusCode.OPERATION_SUCCESS;

@RestController
@RequestMapping("api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping(value = "/placeOrder", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse placeOrder(@RequestBody OrderDTO orderDTO) {
        OrderDTO savedOrderDTO = orderService.placeOrder(orderDTO);
        return new CommonResponse(OPERATION_SUCCESS, savedOrderDTO, SUCCESS_MESSAGE);
    }

    @PutMapping(value = "/cancelOrder/{orderId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse cancelOrder(@PathVariable Long orderId) {
        String message = orderService.cancelOrder(orderId);
        return new CommonResponse(OPERATION_SUCCESS, message, SUCCESS_MESSAGE);
    }

    @GetMapping(value = "/getAllOrders", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getAllOrders() {
        List<OrderDTO> orderList = orderService.getAllOrders();
        return new CommonResponse(OPERATION_SUCCESS, orderList, SUCCESS_MESSAGE);
    }

    @GetMapping(value = "/getOrder/{orderId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getOrder(@PathVariable Long orderId) {
        OrderDTO orderDTO = orderService.getOrderById(orderId);
        return new CommonResponse(OPERATION_SUCCESS, orderDTO, SUCCESS_MESSAGE);
    }

    @GetMapping(value = "/getByCustomer/{customerId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getOrdersByCustomer(@PathVariable Long customerId) {
        List<OrderDTO> orderList = orderService.getOrdersByCustomerId(customerId);
        return new CommonResponse(OPERATION_SUCCESS, orderList, SUCCESS_MESSAGE);
    }
}