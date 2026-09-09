package lk.ijse.Apple.Vision.Mobile.Shop.Controller;

import lk.ijse.Apple.Vision.Mobile.Shop.Constant.CommonResponse;
import lk.ijse.Apple.Vision.Mobile.Shop.DTO.ProductDTO;
import lk.ijse.Apple.Vision.Mobile.Shop.Service.ProductService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static lk.ijse.Apple.Vision.Mobile.Shop.Constant.ResponseMessage.SUCCESS_MESSAGE;
import static lk.ijse.Apple.Vision.Mobile.Shop.Constant.ResponseStatusCode.OPERATION_SUCCESS;

@RestController
@RequestMapping("api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping(value = "/saveProduct", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse saveProduct(@RequestBody ProductDTO productDTO) {
        ProductDTO savedProductDTO = productService.saveProduct(productDTO);
        return new CommonResponse(OPERATION_SUCCESS, savedProductDTO, SUCCESS_MESSAGE);
    }

    @PutMapping(value = "/updateProduct", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse updateProduct(@RequestBody ProductDTO productDTO) {
        ProductDTO updatedProductDTO = productService.updateProduct(productDTO);
        return new CommonResponse(OPERATION_SUCCESS, updatedProductDTO, SUCCESS_MESSAGE);
    }

    @DeleteMapping(value = "/deleteProduct/{productId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse deleteProduct(@PathVariable Long productId) {
        String message = productService.deleteProduct(productId);
        return new CommonResponse(OPERATION_SUCCESS, message, SUCCESS_MESSAGE);
    }

    @GetMapping(value = "/getAllProducts", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getAllProducts() {
        List<ProductDTO> productList = productService.getAllProducts();
        return new CommonResponse(OPERATION_SUCCESS, productList, SUCCESS_MESSAGE);
    }

    @GetMapping(value = "/getProduct/{productId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getProduct(@PathVariable Long productId) {
        ProductDTO productDTO = productService.getProductById(productId);
        return new CommonResponse(OPERATION_SUCCESS, productDTO, SUCCESS_MESSAGE);
    }
}