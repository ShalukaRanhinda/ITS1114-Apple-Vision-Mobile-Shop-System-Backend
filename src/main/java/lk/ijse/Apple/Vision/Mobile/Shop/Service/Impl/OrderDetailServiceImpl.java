package lk.ijse.Apple.Vision.Mobile.Shop.Service.Impl;

import jakarta.transaction.Transactional;
import lk.ijse.Apple.Vision.Mobile.Shop.DTO.OrderDetailDTO;
import lk.ijse.Apple.Vision.Mobile.Shop.Entity.Order;
import lk.ijse.Apple.Vision.Mobile.Shop.Entity.OrderDetail;
import lk.ijse.Apple.Vision.Mobile.Shop.Entity.ProductVariant;
import lk.ijse.Apple.Vision.Mobile.Shop.Exception.CustomException;
import lk.ijse.Apple.Vision.Mobile.Shop.Repository.OrderDetailRepository;
import lk.ijse.Apple.Vision.Mobile.Shop.Repository.OrderRepository;
import lk.ijse.Apple.Vision.Mobile.Shop.Repository.ProductVariantRepository;
import lk.ijse.Apple.Vision.Mobile.Shop.Service.OrderDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@Slf4j
public class OrderDetailServiceImpl implements OrderDetailService {

    private final OrderDetailRepository orderDetailRepository;
    private final OrderRepository orderRepository;
    private final ProductVariantRepository productVariantRepository;

    public OrderDetailServiceImpl(OrderDetailRepository orderDetailRepository,
                                  OrderRepository orderRepository,
                                  ProductVariantRepository productVariantRepository) {
        this.orderDetailRepository = orderDetailRepository;
        this.orderRepository = orderRepository;
        this.productVariantRepository = productVariantRepository;
    }

    @Override
    public OrderDetailDTO saveOrderDetail(Long orderId, OrderDetailDTO orderDetailDTO) {
        log.info("Execute saveOrderDetail()");

        if (orderId == null) {
            throw new CustomException(400, "Order ID cannot be null!");
        }
        if (orderDetailDTO == null) {
            throw new CustomException(400, "OrderDetail data cannot be null!");
        }
        if (orderDetailDTO.getVariantId() == null) {
            throw new CustomException(400, "Product variant ID cannot be null!");
        }
        if (orderDetailDTO.getQuantity() <= 0) {
            throw new CustomException(400, "Quantity must be greater than zero!");
        }
        if (orderDetailDTO.getPrice() == null || orderDetailDTO.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new CustomException(400, "Price cannot be null or negative!");
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomException(404, "Order not found with ID: " + orderId));

        ProductVariant variant = productVariantRepository.findById(orderDetailDTO.getVariantId())
                .orElseThrow(() -> new CustomException(404, "Product variant not found with ID: " + orderDetailDTO.getVariantId()));

        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrder(order);
        orderDetail.setProductVariant(variant);
        orderDetail.setQuantity(orderDetailDTO.getQuantity());
        orderDetail.setPrice(orderDetailDTO.getPrice());

        OrderDetail savedDetail = orderDetailRepository.save(orderDetail);
        log.info("OrderDetail saved successfully with ID: {}", savedDetail.getOrderDetailId());

        orderDetailDTO.setOrderDetailId(savedDetail.getOrderDetailId());
        return orderDetailDTO;
    }

