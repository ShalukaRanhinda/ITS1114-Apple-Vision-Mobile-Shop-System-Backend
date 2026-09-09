package lk.ijse.Apple.Vision.Mobile.Shop.Service.Impl;

import jakarta.transaction.Transactional;
import lk.ijse.Apple.Vision.Mobile.Shop.DTO.ProductVariantDTO;
import lk.ijse.Apple.Vision.Mobile.Shop.Entity.Product;
import lk.ijse.Apple.Vision.Mobile.Shop.Entity.ProductVariant;
import lk.ijse.Apple.Vision.Mobile.Shop.Enumeration.ProductVariantStatus;
import lk.ijse.Apple.Vision.Mobile.Shop.Repository.ProductRepository;
import lk.ijse.Apple.Vision.Mobile.Shop.Repository.ProductVariantRepository;
import lk.ijse.Apple.Vision.Mobile.Shop.Service.ProductVariantService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@Slf4j
public class ProductVariantServiceImpl implements ProductVariantService {

    private final ProductVariantRepository productVariantRepository;
    private final ProductRepository productRepository;

    public ProductVariantServiceImpl(ProductVariantRepository productVariantRepository, ProductRepository productRepository) {
        this.productVariantRepository = productVariantRepository;
        this.productRepository = productRepository;
    }

    @Override
    public ProductVariantDTO saveProductVariant(ProductVariantDTO productVariantDTO) {
        log.info("Execute saveProductVariant()");

        Product product = productRepository.findById(productVariantDTO.getProductId()).orElse(null);

        ProductVariant variant = new ProductVariant();
        variant.setVariantDescription(productVariantDTO.getVariantDescription());
        variant.setAdditionalPrice(productVariantDTO.getAdditionalPrice());
        variant.setVariantStatus(ProductVariantStatus.ACTIVE);
        variant.setProduct(product);

        ProductVariant savedVariant = productVariantRepository.save(variant);
        log.info("ProductVariant saved successfully with ID: {}", savedVariant.getVariantId());

        productVariantDTO.setVariantId(savedVariant.getVariantId());
        productVariantDTO.setVariantStatus(savedVariant.getVariantStatus());
        return productVariantDTO;
    }

    @Override
    public ProductVariantDTO updateProductVariant(ProductVariantDTO productVariantDTO) {
        log.info("Execute updateProductVariant()");

        ProductVariant variant = productVariantRepository.findById(productVariantDTO.getVariantId()).orElse(new ProductVariant());
        Product product = productRepository.findById(productVariantDTO.getProductId()).orElse(null);

        variant.setVariantDescription(productVariantDTO.getVariantDescription());
        variant.setAdditionalPrice(productVariantDTO.getAdditionalPrice());
        variant.setProduct(product);

        if (productVariantDTO.getVariantStatus() != null) {
            variant.setVariantStatus(productVariantDTO.getVariantStatus());
        }

        ProductVariant updatedVariant = productVariantRepository.save(variant);
        log.info("ProductVariant updated successfully!");

        ProductVariantDTO responseDTO = new ProductVariantDTO();
        responseDTO.setVariantId(updatedVariant.getVariantId());
        responseDTO.setVariantDescription(updatedVariant.getVariantDescription());
        responseDTO.setAdditionalPrice(updatedVariant.getAdditionalPrice());
        responseDTO.setVariantStatus(updatedVariant.getVariantStatus());
        responseDTO.setProductId(updatedVariant.getProduct() != null ? updatedVariant.getProduct().getProductId() : null);

        return responseDTO;
    }

    @Override
    public String deleteProductVariant(Long variantId) {
        log.info("Execute deleteProductVariant()");

        ProductVariant variant = productVariantRepository.findById(variantId).orElse(null);
        if (variant != null) {
            variant.setVariantStatus(ProductVariantStatus.DELETED);
            productVariantRepository.save(variant);
        }

        return "ProductVariant deleted successfully!";
    }

    @Override
    public List<ProductVariantDTO> getAllProductVariants() {
        log.info("Execute getAllProductVariants()");

        List<ProductVariant> variantList = productVariantRepository.findAll();
        List<ProductVariantDTO> responseList = new ArrayList<>();

        for (ProductVariant variant : variantList) {
            if (variant.getVariantStatus() != ProductVariantStatus.DELETED) {
                ProductVariantDTO dto = new ProductVariantDTO();
                dto.setVariantId(variant.getVariantId());
                dto.setVariantDescription(variant.getVariantDescription());
                dto.setAdditionalPrice(variant.getAdditionalPrice());
                dto.setVariantStatus(variant.getVariantStatus());
                dto.setProductId(variant.getProduct() != null ? variant.getProduct().getProductId() : null);

                responseList.add(dto);
            }
        }
        return responseList;
    }

    @Override
    public ProductVariantDTO getProductVariantById(Long variantId) {
        log.info("Execute getProductVariantById()");

        ProductVariant variant = productVariantRepository.findById(variantId).orElse(new ProductVariant());

        ProductVariantDTO dto = new ProductVariantDTO();
        dto.setVariantId(variant.getVariantId());
        dto.setVariantDescription(variant.getVariantDescription());
        dto.setAdditionalPrice(variant.getAdditionalPrice());
        dto.setVariantStatus(variant.getVariantStatus());
        dto.setProductId(variant.getProduct() != null ? variant.getProduct().getProductId() : null);

        return dto;
    }

    @Override
    public List<ProductVariantDTO> getVariantsByProductId(Long productId) {
        log.info("Execute getVariantsByProductId()");

        List<ProductVariant> variantList = productVariantRepository.findAllByProduct_ProductId(productId);
        List<ProductVariantDTO> responseList = new ArrayList<>();

        for (ProductVariant variant : variantList) {
            if (variant.getVariantStatus() != ProductVariantStatus.DELETED) {
                ProductVariantDTO dto = new ProductVariantDTO();
                dto.setVariantId(variant.getVariantId());
                dto.setVariantDescription(variant.getVariantDescription());
                dto.setAdditionalPrice(variant.getAdditionalPrice());
                dto.setVariantStatus(variant.getVariantStatus());
                dto.setProductId(variant.getProduct() != null ? variant.getProduct().getProductId() : null);

                responseList.add(dto);
            }
        }
        return responseList;
    }
}