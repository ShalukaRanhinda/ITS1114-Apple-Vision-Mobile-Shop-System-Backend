package lk.ijse.Apple.Vision.Mobile.Shop.Service.Impl;

import jakarta.transaction.Transactional;
import lk.ijse.Apple.Vision.Mobile.Shop.DTO.OrderDetailDTO;
import lk.ijse.Apple.Vision.Mobile.Shop.DTO.OrderDTO;
import lk.ijse.Apple.Vision.Mobile.Shop.Entity.Customer;
import lk.ijse.Apple.Vision.Mobile.Shop.Entity.Inventory;
import lk.ijse.Apple.Vision.Mobile.Shop.Entity.Order;
import lk.ijse.Apple.Vision.Mobile.Shop.Entity.OrderDetail;
import lk.ijse.Apple.Vision.Mobile.Shop.Entity.ProductVariant;
import lk.ijse.Apple.Vision.Mobile.Shop.Enumeration.CustomerStatus;
import lk.ijse.Apple.Vision.Mobile.Shop.Enumeration.InventoryStatus;
import lk.ijse.Apple.Vision.Mobile.Shop.Enumeration.OrderStatus;
import lk.ijse.Apple.Vision.Mobile.Shop.Exception.CustomException;
import lk.ijse.Apple.Vision.Mobile.Shop.Repository.CustomerRepository;
import lk.ijse.Apple.Vision.Mobile.Shop.Repository.InventoryRepository;
import lk.ijse.Apple.Vision.Mobile.Shop.Repository.OrderRepository;
import lk.ijse.Apple.Vision.Mobile.Shop.Repository.ProductVariantRepository;
import lk.ijse.Apple.Vision.Mobile.Shop.Service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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

        if (orderDTO == null) {
            throw new CustomException(400, "Order data cannot be null!");
        }
        if (orderDTO.getCustomerId() == null) {
            throw new CustomException(400, "Customer ID cannot be null!");
        }
        if (orderDTO.getOrderDetails() == null || orderDTO.getOrderDetails().isEmpty()) {
            throw new CustomException(400, "Order details cannot be empty!");
        }
        if (orderDTO.getTotalAmount() == null || orderDTO.getTotalAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new CustomException(400, "Total amount must be greater than zero!");
        }

        Customer customer = customerRepository.findById(orderDTO.getCustomerId())
                .orElseThrow(() -> new CustomException(404, "Customer not found with ID: " + orderDTO.getCustomerId()));

        if (customer.getCustomerStatus() == CustomerStatus.DELETED) {
            throw new CustomException(400, "Cannot place order for an inactive or deleted customer!");
        }

        // 1. Stock availability validation (ඕඩර් එක දැමීමට පෙර සියලු භාණ්ඩ වල තොග පරීක්ෂා කිරීම)
        for (OrderDetailDTO detailDTO : orderDTO.getOrderDetails()) {
            if (detailDTO.getVariantId() == null) {
                throw new CustomException(400, "Product variant ID cannot be null!");
            }
            if (detailDTO.getQuantity() <= 0) {
                throw new CustomException(400, "Order quantity must be greater than zero!");
            }

            Inventory inventory = inventoryRepository.findByProductVariant_VariantId(detailDTO.getVariantId())
                    .orElseThrow(() -> new CustomException(404, "Inventory not found for variant ID: " + detailDTO.getVariantId()));

            if (inventory.getInventoryStatus() == InventoryStatus.DELETED) {
                throw new CustomException(400, "Inventory item is inactive for variant ID: " + detailDTO.getVariantId());
            }

            if (inventory.getQuantity() < detailDTO.getQuantity()) {
                throw new CustomException(400, "Insufficient stock for variant ID: " + detailDTO.getVariantId()
                        + ". Available: " + inventory.getQuantity() + ", Requested: " + detailDTO.getQuantity());
            }
        }

        // 2. Order setup
        Order order = new Order();
        order.setCustomer(customer);
        order.setTotalAmount(orderDTO.getTotalAmount());
        order.setOrderDate(LocalDateTime.now());
        order.setOrderStatus(OrderStatus.COMPLETED);

        List<OrderDetail> detailList = new ArrayList<>();

        // 3. Stock අඩු කිරීම සහ OrderDetail සකස් කිරීම
        for (OrderDetailDTO detailDTO : orderDTO.getOrderDetails()) {
            ProductVariant variant = productVariantRepository.findById(detailDTO.getVariantId())
                    .orElseThrow(() -> new CustomException(404, "Product variant not found with ID: " + detailDTO.getVariantId()));

            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            orderDetail.setProductVariant(variant);
            orderDetail.setQuantity(detailDTO.getQuantity());
            orderDetail.setPrice(detailDTO.getPrice());
            detailList.add(orderDetail);

            Inventory inventory = inventoryRepository.findByProductVariant_VariantId(detailDTO.getVariantId()).get();
            inventory.setQuantity(inventory.getQuantity() - detailDTO.getQuantity());
            inventoryRepository.save(inventory);
        }

        order.setOrderDetail(detailList);
        Order savedOrder = orderRepository.save(order);
        log.info("Order placed successfully with ID: {}", savedOrder.getOrderId());

        List<OrderDetailDTO> responseDetailDTOs = new ArrayList<>();
        for (OrderDetail detail : savedOrder.getOrderDetail()) {
            responseDetailDTOs.add(new OrderDetailDTO(
                    detail.getOrderDetailId(),
                    detail.getQuantity(),
                    detail.getPrice(),
                    detail.getProductVariant() != null ? detail.getProductVariant().getVariantId() : null
            ));
        }

        return new OrderDTO(
                savedOrder.getOrderId(),
                savedOrder.getTotalAmount(),
                savedOrder.getOrderDate(),
                savedOrder.getOrderStatus(),
                savedOrder.getCustomer().getCustomerId(),
                responseDetailDTOs
        );
    }

    @Override
    public String cancelOrder(Long orderId) {
        log.info("Execute cancelOrder()");

        if (orderId == null) {
            throw new CustomException(400, "Order ID cannot be null!");
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomException(404, "Order not found with ID: " + orderId));

        if (order.getOrderStatus() == OrderStatus.CANCELLED) {
            throw new CustomException(400, "Order is already cancelled!");
        }

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
        log.info("Order cancelled successfully for ID: {}", orderId);

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

        if (orderId == null) {
            throw new CustomException(400, "Order ID cannot be null!");
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomException(404, "Order not found with ID: " + orderId));

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

        if (customerId == null) {
            throw new CustomException(400, "Customer ID cannot be null!");
        }

        if (!customerRepository.existsById(customerId)) {
            throw new CustomException(404, "Customer not found with ID: " + customerId);
        }

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