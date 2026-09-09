package lk.ijse.Apple.Vision.Mobile.Shop.Repository;

import lk.ijse.Apple.Vision.Mobile.Shop.Entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
    List<OrderDetail> findAllByOrder_OrderId(Long orderId);
    List<OrderDetail> findAllByProductVariant_VariantId(Long variantId);
}