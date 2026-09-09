package lk.ijse.Apple.Vision.Mobile.Shop.Service.Impl;

import jakarta.transaction.Transactional;
import lk.ijse.Apple.Vision.Mobile.Shop.DTO.OrderDetailDTO;
import lk.ijse.Apple.Vision.Mobile.Shop.Entity.Order;
import lk.ijse.Apple.Vision.Mobile.Shop.Entity.OrderDetail;
import lk.ijse.Apple.Vision.Mobile.Shop.Entity.ProductVariant;
import lk.ijse.Apple.Vision.Mobile.Shop.Repository.OrderDetailRepository;
import lk.ijse.Apple.Vision.Mobile.Shop.Repository.OrderRepository;
import lk.ijse.Apple.Vision.Mobile.Shop.Repository.ProductVariantRepository;
import lk.ijse.Apple.Vision.Mobile.Shop.Service.OrderDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

        Order order = orderRepository.findById(orderId).orElse(null);
        ProductVariant variant = productVariantRepository.findById(orderDetailDTO.getVariantId()).orElse(null);

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

        OrderDetail orderDetail = orderDetailRepository.findById(orderDetailDTO.getOrderDetailId()).orElse(new OrderDetail());
        ProductVariant variant = productVariantRepository.findById(orderDetailDTO.getVariantId()).orElse(null);

        orderDetail.setProductVariant(variant);
        orderDetail.setQuantity(orderDetailDTO.getQuantity());
        orderDetail.setPrice(orderDetailDTO.getPrice());

        OrderDetail updatedDetail = orderDetailRepository.save(orderDetail);
        log.info("OrderDetail updated successfully!");

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

        orderDetailRepository.deleteById(orderDetailId);
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

        OrderDetail detail = orderDetailRepository.findById(orderDetailId).orElse(new OrderDetail());

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