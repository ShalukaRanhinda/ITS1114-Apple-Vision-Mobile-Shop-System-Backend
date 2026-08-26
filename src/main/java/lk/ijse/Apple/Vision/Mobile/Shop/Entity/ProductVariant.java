package lk.ijse.Apple.Vision.Mobile.Shop.Entity;

import jakarta.persistence.*;
import lk.ijse.Apple.Vision.Mobile.Shop.Enumeration.ProductVariantStatus;
import lombok.*;

import java.math.BigDecimal;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProductVariant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long variantId;
    private String variantDescription;
    private BigDecimal additionalPrice;
    @Enumerated(EnumType.STRING)
    private ProductVariantStatus variantStatus =  ProductVariantStatus.ACTIVE;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;
}
