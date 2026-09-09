package lk.ijse.Apple.Vision.Mobile.Shop.Service;

import lk.ijse.Apple.Vision.Mobile.Shop.DTO.ProductVariantDTO;
import java.util.List;

public interface ProductVariantService {
    ProductVariantDTO saveProductVariant(ProductVariantDTO productVariantDTO);
    ProductVariantDTO updateProductVariant(ProductVariantDTO productVariantDTO);
    String deleteProductVariant(Long variantId);
    List<ProductVariantDTO> getAllProductVariants();
    ProductVariantDTO getProductVariantById(Long variantId);
    List<ProductVariantDTO> getVariantsByProductId(Long productId);
}