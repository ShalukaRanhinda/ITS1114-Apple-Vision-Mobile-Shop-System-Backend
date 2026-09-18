package lk.ijse.Apple.Vision.Mobile.Shop.Service.Impl;

import jakarta.transaction.Transactional;
import lk.ijse.Apple.Vision.Mobile.Shop.DTO.PurchaseOrderDetailsDTO;
import lk.ijse.Apple.Vision.Mobile.Shop.Entity.PurchaseOrder;
import lk.ijse.Apple.Vision.Mobile.Shop.Entity.PurchaseOrderDetails;
import lk.ijse.Apple.Vision.Mobile.Shop.Entity.ProductVariant;
import lk.ijse.Apple.Vision.Mobile.Shop.Repository.PurchaseOrderDetailsRepository;
import lk.ijse.Apple.Vision.Mobile.Shop.Repository.PurchaseOrderRepository;
import lk.ijse.Apple.Vision.Mobile.Shop.Repository.ProductVariantRepository;
import lk.ijse.Apple.Vision.Mobile.Shop.Service.PurchaseOrderDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(purchaseOrderId).orElse(null);
        ProductVariant variant = productVariantRepository.findById(dto.getVariantId()).orElse(null);

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

        PurchaseOrderDetails details = purchaseOrderDetailsRepository.findById(dto.getPoDetailsId()).orElse(new PurchaseOrderDetails());
        ProductVariant variant = productVariantRepository.findById(dto.getVariantId()).orElse(null);

        details.setProductVariant(variant);
        details.setQuantity(dto.getQuantity());
        details.setUnitPrice(dto.getUnitPrice());

        PurchaseOrderDetails updated = purchaseOrderDetailsRepository.save(details);
        log.info("PurchaseOrderDetail updated successfully!");

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

        purchaseOrderDetailsRepository.deleteById(poDetailsId);
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

        PurchaseOrderDetails detail = purchaseOrderDetailsRepository.findById(poDetailsId).orElse(new PurchaseOrderDetails());

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