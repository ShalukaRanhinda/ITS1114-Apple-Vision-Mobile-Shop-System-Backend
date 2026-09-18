package lk.ijse.Apple.Vision.Mobile.Shop.Service.Impl;

import jakarta.transaction.Transactional;
import lk.ijse.Apple.Vision.Mobile.Shop.DTO.PurchaseOrderDetailsDTO;
import lk.ijse.Apple.Vision.Mobile.Shop.Entity.ProductVariant;
import lk.ijse.Apple.Vision.Mobile.Shop.Entity.PurchaseOrder;
import lk.ijse.Apple.Vision.Mobile.Shop.Entity.PurchaseOrderDetails;
import lk.ijse.Apple.Vision.Mobile.Shop.Enumeration.ProductVariantStatus;
import lk.ijse.Apple.Vision.Mobile.Shop.Enumeration.PurchaseOrderStatus;
import lk.ijse.Apple.Vision.Mobile.Shop.Exception.CustomException;
import lk.ijse.Apple.Vision.Mobile.Shop.Repository.ProductVariantRepository;
import lk.ijse.Apple.Vision.Mobile.Shop.Repository.PurchaseOrderDetailsRepository;
import lk.ijse.Apple.Vision.Mobile.Shop.Repository.PurchaseOrderRepository;
import lk.ijse.Apple.Vision.Mobile.Shop.Service.PurchaseOrderDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@Slf4j
public class PurchaseOrderDetailsServiceImpl implements PurchaseOrderDetailsService {

    private final PurchaseOrderDetailsRepository purchaseOrderDetailsRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final ProductVariantRepository productVariantRepository;

    public PurchaseOrderDetailsServiceImpl(PurchaseOrderDetailsRepository purchaseOrderDetailsRepository,
                                           PurchaseOrderRepository purchaseOrderRepository,
                                           ProductVariantRepository productVariantRepository) {
        this.purchaseOrderDetailsRepository = purchaseOrderDetailsRepository;
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.productVariantRepository = productVariantRepository;
    }

    @Override
    public PurchaseOrderDetailsDTO savePurchaseOrderDetail(Long purchaseOrderId, PurchaseOrderDetailsDTO dto) {
        log.info("Execute savePurchaseOrderDetail()");

        if (purchaseOrderId == null) {
            throw new CustomException(400, "Purchase order ID cannot be null!");
        }
        if (dto == null) {
            throw new CustomException(400, "Purchase order details data cannot be null!");
        }
        if (dto.getVariantId() == null) {
            throw new CustomException(400, "Product variant ID cannot be null!");
        }
        if (dto.getQuantity() <= 0) {
            throw new CustomException(400, "Quantity must be greater than zero!");
        }
        if (dto.getUnitPrice() == null || dto.getUnitPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new CustomException(400, "Unit price must be greater than zero!");
        }

        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(purchaseOrderId)
                .orElseThrow(() -> new CustomException(404, "Purchase order not found with ID: " + purchaseOrderId));

        if (purchaseOrder.getPurchaseOrderStatus() == PurchaseOrderStatus.CANCELLED) {
            throw new CustomException(400, "Cannot add details to a cancelled purchase order!");
        }

        ProductVariant variant = productVariantRepository.findById(dto.getVariantId())
                .orElseThrow(() -> new CustomException(404, "Product variant not found with ID: " + dto.getVariantId()));

        if (variant.getVariantStatus() == ProductVariantStatus.DELETED) {
            throw new CustomException(400, "Cannot add a deleted product variant to purchase order!");
        }

        PurchaseOrderDetails details = new PurchaseOrderDetails();
        details.setPurchaseOrder(purchaseOrder);
        details.setProductVariant(variant);
        details.setQuantity(dto.getQuantity());
        details.setUnitPrice(dto.getUnitPrice());

        PurchaseOrderDetails saved = purchaseOrderDetailsRepository.save(details);
        log.info("PurchaseOrderDetail saved with ID: {}", saved.getPoDetailsId());

        dto.setPoDetailsId(saved.getPoDetailsId());
        return dto;
    }

