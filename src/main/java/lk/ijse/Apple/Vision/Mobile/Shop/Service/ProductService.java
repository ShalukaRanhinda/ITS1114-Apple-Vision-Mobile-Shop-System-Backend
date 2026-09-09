package lk.ijse.Apple.Vision.Mobile.Shop.Service;

import lk.ijse.Apple.Vision.Mobile.Shop.DTO.ProductDTO;
import java.util.List;

public interface ProductService {
    ProductDTO saveProduct(ProductDTO productDTO);
    ProductDTO updateProduct(ProductDTO productDTO);
    String deleteProduct(Long productId);
    List<ProductDTO> getAllProducts();
    ProductDTO getProductById(Long productId);
}