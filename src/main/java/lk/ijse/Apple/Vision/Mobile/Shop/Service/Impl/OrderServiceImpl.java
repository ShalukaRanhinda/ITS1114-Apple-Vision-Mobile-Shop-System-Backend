package lk.ijse.Apple.Vision.Mobile.Shop.Service.Impl;

import jakarta.transaction.Transactional;
import lk.ijse.Apple.Vision.Mobile.Shop.DTO.OrderDetailDTO;
import lk.ijse.Apple.Vision.Mobile.Shop.DTO.OrderDTO;
import lk.ijse.Apple.Vision.Mobile.Shop.Entity.Customer;
import lk.ijse.Apple.Vision.Mobile.Shop.Entity.Inventory;
import lk.ijse.Apple.Vision.Mobile.Shop.Entity.Order;
import lk.ijse.Apple.Vision.Mobile.Shop.Entity.OrderDetail;
import lk.ijse.Apple.Vision.Mobile.Shop.Entity.ProductVariant;
import lk.ijse.Apple.Vision.Mobile.Shop.Enumeration.OrderStatus;
import lk.ijse.Apple.Vision.Mobile.Shop.Repository.CustomerRepository;
import lk.ijse.Apple.Vision.Mobile.Shop.Repository.InventoryRepository;
import lk.ijse.Apple.Vision.Mobile.Shop.Repository.OrderRepository;
import lk.ijse.Apple.Vision.Mobile.Shop.Repository.ProductVariantRepository;
import lk.ijse.Apple.Vision.Mobile.Shop.Service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductVariantRepository productVariantRepository;
    private final InventoryRepository inventoryRepository;

    public OrderServiceImpl(OrderRepository orderRepository,
                            CustomerRepository customerRepository,
                            ProductVariantRepository productVariantRepository,
                            InventoryRepository inventoryRepository) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.productVariantRepository = productVariantRepository;
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    public OrderDTO placeOrder(OrderDTO orderDTO) {
        log.info("Execute placeOrder()");

        Customer customer = customerRepository.findById(orderDTO.getCustomerId()).orElse(null);

        Order order = new Order();
        order.setCustomer(customer);
        order.setTotalAmount(orderDTO.getTotalAmount());
        order.setOrderDate(LocalDateTime.now());
        order.setOrderStatus(OrderStatus.COMPLETED);

        List<OrderDetail> detailList = new ArrayList<>();

        if (orderDTO.getOrderDetails() != null) {
            for (OrderDetailDTO detailDTO : orderDTO.getOrderDetails()) {
                ProductVariant variant = productVariantRepository.findById(detailDTO.getVariantId()).orElse(null);

                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setOrder(order);
                orderDetail.setProductVariant(variant);
                orderDetail.setQuantity(detailDTO.getQuantity());
                orderDetail.setPrice(detailDTO.getPrice());
                detailList.add(orderDetail);

                // Inventory එකෙන් quantity අඩු කිරීම
                if (detailDTO.getVariantId() != null) {
                    Inventory inventory = inventoryRepository.findByProductVariant_VariantId(detailDTO.getVariantId()).orElse(null);
                    if (inventory != null) {
                        inventory.setQuantity(inventory.getQuantity() - detailDTO.getQuantity());
                        inventoryRepository.save(inventory);
                    }
                }
            }
        }

        order.setOrderDetail(detailList);
        Order savedOrder = orderRepository.save(order);
        log.info("Order placed successfully with ID: {}", savedOrder.getOrderId());

        List<OrderDetailDTO> responseDetailDTOs = new ArrayList<>();
        if (savedOrder.getOrderDetail() != null) {
            for (OrderDetail detail : savedOrder.getOrderDetail()) {
                responseDetailDTOs.add(new OrderDetailDTO(
                        detail.getOrderDetailId(),
                        detail.getQuantity(),
                        detail.getPrice(),
                        detail.getProductVariant() != null ? detail.getProductVariant().getVariantId() : null
                ));
            }
        }

        return new OrderDTO(
                savedOrder.getOrderId(),
                savedOrder.getTotalAmount(),
                savedOrder.getOrderDate(),
                savedOrder.getOrderStatus(),
                savedOrder.getCustomer() != null ? savedOrder.getCustomer().getCustomerId() : null,
                responseDetailDTOs
        );
    }

    @Override
    public String cancelOrder(Long orderId) {
        log.info("Execute cancelOrder()");

        Order order = orderRepository.findById(orderId).orElse(null);
        if (order != null) {
            order.setOrderStatus(OrderStatus.CANCELLED);

            // Cancel කළ විට බඩු නැවත Inventory එකට එකතු කිරීම
            if (order.getOrderDetail() != null) {
                for (OrderDetail detail : order.getOrderDetail()) {
                    if (detail.getProductVariant() != null) {
                        Inventory inventory = inventoryRepository.findByProductVariant_VariantId(detail.getProductVariant().getVariantId()).orElse(null);
                        if (inventory != null) {
                            inventory.setQuantity(inventory.getQuantity() + detail.getQuantity());
                            inventoryRepository.save(inventory);
                        }
                    }
                }
            }

            orderRepository.save(order);
        }

        return "Order cancelled successfully!";
    }

    @Override
    public List<OrderDTO> getAllOrders() {
        log.info("Execute getAllOrders()");

        List<Order> orderList = orderRepository.findAll();
        List<OrderDTO> responseList = new ArrayList<>();

        for (Order order : orderList) {
            List<OrderDetailDTO> detailDTOs = new ArrayList<>();
            if (order.getOrderDetail() != null) {
                for (OrderDetail detail : order.getOrderDetail()) {
                    detailDTOs.add(new OrderDetailDTO(
                            detail.getOrderDetailId(),
                            detail.getQuantity(),
                            detail.getPrice(),
                            detail.getProductVariant() != null ? detail.getProductVariant().getVariantId() : null
                    ));
                }
            }

            responseList.add(new OrderDTO(
                    order.getOrderId(),
                    order.getTotalAmount(),
                    order.getOrderDate(),
                    order.getOrderStatus(),
                    order.getCustomer() != null ? order.getCustomer().getCustomerId() : null,
                    detailDTOs
            ));
        }

        return responseList;
    }

    @Override
    public OrderDTO getOrderById(Long orderId) {
        log.info("Execute getOrderById()");

        Order order = orderRepository.findById(orderId).orElse(new Order());
        List<OrderDetailDTO> detailDTOs = new ArrayList<>();

        if (order.getOrderDetail() != null) {
            for (OrderDetail detail : order.getOrderDetail()) {
                detailDTOs.add(new OrderDetailDTO(
                        detail.getOrderDetailId(),
                        detail.getQuantity(),
                        detail.getPrice(),
                        detail.getProductVariant() != null ? detail.getProductVariant().getVariantId() : null
                ));
            }
        }

        return new OrderDTO(
                order.getOrderId(),
                order.getTotalAmount(),
                order.getOrderDate(),
                order.getOrderStatus(),
                order.getCustomer() != null ? order.getCustomer().getCustomerId() : null,
                detailDTOs
        );
    }

    @Override
    public List<OrderDTO> getOrdersByCustomerId(Long customerId) {
        log.info("Execute getOrdersByCustomerId()");

        List<Order> orderList = orderRepository.findAllByCustomer_CustomerId(customerId);
        List<OrderDTO> responseList = new ArrayList<>();

        for (Order order : orderList) {
            List<OrderDetailDTO> detailDTOs = new ArrayList<>();
            if (order.getOrderDetail() != null) {
                for (OrderDetail detail : order.getOrderDetail()) {
                    detailDTOs.add(new OrderDetailDTO(
                            detail.getOrderDetailId(),
                            detail.getQuantity(),
                            detail.getPrice(),
                            detail.getProductVariant() != null ? detail.getProductVariant().getVariantId() : null
                    ));
                }
            }

            responseList.add(new OrderDTO(
                    order.getOrderId(),
                    order.getTotalAmount(),
                    order.getOrderDate(),
                    order.getOrderStatus(),
                    order.getCustomer() != null ? order.getCustomer().getCustomerId() : null,
                    detailDTOs
            ));
        }

        return responseList;
    }
}