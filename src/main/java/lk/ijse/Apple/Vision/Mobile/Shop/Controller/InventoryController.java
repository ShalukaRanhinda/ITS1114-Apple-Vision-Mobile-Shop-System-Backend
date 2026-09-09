package lk.ijse.Apple.Vision.Mobile.Shop.Controller;

import lk.ijse.Apple.Vision.Mobile.Shop.Constant.CommonResponse;
import lk.ijse.Apple.Vision.Mobile.Shop.DTO.InventoryDTO;
import lk.ijse.Apple.Vision.Mobile.Shop.Service.InventoryService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static lk.ijse.Apple.Vision.Mobile.Shop.Constant.ResponseMessage.SUCCESS_MESSAGE;
import static lk.ijse.Apple.Vision.Mobile.Shop.Constant.ResponseStatusCode.OPERATION_SUCCESS;

@RestController
@RequestMapping("api/inventories")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping(value = "/saveInventory", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse saveInventory(@RequestBody InventoryDTO inventoryDTO) {
        InventoryDTO savedDTO = inventoryService.saveInventory(inventoryDTO);
        return new CommonResponse(OPERATION_SUCCESS, savedDTO, SUCCESS_MESSAGE);
    }

    @PutMapping(value = "/updateInventory", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse updateInventory(@RequestBody InventoryDTO inventoryDTO) {
        InventoryDTO updatedDTO = inventoryService.updateInventory(inventoryDTO);
        return new CommonResponse(OPERATION_SUCCESS, updatedDTO, SUCCESS_MESSAGE);
    }

    @DeleteMapping(value = "/deleteInventory/{inventoryId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse deleteInventory(@PathVariable Long inventoryId) {
        String message = inventoryService.deleteInventory(inventoryId);
        return new CommonResponse(OPERATION_SUCCESS, message, SUCCESS_MESSAGE);
    }

    @GetMapping(value = "/getAllInventories", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getAllInventories() {
        List<InventoryDTO> inventoryList = inventoryService.getAllInventories();
        return new CommonResponse(OPERATION_SUCCESS, inventoryList, SUCCESS_MESSAGE);
    }

    @GetMapping(value = "/getInventory/{inventoryId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getInventory(@PathVariable Long inventoryId) {
        InventoryDTO inventoryDTO = inventoryService.getInventoryById(inventoryId);
        return new CommonResponse(OPERATION_SUCCESS, inventoryDTO, SUCCESS_MESSAGE);
    }

    @GetMapping(value = "/getByVariant/{variantId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getInventoryByVariant(@PathVariable Long variantId) {
        InventoryDTO inventoryDTO = inventoryService.getInventoryByVariantId(variantId);
        return new CommonResponse(OPERATION_SUCCESS, inventoryDTO, SUCCESS_MESSAGE);
    }
}