    @Override
    public OrderDetailDTO updateOrderDetail(OrderDetailDTO orderDetailDTO) {
        log.info("Execute updateOrderDetail()");

        if (orderDetailDTO == null) {
            throw new CustomException(400, "OrderDetail update data cannot be null!");
        }
        if (orderDetailDTO.getOrderDetailId() == null) {
            throw new CustomException(400, "OrderDetail ID cannot be null for update!");
        }
        if (orderDetailDTO.getQuantity() <= 0) {
            throw new CustomException(400, "Quantity must be greater than zero!");
        }
        if (orderDetailDTO.getPrice() == null || orderDetailDTO.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new CustomException(400, "Price cannot be null or negative!");
        }

        OrderDetail orderDetail = orderDetailRepository.findById(orderDetailDTO.getOrderDetailId())
                .orElseThrow(() -> new CustomException(404, "OrderDetail not found with ID: " + orderDetailDTO.getOrderDetailId()));

        if (orderDetailDTO.getVariantId() != null) {
            ProductVariant variant = productVariantRepository.findById(orderDetailDTO.getVariantId())
                    .orElseThrow(() -> new CustomException(404, "Product variant not found with ID: " + orderDetailDTO.getVariantId()));
            orderDetail.setProductVariant(variant);
        }

        orderDetail.setQuantity(orderDetailDTO.getQuantity());
        orderDetail.setPrice(orderDetailDTO.getPrice());

        OrderDetail updatedDetail = orderDetailRepository.save(orderDetail);
        log.info("OrderDetail updated successfully with ID: {}", updatedDetail.getOrderDetailId());

        OrderDetailDTO responseDTO = new OrderDetailDTO();
        responseDTO.setOrderDetailId(updatedDetail.getOrderDetailId());
        responseDTO.setQuantity(updatedDetail.getQuantity());
        responseDTO.setPrice(updatedDetail.getPrice());
        responseDTO.setVariantId(updatedDetail.getProductVariant() != null ? updatedDetail.getProductVariant().getVariantId() : null);

        return responseDTO;
    }

    @Override
    public String deleteOrderDetail(Long orderDetailId) {
        log.info("Execute deleteOrderDetail()");

        if (orderDetailId == null) {
            throw new CustomException(400, "OrderDetail ID cannot be null!");
        }

        OrderDetail orderDetail = orderDetailRepository.findById(orderDetailId)
                .orElseThrow(() -> new CustomException(404, "OrderDetail not found with ID: " + orderDetailId));

        orderDetailRepository.delete(orderDetail);
        log.info("OrderDetail deleted successfully for ID: {}", orderDetailId);

        return "OrderDetail deleted successfully!";
    }

    @Override
    public List<OrderDetailDTO> getAllOrderDetails() {
        log.info("Execute getAllOrderDetails()");

        List<OrderDetail> detailsList = orderDetailRepository.findAll();
        List<OrderDetailDTO> responseList = new ArrayList<>();

        for (OrderDetail detail : detailsList) {
            OrderDetailDTO dto = new OrderDetailDTO();
            dto.setOrderDetailId(detail.getOrderDetailId());
            dto.setQuantity(detail.getQuantity());
            dto.setPrice(detail.getPrice());
            dto.setVariantId(detail.getProductVariant() != null ? detail.getProductVariant().getVariantId() : null);

            responseList.add(dto);
        }
        return responseList;
    }

    @Override
    public OrderDetailDTO getOrderDetailById(Long orderDetailId) {
        log.info("Execute getOrderDetailById()");

        if (orderDetailId == null) {
            throw new CustomException(400, "OrderDetail ID cannot be null!");
        }

        OrderDetail detail = orderDetailRepository.findById(orderDetailId)
                .orElseThrow(() -> new CustomException(404, "OrderDetail not found with ID: " + orderDetailId));

        OrderDetailDTO dto = new OrderDetailDTO();
        dto.setOrderDetailId(detail.getOrderDetailId());
        dto.setQuantity(detail.getQuantity());
        dto.setPrice(detail.getPrice());
        dto.setVariantId(detail.getProductVariant() != null ? detail.getProductVariant().getVariantId() : null);

        return dto;
    }

    @Override
    public List<OrderDetailDTO> getOrderDetailsByOrderId(Long orderId) {
        log.info("Execute getOrderDetailsByOrderId()");

        if (orderId == null) {
            throw new CustomException(400, "Order ID cannot be null!");
        }

        if (!orderRepository.existsById(orderId)) {
            throw new CustomException(404, "Order not found with ID: " + orderId);
        }

        List<OrderDetail> detailsList = orderDetailRepository.findAllByOrder_OrderId(orderId);
        List<OrderDetailDTO> responseList = new ArrayList<>();

        for (OrderDetail detail : detailsList) {
            OrderDetailDTO dto = new OrderDetailDTO();
            dto.setOrderDetailId(detail.getOrderDetailId());
            dto.setQuantity(detail.getQuantity());
            dto.setPrice(detail.getPrice());
            dto.setVariantId(detail.getProductVariant() != null ? detail.getProductVariant().getVariantId() : null);

            responseList.add(dto);
        }
        return responseList;
    }
}