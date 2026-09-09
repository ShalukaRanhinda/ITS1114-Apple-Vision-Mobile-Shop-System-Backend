package lk.ijse.Apple.Vision.Mobile.Shop.DTO;

import lk.ijse.Apple.Vision.Mobile.Shop.Enumeration.ProductVariantStatus;
import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVariantDTO {
    private Long variantId;
    private String variantDescription;
    private BigDecimal additionalPrice;
    private ProductVariantStatus variantStatus;
    private Long productId;
}