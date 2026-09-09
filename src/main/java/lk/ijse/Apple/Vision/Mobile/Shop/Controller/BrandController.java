package lk.ijse.Apple.Vision.Mobile.Shop.Controller;

import lk.ijse.Apple.Vision.Mobile.Shop.Constant.CommonResponse;
import lk.ijse.Apple.Vision.Mobile.Shop.DTO.BrandDTO;
import lk.ijse.Apple.Vision.Mobile.Shop.Service.BrandService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static lk.ijse.Apple.Vision.Mobile.Shop.Constant.ResponseMessage.SUCCESS_MESSAGE;
import static lk.ijse.Apple.Vision.Mobile.Shop.Constant.ResponseStatusCode.OPERATION_SUCCESS;

@RestController
@RequestMapping("api/brands")
public class BrandController {

    private final BrandService brandService;

    public BrandController(BrandService brandService) {
        this.brandService = brandService;
    }

    @PostMapping(value = "/saveBrand", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse saveBrand(@RequestBody BrandDTO brandDTO) {
        BrandDTO savedBrandDTO = brandService.saveBrand(brandDTO);
        return new CommonResponse(OPERATION_SUCCESS, savedBrandDTO, SUCCESS_MESSAGE);
    }

    @PutMapping(value = "/updateBrand", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse updateBrand(@RequestBody BrandDTO brandDTO) {
        BrandDTO updatedBrandDTO = brandService.updateBrand(brandDTO);
        return new CommonResponse(OPERATION_SUCCESS, updatedBrandDTO, SUCCESS_MESSAGE);
    }

    @DeleteMapping(value = "/deleteBrand/{brandId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse deleteBrand(@PathVariable Long brandId) {
        String message = brandService.deleteBrand(brandId);
        return new CommonResponse(OPERATION_SUCCESS, message, SUCCESS_MESSAGE);
    }

    @GetMapping(value = "/getAllBrands", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getAllBrands() {
        List<BrandDTO> brandList = brandService.getAllBrands();
        return new CommonResponse(OPERATION_SUCCESS, brandList, SUCCESS_MESSAGE);
    }

    @GetMapping(value = "/getBrand/{brandId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getBrand(@PathVariable Long brandId) {
        BrandDTO brandDTO = brandService.getBrandById(brandId);
        return new CommonResponse(OPERATION_SUCCESS, brandDTO, SUCCESS_MESSAGE);
    }
}