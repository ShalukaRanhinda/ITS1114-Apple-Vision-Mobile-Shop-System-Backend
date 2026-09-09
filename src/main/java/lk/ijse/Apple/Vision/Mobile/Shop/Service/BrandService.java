package lk.ijse.Apple.Vision.Mobile.Shop.Service;

import lk.ijse.Apple.Vision.Mobile.Shop.DTO.BrandDTO;
import java.util.List;

public interface BrandService {
    BrandDTO saveBrand(BrandDTO brandDTO);
    BrandDTO updateBrand(BrandDTO brandDTO);
    String deleteBrand(Long brandId);
    List<BrandDTO> getAllBrands();
    BrandDTO getBrandById(Long brandId);
}