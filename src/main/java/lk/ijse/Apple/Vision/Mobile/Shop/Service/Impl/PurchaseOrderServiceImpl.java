package lk.ijse.Apple.Vision.Mobile.Shop.Service.Impl;

import jakarta.transaction.Transactional;
import lk.ijse.Apple.Vision.Mobile.Shop.DTO.PurchaseOrderDTO;
import lk.ijse.Apple.Vision.Mobile.Shop.DTO.PurchaseOrderDetailsDTO;
import lk.ijse.Apple.Vision.Mobile.Shop.Entity.*;
import lk.ijse.Apple.Vision.Mobile.Shop.Enumeration.InventoryStatus;
import lk.ijse.Apple.Vision.Mobile.Shop.Enumeration.ProductVariantStatus;
import lk.ijse.Apple.Vision.Mobile.Shop.Enumeration.PurchaseOrderStatus;
import lk.ijse.Apple.Vision.Mobile.Shop.Exception.CustomException;
import lk.ijse.Apple.Vision.Mobile.Shop.Repository.*;
import lk.ijse.Apple.Vision.Mobile.Shop.Service.PurchaseOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@Slf4j
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final PurchaseOrderDetailsRepository purchaseOrderDetailsRepository;
    private final SupplierRepository supplierRepository;
    private final ProductVariantRepository productVariantRepository;
    private final InventoryRepository inventoryRepository;

    public PurchaseOrderServiceImpl(PurchaseOrderRepository purchaseOrderRepository,
                                    PurchaseOrderDetailsRepository purchaseOrderDetailsRepository,
                                    SupplierRepository supplierRepository,
                                    ProductVariantRepository productVariantRepository,
                                    InventoryRepository inventoryRepository) {
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.purchaseOrderDetailsRepository = purchaseOrderDetailsRepository;
        this.supplierRepository = supplierRepository;
        this.productVariantRepository = productVariantRepository;
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    public PurchaseOrderDTO placePurchaseOrder(PurchaseOrderDTO purchaseOrderDTO) {
        log.info("Execute placePurchaseOrder()");

        if (purchaseOrderDTO == null) {
            throw new CustomException(400, "Purchase order data cannot be null!");
        }
        if (purchaseOrderDTO.getSupplierId() == null) {
            throw new CustomException(400, "Supplier ID cannot be null!");
        }
        if (purchaseOrderDTO.getPurchaseOrderDetails() == null || purchaseOrderDTO.getPurchaseOrderDetails().isEmpty()) {
            throw new CustomException(400, "Purchase order must contain at least one item detail!");
        }

        Supplier supplier = supplierRepository.findById(purchaseOrderDTO.getSupplierId())
                .orElseThrow(() -> new CustomException(404, "Supplier not found with ID: " + purchaseOrderDTO.getSupplierId()));

        // Item details වලංගුභාවය මුලින්ම තහවුරු කිරීම
        for (PurchaseOrderDetailsDTO detailDTO : purchaseOrderDTO.getPurchaseOrderDetails()) {
            if (detailDTO.getVariantId() == null) {
                throw new CustomException(400, "Product variant ID cannot be null!");
            }
            if (detailDTO.getQuantity() <= 0) {
                throw new CustomException(400, "Quantity must be greater than zero for variant ID: " + detailDTO.getVariantId());
            }
            if (detailDTO.getUnitPrice() == null || detailDTO.getUnitPrice().compareTo(BigDecimal.ZERO) <= 0) {
                throw new CustomException(400, "Unit price must be greater than zero for variant ID: " + detailDTO.getVariantId());
            }

            ProductVariant variant = productVariantRepository.findById(detailDTO.getVariantId())
                    .orElseThrow(() -> new CustomException(404, "Product variant not found with ID: " + detailDTO.getVariantId()));

            if (variant.getVariantStatus() == ProductVariantStatus.DELETED) {
                throw new CustomException(400, "Cannot create purchase order for a deleted product variant ID: " + detailDTO.getVariantId());
            }
        }

        PurchaseOrder purchaseOrder = new PurchaseOrder();
        purchaseOrder.setSupplier(supplier);
        purchaseOrder.setPurchaseOrderDate(LocalDateTime.now());
        purchaseOrder.setPurchaseOrderStatus(purchaseOrderDTO.getPurchaseOrderStatus() != null ?
                purchaseOrderDTO.getPurchaseOrderStatus() : PurchaseOrderStatus.COMPLETED);

        PurchaseOrder savedOrder = purchaseOrderRepository.save(purchaseOrder);
        log.info("PurchaseOrder saved with ID: {}", savedOrder.getPurchaseOrderId());

        List<PurchaseOrderDetailsDTO> savedDetailDTOs = new ArrayList<>();

        for (PurchaseOrderDetailsDTO detailDTO : purchaseOrderDTO.getPurchaseOrderDetails()) {
            ProductVariant variant = productVariantRepository.findById(detailDTO.getVariantId()).get();

            PurchaseOrderDetails orderDetail = new PurchaseOrderDetails();
            orderDetail.setPurchaseOrder(savedOrder);
            orderDetail.setProductVariant(variant);
            orderDetail.setQuantity(detailDTO.getQuantity());
            orderDetail.setUnitPrice(detailDTO.getUnitPrice());

            PurchaseOrderDetails savedDetail = purchaseOrderDetailsRepository.save(orderDetail);

            // Purchase order එක COMPLETED නම් පමණක් Inventory එකට stock එකතු කිරීම
            if (savedOrder.getPurchaseOrderStatus() == PurchaseOrderStatus.COMPLETED) {
                Inventory inventory = inventoryRepository.findByProductVariant_VariantId(detailDTO.getVariantId()).orElse(null);
                if (inventory != null) {
                    if (inventory.getInventoryStatus() == InventoryStatus.DELETED) {
                        inventory.setInventoryStatus(InventoryStatus.ACTIVE);
                    }
                    inventory.setQuantity(inventory.getQuantity() + detailDTO.getQuantity());
                    inventoryRepository.save(inventory);
                } else {
                    Inventory newInventory = new Inventory();
                    newInventory.setProductVariant(variant);
                    newInventory.setQuantity(detailDTO.getQuantity());
                    newInventory.setInventoryStatus(InventoryStatus.ACTIVE);
                    inventoryRepository.save(newInventory);
                }
            }

            savedDetailDTOs.add(new PurchaseOrderDetailsDTO(
                    savedDetail.getPoDetailsId(),
                    savedDetail.getQuantity(),
                    savedDetail.getUnitPrice(),
                    savedDetail.getProductVariant().getVariantId()
            ));
        }

        return new PurchaseOrderDTO(
                savedOrder.getPurchaseOrderId(),
                savedOrder.getPurchaseOrderDate(),
                savedOrder.getPurchaseOrderStatus(),
                savedOrder.getSupplier().getSupplierId(),
                savedDetailDTOs
        );
    }

    @Override
    public String cancelPurchaseOrder(Long purchaseOrderId) {
        log.info("Execute cancelPurchaseOrder()");

        if (purchaseOrderId == null) {
            throw new CustomException(400, "Purchase order ID cannot be null!");
        }

        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(purchaseOrderId)
                .orElseThrow(() -> new CustomException(404, "Purchase order not found with ID: " + purchaseOrderId));

        if (purchaseOrder.getPurchaseOrderStatus() == PurchaseOrderStatus.CANCELLED) {
            throw new CustomException(400, "Purchase order is already cancelled!");
        }

        // කලින් COMPLETED තත්ත්වයේ තිබුනා නම් Inventory එකෙන් අදාළ stock ප්‍රමාණය අඩු කිරීම
        if (purchaseOrder.getPurchaseOrderStatus() == PurchaseOrderStatus.COMPLETED) {
            List<PurchaseOrderDetails> details = purchaseOrderDetailsRepository.findAllByPurchaseOrder_PurchaseOrderId(purchaseOrderId);
            for (PurchaseOrderDetails detail : details) {
                if (detail.getProductVariant() != null) {
                    Inventory inventory = inventoryRepository.findByProductVariant_VariantId(detail.getProductVariant().getVariantId())
                            .orElseThrow(() -> new CustomException(404, "Inventory record missing for variant ID: " + detail.getProductVariant().getVariantId()));

                    if (inventory.getQuantity() < detail.getQuantity()) {
                        throw new CustomException(400, "Cannot cancel purchase order! Some stock items have already been sold or issued. Current stock: "
                                + inventory.getQuantity() + ", Required to deduct: " + detail.getQuantity());
                    }

                    inventory.setQuantity(inventory.getQuantity() - detail.getQuantity());
                    inventoryRepository.save(inventory);
                }
            }
        }

        purchaseOrder.setPurchaseOrderStatus(PurchaseOrderStatus.CANCELLED);
        purchaseOrderRepository.save(purchaseOrder);
        log.info("PurchaseOrder cancelled successfully for ID: {}", purchaseOrderId);

        return "PurchaseOrder cancelled successfully!";
    }

    @Override
    public List<PurchaseOrderDTO> getAllPurchaseOrders() {
        log.info("Execute getAllPurchaseOrders()");

        List<PurchaseOrder> poList = purchaseOrderRepository.findAll();
        List<PurchaseOrderDTO> responseList = new ArrayList<>();

        for (PurchaseOrder po : poList) {
            List<PurchaseOrderDetails> details = purchaseOrderDetailsRepository.findAllByPurchaseOrder_PurchaseOrderId(po.getPurchaseOrderId());
            List<PurchaseOrderDetailsDTO> detailDTOs = new ArrayList<>();

            for (PurchaseOrderDetails detail : details) {
                detailDTOs.add(new PurchaseOrderDetailsDTO(
                        detail.getPoDetailsId(),
                        detail.getQuantity(),
                        detail.getUnitPrice(),
                        detail.getProductVariant() != null ? detail.getProductVariant().getVariantId() : null
                ));
            }

            responseList.add(new PurchaseOrderDTO(
                    po.getPurchaseOrderId(),
                    po.getPurchaseOrderDate(),
                    po.getPurchaseOrderStatus(),
                    po.getSupplier() != null ? po.getSupplier().getSupplierId() : null,
                    detailDTOs
            ));
        }

        return responseList;
    }

    @Override
    public PurchaseOrderDTO getPurchaseOrderById(Long purchaseOrderId) {
        log.info("Execute getPurchaseOrderById()");

        if (purchaseOrderId == null) {
            throw new CustomException(400, "Purchase order ID cannot be null!");
        }

        PurchaseOrder po = purchaseOrderRepository.findById(purchaseOrderId)
                .orElseThrow(() -> new CustomException(404, "Purchase order not found with ID: " + purchaseOrderId));

        List<PurchaseOrderDetails> details = purchaseOrderDetailsRepository.findAllByPurchaseOrder_PurchaseOrderId(purchaseOrderId);
        List<PurchaseOrderDetailsDTO> detailDTOs = new ArrayList<>();

        for (PurchaseOrderDetails detail : details) {
            detailDTOs.add(new PurchaseOrderDetailsDTO(
                    detail.getPoDetailsId(),
                    detail.getQuantity(),
                    detail.getUnitPrice(),
                    detail.getProductVariant() != null ? detail.getProductVariant().getVariantId() : null
            ));
        }

        return new PurchaseOrderDTO(
                po.getPurchaseOrderId(),
                po.getPurchaseOrderDate(),
                po.getPurchaseOrderStatus(),
                po.getSupplier() != null ? po.getSupplier().getSupplierId() : null,
                detailDTOs
        );
    }

    @Override
    public List<PurchaseOrderDTO> getPurchaseOrdersBySupplierId(Long supplierId) {
        log.info("Execute getPurchaseOrdersBySupplierId()");

        if (supplierId == null) {
            throw new CustomException(400, "Supplier ID cannot be null!");
        }

        if (!supplierRepository.existsById(supplierId)) {
            throw new CustomException(404, "Supplier not found with ID: " + supplierId);
        }

        List<PurchaseOrder> poList = purchaseOrderRepository.findAllBySupplier_SupplierId(supplierId);
        List<PurchaseOrderDTO> responseList = new ArrayList<>();

        for (PurchaseOrder po : poList) {
            List<PurchaseOrderDetails> details = purchaseOrderDetailsRepository.findAllByPurchaseOrder_PurchaseOrderId(po.getPurchaseOrderId());
            List<PurchaseOrderDetailsDTO> detailDTOs = new ArrayList<>();

            for (PurchaseOrderDetails detail : details) {
                detailDTOs.add(new PurchaseOrderDetailsDTO(
                        detail.getPoDetailsId(),
                        detail.getQuantity(),
                        detail.getUnitPrice(),
                        detail.getProductVariant() != null ? detail.getProductVariant().getVariantId() : null
                ));
            }

            responseList.add(new PurchaseOrderDTO(
                    po.getPurchaseOrderId(),
                    po.getPurchaseOrderDate(),
                    po.getPurchaseOrderStatus(),
                    po.getSupplier() != null ? po.getSupplier().getSupplierId() : null,
                    detailDTOs
            ));
        }

        return responseList;
    }
}