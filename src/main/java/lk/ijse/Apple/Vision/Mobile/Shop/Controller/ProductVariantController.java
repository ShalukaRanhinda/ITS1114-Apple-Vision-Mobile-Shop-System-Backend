package lk.ijse.Apple.Vision.Mobile.Shop.Controller;

import lk.ijse.Apple.Vision.Mobile.Shop.Constant.CommonResponse;
import lk.ijse.Apple.Vision.Mobile.Shop.DTO.ProductVariantDTO;
import lk.ijse.Apple.Vision.Mobile.Shop.Service.ProductVariantService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static lk.ijse.Apple.Vision.Mobile.Shop.Constant.ResponseMessage.SUCCESS_MESSAGE;
import static lk.ijse.Apple.Vision.Mobile.Shop.Constant.ResponseStatusCode.OPERATION_SUCCESS;

@RestController
@RequestMapping("api/product-variants")
public class ProductVariantController {

    private final ProductVariantService productVariantService;

    public ProductVariantController(ProductVariantService productVariantService) {
        this.productVariantService = productVariantService;
    }

    @PostMapping(value = "/saveProductVariant", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse saveProductVariant(@RequestBody ProductVariantDTO productVariantDTO) {
        ProductVariantDTO savedDTO = productVariantService.saveProductVariant(productVariantDTO);
        return new CommonResponse(OPERATION_SUCCESS, savedDTO, SUCCESS_MESSAGE);
    }

    @PutMapping(value = "/updateProductVariant", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse updateProductVariant(@RequestBody ProductVariantDTO productVariantDTO) {
        ProductVariantDTO updatedDTO = productVariantService.updateProductVariant(productVariantDTO);
        return new CommonResponse(OPERATION_SUCCESS, updatedDTO, SUCCESS_MESSAGE);
    }

    @DeleteMapping(value = "/deleteProductVariant/{variantId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse deleteProductVariant(@PathVariable Long variantId) {
        String message = productVariantService.deleteProductVariant(variantId);
        return new CommonResponse(OPERATION_SUCCESS, message, SUCCESS_MESSAGE);
    }

    @GetMapping(value = "/getAllProductVariants", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getAllProductVariants() {
        List<ProductVariantDTO> variantList = productVariantService.getAllProductVariants();
        return new CommonResponse(OPERATION_SUCCESS, variantList, SUCCESS_MESSAGE);
    }

    @GetMapping(value = "/getProductVariant/{variantId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getProductVariant(@PathVariable Long variantId) {
        ProductVariantDTO variantDTO = productVariantService.getProductVariantById(variantId);
        return new CommonResponse(OPERATION_SUCCESS, variantDTO, SUCCESS_MESSAGE);
    }

    @GetMapping(value = "/getByProduct/{productId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getVariantsByProduct(@PathVariable Long productId) {
        List<ProductVariantDTO> variantList = productVariantService.getVariantsByProductId(productId);
        return new CommonResponse(OPERATION_SUCCESS, variantList, SUCCESS_MESSAGE);
    }
}