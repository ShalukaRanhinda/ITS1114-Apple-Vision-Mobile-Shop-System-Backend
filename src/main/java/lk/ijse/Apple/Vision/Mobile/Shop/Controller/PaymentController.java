package lk.ijse.Apple.Vision.Mobile.Shop.Controller;

import lk.ijse.Apple.Vision.Mobile.Shop.Constant.CommonResponse;
import lk.ijse.Apple.Vision.Mobile.Shop.DTO.PaymentDTO;
import lk.ijse.Apple.Vision.Mobile.Shop.Service.PaymentService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static lk.ijse.Apple.Vision.Mobile.Shop.Constant.ResponseMessage.SUCCESS_MESSAGE;
import static lk.ijse.Apple.Vision.Mobile.Shop.Constant.ResponseStatusCode.OPERATION_SUCCESS;

@RestController
@RequestMapping("api/payments")
@CrossOrigin
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping(value = "/savePayment", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse savePayment(@RequestBody PaymentDTO paymentDTO) {
        PaymentDTO savedDTO = paymentService.savePayment(paymentDTO);
        return new CommonResponse(OPERATION_SUCCESS, savedDTO, SUCCESS_MESSAGE);
    }

    @PutMapping(value = "/updatePayment", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse updatePayment(@RequestBody PaymentDTO paymentDTO) {
        PaymentDTO updatedDTO = paymentService.updatePayment(paymentDTO);
        return new CommonResponse(OPERATION_SUCCESS, updatedDTO, SUCCESS_MESSAGE);
    }

    @DeleteMapping(value = "/deletePayment/{paymentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse deletePayment(@PathVariable Long paymentId) {
        String message = paymentService.deletePayment(paymentId);
        return new CommonResponse(OPERATION_SUCCESS, message, SUCCESS_MESSAGE);
    }

    @GetMapping(value = "/getAllPayments", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getAllPayments() {
        List<PaymentDTO> paymentList = paymentService.getAllPayments();
        return new CommonResponse(OPERATION_SUCCESS, paymentList, SUCCESS_MESSAGE);
    }

    @GetMapping(value = "/getPayment/{paymentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getPayment(@PathVariable Long paymentId) {
        PaymentDTO paymentDTO = paymentService.getPaymentById(paymentId);
        return new CommonResponse(OPERATION_SUCCESS, paymentDTO, SUCCESS_MESSAGE);
    }

    @GetMapping(value = "/getByOrder/{orderId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getPaymentsByOrder(@PathVariable Long orderId) {
        List<PaymentDTO> paymentList = paymentService.getPaymentsByOrderId(orderId);
        return new CommonResponse(OPERATION_SUCCESS, paymentList, SUCCESS_MESSAGE);
    }

    @GetMapping(value = "/getByRepair/{repairId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getPaymentsByRepair(@PathVariable Long repairId) {
        List<PaymentDTO> paymentList = paymentService.getPaymentsByRepairId(repairId);
        return new CommonResponse(OPERATION_SUCCESS, paymentList, SUCCESS_MESSAGE);
    }
}