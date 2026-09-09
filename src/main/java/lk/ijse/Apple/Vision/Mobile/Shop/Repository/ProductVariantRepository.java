package lk.ijse.Apple.Vision.Mobile.Shop.Repository;

import lk.ijse.Apple.Vision.Mobile.Shop.Entity.ProductVariant;
import lk.ijse.Apple.Vision.Mobile.Shop.Enumeration.ProductVariantStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {
    List<ProductVariant> findAllByProduct_ProductId(Long productId);
    List<ProductVariant> findAllByVariantStatus(ProductVariantStatus variantStatus);
}