package lk.ijse.Apple.Vision.Mobile.Shop.Service.Impl;

import jakarta.transaction.Transactional;
import lk.ijse.Apple.Vision.Mobile.Shop.DTO.InventoryDTO;
import lk.ijse.Apple.Vision.Mobile.Shop.Entity.Inventory;
import lk.ijse.Apple.Vision.Mobile.Shop.Entity.ProductVariant;
import lk.ijse.Apple.Vision.Mobile.Shop.Enumeration.InventoryStatus;
import lk.ijse.Apple.Vision.Mobile.Shop.Exception.CustomException;
import lk.ijse.Apple.Vision.Mobile.Shop.Repository.InventoryRepository;
import lk.ijse.Apple.Vision.Mobile.Shop.Repository.ProductVariantRepository;
import lk.ijse.Apple.Vision.Mobile.Shop.Service.InventoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@Slf4j
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProductVariantRepository productVariantRepository;

    public InventoryServiceImpl(InventoryRepository inventoryRepository, ProductVariantRepository productVariantRepository) {
        this.inventoryRepository = inventoryRepository;
        this.productVariantRepository = productVariantRepository;
    }

    @Override
    public InventoryDTO saveInventory(InventoryDTO inventoryDTO) {
        log.info("Execute saveInventory()");

        if (inventoryDTO == null) {
            throw new CustomException(400, "Inventory data cannot be null!");
        }
        if (inventoryDTO.getVariantId() == null) {
            throw new CustomException(400, "Product variant ID cannot be null!");
        }
        if (inventoryDTO.getQuantity() < 0) {
            throw new CustomException(400, "Inventory quantity cannot be negative!");
        }

        ProductVariant variant = productVariantRepository.findById(inventoryDTO.getVariantId())
                .orElseThrow(() -> new CustomException(404, "Product variant not found with ID: " + inventoryDTO.getVariantId()));

        // Variant එකට දැනටමත් Inventory record එකක් පවතීදැයි පරීක්ෂා කිරීම
        if (inventoryRepository.findByProductVariant_VariantId(inventoryDTO.getVariantId()).isPresent()) {
            throw new CustomException(400, "Inventory already exists for product variant ID: " + inventoryDTO.getVariantId());
        }

        Inventory inventory = new Inventory();
        inventory.setProductVariant(variant);
        inventory.setQuantity(inventoryDTO.getQuantity());
        inventory.setInventoryStatus(InventoryStatus.ACTIVE);

        Inventory savedInventory = inventoryRepository.save(inventory);
        log.info("Inventory saved successfully with ID: {}", savedInventory.getInventoryId());

        inventoryDTO.setInventoryId(savedInventory.getInventoryId());
        inventoryDTO.setInventoryStatus(savedInventory.getInventoryStatus());
        return inventoryDTO;
    }

    @Override
    public InventoryDTO updateInventory(InventoryDTO inventoryDTO) {
        log.info("Execute updateInventory()");

        if (inventoryDTO == null) {
            throw new CustomException(400, "Inventory update data cannot be null!");
        }
        if (inventoryDTO.getInventoryId() == null) {
            throw new CustomException(400, "Inventory ID cannot be null for update!");
        }
        if (inventoryDTO.getQuantity() < 0) {
            throw new CustomException(400, "Inventory quantity cannot be negative!");
        }

        Inventory inventory = inventoryRepository.findById(inventoryDTO.getInventoryId())
                .orElseThrow(() -> new CustomException(404, "Inventory not found with ID: " + inventoryDTO.getInventoryId()));

        if (inventory.getInventoryStatus() == InventoryStatus.DELETED) {
            throw new CustomException(400, "Cannot update a deleted inventory record!");
        }

        if (inventoryDTO.getVariantId() != null) {
            ProductVariant variant = productVariantRepository.findById(inventoryDTO.getVariantId())
                    .orElseThrow(() -> new CustomException(404, "Product variant not found with ID: " + inventoryDTO.getVariantId()));
            inventory.setProductVariant(variant);
        }

        inventory.setQuantity(inventoryDTO.getQuantity());

        if (inventoryDTO.getInventoryStatus() != null) {
            inventory.setInventoryStatus(inventoryDTO.getInventoryStatus());
        }

        Inventory updatedInventory = inventoryRepository.save(inventory);
        log.info("Inventory updated successfully with ID: {}", updatedInventory.getInventoryId());

        InventoryDTO responseDTO = new InventoryDTO();
        responseDTO.setInventoryId(updatedInventory.getInventoryId());
        responseDTO.setVariantId(updatedInventory.getProductVariant() != null ? updatedInventory.getProductVariant().getVariantId() : null);
        responseDTO.setQuantity(updatedInventory.getQuantity());
        responseDTO.setInventoryStatus(updatedInventory.getInventoryStatus());

        return responseDTO;
    }

    @Override
    public String deleteInventory(Long inventoryId) {
        log.info("Execute deleteInventory()");

        if (inventoryId == null) {
            throw new CustomException(400, "Inventory ID cannot be null!");
        }

        Inventory inventory = inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new CustomException(404, "Inventory not found with ID: " + inventoryId));

        if (inventory.getInventoryStatus() == InventoryStatus.DELETED) {
            throw new CustomException(400, "Inventory is already deleted!");
        }

        inventory.setInventoryStatus(InventoryStatus.DELETED);
        inventoryRepository.save(inventory);
        log.info("Inventory marked as DELETED for ID: {}", inventoryId);

        return "Inventory deleted successfully!";
    }

    @Override
    public List<InventoryDTO> getAllInventories() {
        log.info("Execute getAllInventories()");

        List<Inventory> inventoryList = inventoryRepository.findAll();
        List<InventoryDTO> responseList = new ArrayList<>();

        for (Inventory inventory : inventoryList) {
            if (inventory.getInventoryStatus() != InventoryStatus.DELETED) {
                InventoryDTO dto = new InventoryDTO();
                dto.setInventoryId(inventory.getInventoryId());
                dto.setVariantId(inventory.getProductVariant() != null ? inventory.getProductVariant().getVariantId() : null);
                dto.setQuantity(inventory.getQuantity());
                dto.setInventoryStatus(inventory.getInventoryStatus());

                responseList.add(dto);
            }
        }
        return responseList;
    }

    @Override
    public InventoryDTO getInventoryById(Long inventoryId) {
        log.info("Execute getInventoryById()");

        if (inventoryId == null) {
            throw new CustomException(400, "Inventory ID cannot be null!");
        }

        Inventory inventory = inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new CustomException(404, "Inventory not found with ID: " + inventoryId));

        if (inventory.getInventoryStatus() == InventoryStatus.DELETED) {
            throw new CustomException(404, "Inventory not found or has been deleted!");
        }

        InventoryDTO dto = new InventoryDTO();
        dto.setInventoryId(inventory.getInventoryId());
        dto.setVariantId(inventory.getProductVariant() != null ? inventory.getProductVariant().getVariantId() : null);
        dto.setQuantity(inventory.getQuantity());
        dto.setInventoryStatus(inventory.getInventoryStatus());

        return dto;
    }

    @Override
    public InventoryDTO getInventoryByVariantId(Long variantId) {
        log.info("Execute getInventoryByVariantId()");

        if (variantId == null) {
            throw new CustomException(400, "Product variant ID cannot be null!");
        }

        Inventory inventory = inventoryRepository.findByProductVariant_VariantId(variantId)
                .orElseThrow(() -> new CustomException(404, "No active inventory found for variant ID: " + variantId));

        if (inventory.getInventoryStatus() == InventoryStatus.DELETED) {
            throw new CustomException(404, "Inventory not found or has been deleted!");
        }

        InventoryDTO dto = new InventoryDTO();
        dto.setInventoryId(inventory.getInventoryId());
        dto.setVariantId(inventory.getProductVariant() != null ? inventory.getProductVariant().getVariantId() : null);
        dto.setQuantity(inventory.getQuantity());
        dto.setInventoryStatus(inventory.getInventoryStatus());

        return dto;
    }
}