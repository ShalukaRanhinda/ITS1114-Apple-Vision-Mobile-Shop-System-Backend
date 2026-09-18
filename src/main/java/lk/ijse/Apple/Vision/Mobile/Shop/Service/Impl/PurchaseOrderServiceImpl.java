package lk.ijse.Apple.Vision.Mobile.Shop.Service.Impl;

import jakarta.transaction.Transactional;
import lk.ijse.Apple.Vision.Mobile.Shop.DTO.PurchaseOrderDTO;
import lk.ijse.Apple.Vision.Mobile.Shop.DTO.PurchaseOrderDetailsDTO;
import lk.ijse.Apple.Vision.Mobile.Shop.Entity.*;
import lk.ijse.Apple.Vision.Mobile.Shop.Enumeration.InventoryStatus;
import lk.ijse.Apple.Vision.Mobile.Shop.Enumeration.PurchaseOrderStatus;
import lk.ijse.Apple.Vision.Mobile.Shop.Repository.*;
import lk.ijse.Apple.Vision.Mobile.Shop.Service.PurchaseOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

        Supplier supplier = null;
        if (purchaseOrderDTO.getSupplierId() != null) {
            supplier = supplierRepository.findById(purchaseOrderDTO.getSupplierId()).orElse(null);
        }

        PurchaseOrder purchaseOrder = new PurchaseOrder();
        purchaseOrder.setSupplier(supplier);
        purchaseOrder.setPurchaseOrderDate(LocalDateTime.now());
        purchaseOrder.setPurchaseOrderStatus(purchaseOrderDTO.getPurchaseOrderStatus() != null ?
                purchaseOrderDTO.getPurchaseOrderStatus() : PurchaseOrderStatus.COMPLETED);

        PurchaseOrder savedOrder = purchaseOrderRepository.save(purchaseOrder);
        log.info("PurchaseOrder saved with ID: {}", savedOrder.getPurchaseOrderId());

        List<PurchaseOrderDetailsDTO> savedDetailDTOs = new ArrayList<>();

        if (purchaseOrderDTO.getPurchaseOrderDetails() != null) {
            for (PurchaseOrderDetailsDTO detailDTO : purchaseOrderDTO.getPurchaseOrderDetails()) {
                ProductVariant variant = null;
                if (detailDTO.getVariantId() != null) {
                    variant = productVariantRepository.findById(detailDTO.getVariantId()).orElse(null);
                }

                PurchaseOrderDetails orderDetail = new PurchaseOrderDetails();
                orderDetail.setPurchaseOrder(savedOrder);
                orderDetail.setProductVariant(variant);
                orderDetail.setQuantity(detailDTO.getQuantity());
                orderDetail.setUnitPrice(detailDTO.getUnitPrice());

                PurchaseOrderDetails savedDetail = purchaseOrderDetailsRepository.save(orderDetail);

                // Stock එක Inventory එකට වැඩි කිරීම
                if (detailDTO.getVariantId() != null) {
                    Inventory inventory = inventoryRepository.findByProductVariant_VariantId(detailDTO.getVariantId()).orElse(null);
                    if (inventory != null) {
                        inventory.setQuantity(inventory.getQuantity() + detailDTO.getQuantity());
                        inventoryRepository.save(inventory);
                    } else if (variant != null) {
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
                        savedDetail.getProductVariant() != null ? savedDetail.getProductVariant().getVariantId() : null
                ));
            }
        }

        return new PurchaseOrderDTO(
                savedOrder.getPurchaseOrderId(),
                savedOrder.getPurchaseOrderDate(),
                savedOrder.getPurchaseOrderStatus(),
                savedOrder.getSupplier() != null ? savedOrder.getSupplier().getSupplierId() : null,
                savedDetailDTOs
        );
    }

    @Override
    public String cancelPurchaseOrder(Long purchaseOrderId) {
        log.info("Execute cancelPurchaseOrder()");

        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(purchaseOrderId).orElse(null);
        if (purchaseOrder != null) {
            purchaseOrder.setPurchaseOrderStatus(PurchaseOrderStatus.CANCELLED);

            // Cancel කළ විට එකතු කළ බඩු ප්‍රමාණය Inventory එකෙන් නැවත අඩු කිරීම
            List<PurchaseOrderDetails> details = purchaseOrderDetailsRepository.findAllByPurchaseOrder_PurchaseOrderId(purchaseOrderId);
            for (PurchaseOrderDetails detail : details) {
                if (detail.getProductVariant() != null) {
                    Inventory inventory = inventoryRepository.findByProductVariant_VariantId(detail.getProductVariant().getVariantId()).orElse(null);
                    if (inventory != null) {
                        inventory.setQuantity(inventory.getQuantity() - detail.getQuantity());
                        inventoryRepository.save(inventory);
                    }
                }
            }
            purchaseOrderRepository.save(purchaseOrder);
        }

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

        PurchaseOrder po = purchaseOrderRepository.findById(purchaseOrderId).orElse(new PurchaseOrder());
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