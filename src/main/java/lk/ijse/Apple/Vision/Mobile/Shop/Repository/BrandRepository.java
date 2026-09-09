package lk.ijse.Apple.Vision.Mobile.Shop.Repository;

import lk.ijse.Apple.Vision.Mobile.Shop.Entity.Brand;
import lk.ijse.Apple.Vision.Mobile.Shop.Enumeration.BrandStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {
    boolean existsByBrandName(String brandName);
    List<Brand> findAllByBrandStatus(BrandStatus brandStatus);
}