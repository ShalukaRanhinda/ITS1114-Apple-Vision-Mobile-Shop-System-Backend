package lk.ijse.Apple.Vision.Mobile.Shop.DTO;

import lk.ijse.Apple.Vision.Mobile.Shop.Enumeration.ProductStatus;
import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDTO {
    private Long productId;
    private String productName;
    private BigDecimal productPrice;
    private Long brandId;
    private Long categoryId;
    private ProductStatus productStatus;
}