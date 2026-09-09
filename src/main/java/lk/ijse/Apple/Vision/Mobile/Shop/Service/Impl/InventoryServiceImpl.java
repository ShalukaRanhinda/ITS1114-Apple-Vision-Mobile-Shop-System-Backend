package lk.ijse.Apple.Vision.Mobile.Shop.Service.Impl;

import jakarta.transaction.Transactional;
import lk.ijse.Apple.Vision.Mobile.Shop.DTO.InventoryDTO;
import lk.ijse.Apple.Vision.Mobile.Shop.Entity.Inventory;
import lk.ijse.Apple.Vision.Mobile.Shop.Entity.ProductVariant;
import lk.ijse.Apple.Vision.Mobile.Shop.Enumeration.InventoryStatus;
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

        ProductVariant variant = productVariantRepository.findById(inventoryDTO.getVariantId()).orElse(null);

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

        Inventory inventory = inventoryRepository.findById(inventoryDTO.getInventoryId()).orElse(new Inventory());
        ProductVariant variant = productVariantRepository.findById(inventoryDTO.getVariantId()).orElse(null);

        inventory.setProductVariant(variant);
        inventory.setQuantity(inventoryDTO.getQuantity());

        if (inventoryDTO.getInventoryStatus() != null) {
            inventory.setInventoryStatus(inventoryDTO.getInventoryStatus());
        }

        Inventory updatedInventory = inventoryRepository.save(inventory);
        log.info("Inventory updated successfully!");

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

        Inventory inventory = inventoryRepository.findById(inventoryId).orElse(null);
        if (inventory != null) {
            inventory.setInventoryStatus(InventoryStatus.DELETED);
            inventoryRepository.save(inventory);
        }

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

        Inventory inventory = inventoryRepository.findById(inventoryId).orElse(new Inventory());

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

        Inventory inventory = inventoryRepository.findByProductVariant_VariantId(variantId).orElse(new Inventory());

        InventoryDTO dto = new InventoryDTO();
        dto.setInventoryId(inventory.getInventoryId());
        dto.setVariantId(inventory.getProductVariant() != null ? inventory.getProductVariant().getVariantId() : null);
        dto.setQuantity(inventory.getQuantity());
        dto.setInventoryStatus(inventory.getInventoryStatus());

        return dto;
    }
}