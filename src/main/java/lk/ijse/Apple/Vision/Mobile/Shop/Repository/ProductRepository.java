package lk.ijse.Apple.Vision.Mobile.Shop.Repository;

import lk.ijse.Apple.Vision.Mobile.Shop.Entity.Product;
import lk.ijse.Apple.Vision.Mobile.Shop.Enumeration.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsByProductName(String productName);
    List<Product> findAllByProductStatus(ProductStatus productStatus);
    List<Product> findAllByBrand_BrandId(Long brandId);
    List<Product> findAllByCategory_CategoryId(Long categoryId);
}