    @Override
    public PurchaseOrderDetailsDTO updatePurchaseOrderDetail(PurchaseOrderDetailsDTO dto) {
        log.info("Execute updatePurchaseOrderDetail()");

        if (dto == null) {
            throw new CustomException(400, "Purchase order details update data cannot be null!");
        }
        if (dto.getPoDetailsId() == null) {
            throw new CustomException(400, "Purchase order detail ID cannot be null for update!");
        }
        if (dto.getQuantity() <= 0) {
            throw new CustomException(400, "Quantity must be greater than zero!");
        }
        if (dto.getUnitPrice() == null || dto.getUnitPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new CustomException(400, "Unit price must be greater than zero!");
        }

        PurchaseOrderDetails details = purchaseOrderDetailsRepository.findById(dto.getPoDetailsId())
                .orElseThrow(() -> new CustomException(404, "Purchase order detail not found with ID: " + dto.getPoDetailsId()));

        if (details.getPurchaseOrder() != null && details.getPurchaseOrder().getPurchaseOrderStatus() == PurchaseOrderStatus.CANCELLED) {
            throw new CustomException(400, "Cannot update details of a cancelled purchase order!");
        }

        if (dto.getVariantId() != null) {
            ProductVariant variant = productVariantRepository.findById(dto.getVariantId())
                    .orElseThrow(() -> new CustomException(404, "Product variant not found with ID: " + dto.getVariantId()));

            if (variant.getVariantStatus() == ProductVariantStatus.DELETED) {
                throw new CustomException(400, "Cannot link detail to a deleted product variant!");
            }
            details.setProductVariant(variant);
        }

        details.setQuantity(dto.getQuantity());
        details.setUnitPrice(dto.getUnitPrice());

        PurchaseOrderDetails updated = purchaseOrderDetailsRepository.save(details);
        log.info("PurchaseOrderDetail updated successfully with ID: {}", updated.getPoDetailsId());

        PurchaseOrderDetailsDTO responseDTO = new PurchaseOrderDetailsDTO();
        responseDTO.setPoDetailsId(updated.getPoDetailsId());
        responseDTO.setQuantity(updated.getQuantity());
        responseDTO.setUnitPrice(updated.getUnitPrice());
        responseDTO.setVariantId(updated.getProductVariant() != null ? updated.getProductVariant().getVariantId() : null);

        return responseDTO;
    }

    @Override
    public String deletePurchaseOrderDetail(Long poDetailsId) {
        log.info("Execute deletePurchaseOrderDetail()");

        if (poDetailsId == null) {
            throw new CustomException(400, "Purchase order detail ID cannot be null!");
        }

        PurchaseOrderDetails details = purchaseOrderDetailsRepository.findById(poDetailsId)
                .orElseThrow(() -> new CustomException(404, "Purchase order detail not found with ID: " + poDetailsId));

        purchaseOrderDetailsRepository.delete(details);
        log.info("PurchaseOrderDetail deleted successfully with ID: {}", poDetailsId);

        return "PurchaseOrderDetail deleted successfully!";
    }

    @Override
    public List<PurchaseOrderDetailsDTO> getAllPurchaseOrderDetails() {
        log.info("Execute getAllPurchaseOrderDetails()");

        List<PurchaseOrderDetails> list = purchaseOrderDetailsRepository.findAll();
        List<PurchaseOrderDetailsDTO> responseList = new ArrayList<>();

        for (PurchaseOrderDetails detail : list) {
            PurchaseOrderDetailsDTO dto = new PurchaseOrderDetailsDTO();
            dto.setPoDetailsId(detail.getPoDetailsId());
            dto.setQuantity(detail.getQuantity());
            dto.setUnitPrice(detail.getUnitPrice());
            dto.setVariantId(detail.getProductVariant() != null ? detail.getProductVariant().getVariantId() : null);

            responseList.add(dto);
        }
        return responseList;
    }

    @Override
    public PurchaseOrderDetailsDTO getPurchaseOrderDetailById(Long poDetailsId) {
        log.info("Execute getPurchaseOrderDetailById()");

        if (poDetailsId == null) {
            throw new CustomException(400, "Purchase order detail ID cannot be null!");
        }

        PurchaseOrderDetails detail = purchaseOrderDetailsRepository.findById(poDetailsId)
                .orElseThrow(() -> new CustomException(404, "Purchase order detail not found with ID: " + poDetailsId));

        PurchaseOrderDetailsDTO dto = new PurchaseOrderDetailsDTO();
        dto.setPoDetailsId(detail.getPoDetailsId());
        dto.setQuantity(detail.getQuantity());
        dto.setUnitPrice(detail.getUnitPrice());
        dto.setVariantId(detail.getProductVariant() != null ? detail.getProductVariant().getVariantId() : null);

        return dto;
    }

    @Override
    public List<PurchaseOrderDetailsDTO> getDetailsByPurchaseOrderId(Long purchaseOrderId) {
        log.info("Execute getDetailsByPurchaseOrderId()");

        if (purchaseOrderId == null) {
            throw new CustomException(400, "Purchase order ID cannot be null!");
        }

        if (!purchaseOrderRepository.existsById(purchaseOrderId)) {
            throw new CustomException(404, "Purchase order not found with ID: " + purchaseOrderId);
        }

        List<PurchaseOrderDetails> list = purchaseOrderDetailsRepository.findAllByPurchaseOrder_PurchaseOrderId(purchaseOrderId);
        List<PurchaseOrderDetailsDTO> responseList = new ArrayList<>();

        for (PurchaseOrderDetails detail : list) {
            PurchaseOrderDetailsDTO dto = new PurchaseOrderDetailsDTO();
            dto.setPoDetailsId(detail.getPoDetailsId());
            dto.setQuantity(detail.getQuantity());
            dto.setUnitPrice(detail.getUnitPrice());
            dto.setVariantId(detail.getProductVariant() != null ? detail.getProductVariant().getVariantId() : null);

            responseList.add(dto);
        }
        return responseList;
    }
}