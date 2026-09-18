package lk.ijse.Apple.Vision.Mobile.Shop.Service.Impl;

import jakarta.transaction.Transactional;
import lk.ijse.Apple.Vision.Mobile.Shop.DTO.ProductVariantDTO;
import lk.ijse.Apple.Vision.Mobile.Shop.Entity.Product;
import lk.ijse.Apple.Vision.Mobile.Shop.Entity.ProductVariant;
import lk.ijse.Apple.Vision.Mobile.Shop.Enumeration.ProductStatus;
import lk.ijse.Apple.Vision.Mobile.Shop.Enumeration.ProductVariantStatus;
import lk.ijse.Apple.Vision.Mobile.Shop.Exception.CustomException;
import lk.ijse.Apple.Vision.Mobile.Shop.Repository.ProductRepository;
import lk.ijse.Apple.Vision.Mobile.Shop.Repository.ProductVariantRepository;
import lk.ijse.Apple.Vision.Mobile.Shop.Service.ProductVariantService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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

        if (productVariantDTO == null) {
            throw new CustomException(400, "Product variant data cannot be null!");
        }
        if (productVariantDTO.getProductId() == null) {
            throw new CustomException(400, "Product ID cannot be null!");
        }
        if (productVariantDTO.getVariantDescription() == null || productVariantDTO.getVariantDescription().trim().isEmpty()) {
            throw new CustomException(400, "Variant description cannot be empty!");
        }
        if (productVariantDTO.getAdditionalPrice() == null || productVariantDTO.getAdditionalPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new CustomException(400, "Additional price cannot be null or negative!");
        }

        Product product = productRepository.findById(productVariantDTO.getProductId())
                .orElseThrow(() -> new CustomException(404, "Product not found with ID: " + productVariantDTO.getProductId()));

        if (product.getProductStatus() == ProductStatus.DELETED) {
            throw new CustomException(400, "Cannot add a variant to an inactive or deleted product!");
        }

        ProductVariant variant = new ProductVariant();
        variant.setVariantDescription(productVariantDTO.getVariantDescription().trim());
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

        if (productVariantDTO == null) {
            throw new CustomException(400, "Product variant update data cannot be null!");
        }
        if (productVariantDTO.getVariantId() == null) {
            throw new CustomException(400, "Product variant ID cannot be null for update!");
        }
        if (productVariantDTO.getVariantDescription() == null || productVariantDTO.getVariantDescription().trim().isEmpty()) {
            throw new CustomException(400, "Variant description cannot be empty!");
        }
        if (productVariantDTO.getAdditionalPrice() == null || productVariantDTO.getAdditionalPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new CustomException(400, "Additional price cannot be null or negative!");
        }

        ProductVariant variant = productVariantRepository.findById(productVariantDTO.getVariantId())
                .orElseThrow(() -> new CustomException(404, "Product variant not found with ID: " + productVariantDTO.getVariantId()));

        if (variant.getVariantStatus() == ProductVariantStatus.DELETED) {
            throw new CustomException(400, "Cannot update a deleted product variant!");
        }

        if (productVariantDTO.getProductId() != null) {
            Product product = productRepository.findById(productVariantDTO.getProductId())
                    .orElseThrow(() -> new CustomException(404, "Product not found with ID: " + productVariantDTO.getProductId()));

            if (product.getProductStatus() == ProductStatus.DELETED) {
                throw new CustomException(400, "Cannot link variant to an inactive or deleted product!");
            }
            variant.setProduct(product);
        }

        variant.setVariantDescription(productVariantDTO.getVariantDescription().trim());
        variant.setAdditionalPrice(productVariantDTO.getAdditionalPrice());

        if (productVariantDTO.getVariantStatus() != null) {
            variant.setVariantStatus(productVariantDTO.getVariantStatus());
        }

        ProductVariant updatedVariant = productVariantRepository.save(variant);
        log.info("ProductVariant updated successfully with ID: {}", updatedVariant.getVariantId());

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

        if (variantId == null) {
            throw new CustomException(400, "Product variant ID cannot be null!");
        }

        ProductVariant variant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new CustomException(404, "Product variant not found with ID: " + variantId));

        if (variant.getVariantStatus() == ProductVariantStatus.DELETED) {
            throw new CustomException(400, "Product variant is already deleted!");
        }

        variant.setVariantStatus(ProductVariantStatus.DELETED);
        productVariantRepository.save(variant);
        log.info("ProductVariant marked as DELETED for ID: {}", variantId);

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

        if (variantId == null) {
            throw new CustomException(400, "Product variant ID cannot be null!");
        }

        ProductVariant variant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new CustomException(404, "Product variant not found with ID: " + variantId));

        if (variant.getVariantStatus() == ProductVariantStatus.DELETED) {
            throw new CustomException(404, "Product variant not found or has been deleted!");
        }

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

        if (productId == null) {
            throw new CustomException(400, "Product ID cannot be null!");
        }

        if (!productRepository.existsById(productId)) {
            throw new CustomException(404, "Product not found with ID: " + productId);
        }

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