package lk.ijse.Apple.Vision.Mobile.Shop.Service.Impl;

import jakarta.transaction.Transactional;
import lk.ijse.Apple.Vision.Mobile.Shop.DTO.ProductDTO;
import lk.ijse.Apple.Vision.Mobile.Shop.Entity.Brand;
import lk.ijse.Apple.Vision.Mobile.Shop.Entity.Category;
import lk.ijse.Apple.Vision.Mobile.Shop.Entity.Product;
import lk.ijse.Apple.Vision.Mobile.Shop.Enumeration.BrandStatus;
import lk.ijse.Apple.Vision.Mobile.Shop.Enumeration.CategoryStatus;
import lk.ijse.Apple.Vision.Mobile.Shop.Enumeration.ProductStatus;
import lk.ijse.Apple.Vision.Mobile.Shop.Exception.CustomException;
import lk.ijse.Apple.Vision.Mobile.Shop.Repository.BrandRepository;
import lk.ijse.Apple.Vision.Mobile.Shop.Repository.CategoryRepository;
import lk.ijse.Apple.Vision.Mobile.Shop.Repository.ProductRepository;
import lk.ijse.Apple.Vision.Mobile.Shop.Service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;

    public ProductServiceImpl(ProductRepository productRepository,
                              BrandRepository brandRepository,
                              CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.brandRepository = brandRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public ProductDTO saveProduct(ProductDTO productDTO) {
        log.info("Execute saveProduct()");

        if (productDTO == null) {
            throw new CustomException(400, "Product data cannot be null!");
        }
        if (productDTO.getProductName() == null || productDTO.getProductName().trim().isEmpty()) {
            throw new CustomException(400, "Product name cannot be empty!");
        }
        if (productDTO.getProductPrice() == null || productDTO.getProductPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new CustomException(400, "Product price must be greater than zero!");
        }
        if (productDTO.getBrandId() == null) {
            throw new CustomException(400, "Brand ID cannot be null!");
        }
        if (productDTO.getCategoryId() == null) {
            throw new CustomException(400, "Category ID cannot be null!");
        }

        Brand brand = brandRepository.findById(productDTO.getBrandId())
                .orElseThrow(() -> new CustomException(404, "Brand not found with ID: " + productDTO.getBrandId()));

        if (brand.getBrandStatus() == BrandStatus.DELETED) {
            throw new CustomException(400, "Cannot assign a deleted brand to the product!");
        }

        Category category = categoryRepository.findById(productDTO.getCategoryId())
                .orElseThrow(() -> new CustomException(404, "Category not found with ID: " + productDTO.getCategoryId()));

        if (category.getCategoryStatus() == CategoryStatus.DELETED) {
            throw new CustomException(400, "Cannot assign a deleted category to the product!");
        }

        Product product = new Product();
        product.setProductName(productDTO.getProductName().trim());
        product.setProductPrice(productDTO.getProductPrice());
        product.setBrand(brand);
        product.setCategory(category);
        product.setProductStatus(ProductStatus.ACTIVE);

        Product savedProduct = productRepository.save(product);
        log.info("Product saved successfully with ID: {}", savedProduct.getProductId());

        productDTO.setProductId(savedProduct.getProductId());
        productDTO.setProductStatus(savedProduct.getProductStatus());
        return productDTO;
    }

    @Override
    public ProductDTO updateProduct(ProductDTO productDTO) {
        log.info("Execute updateProduct()");

        if (productDTO == null) {
            throw new CustomException(400, "Product update data cannot be null!");
        }
        if (productDTO.getProductId() == null) {
            throw new CustomException(400, "Product ID cannot be null for update!");
        }
        if (productDTO.getProductName() == null || productDTO.getProductName().trim().isEmpty()) {
            throw new CustomException(400, "Product name cannot be empty!");
        }
        if (productDTO.getProductPrice() == null || productDTO.getProductPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new CustomException(400, "Product price must be greater than zero!");
        }

        Product product = productRepository.findById(productDTO.getProductId())
                .orElseThrow(() -> new CustomException(404, "Product not found with ID: " + productDTO.getProductId()));

        if (product.getProductStatus() == ProductStatus.DELETED) {
            throw new CustomException(400, "Cannot update a deleted product!");
        }

        if (productDTO.getBrandId() != null) {
            Brand brand = brandRepository.findById(productDTO.getBrandId())
                    .orElseThrow(() -> new CustomException(404, "Brand not found with ID: " + productDTO.getBrandId()));

            if (brand.getBrandStatus() == BrandStatus.DELETED) {
                throw new CustomException(400, "Cannot assign a deleted brand to the product!");
            }
            product.setBrand(brand);
        }

        if (productDTO.getCategoryId() != null) {
            Category category = categoryRepository.findById(productDTO.getCategoryId())
                    .orElseThrow(() -> new CustomException(404, "Category not found with ID: " + productDTO.getCategoryId()));

            if (category.getCategoryStatus() == CategoryStatus.DELETED) {
                throw new CustomException(400, "Cannot assign a deleted category to the product!");
            }
            product.setCategory(category);
        }

        product.setProductName(productDTO.getProductName().trim());
        product.setProductPrice(productDTO.getProductPrice());

        if (productDTO.getProductStatus() != null) {
            product.setProductStatus(productDTO.getProductStatus());
        }

        Product updatedProduct = productRepository.save(product);
        log.info("Product updated successfully with ID: {}", updatedProduct.getProductId());

        ProductDTO responseDTO = new ProductDTO();
        responseDTO.setProductId(updatedProduct.getProductId());
        responseDTO.setProductName(updatedProduct.getProductName());
        responseDTO.setProductPrice(updatedProduct.getProductPrice());
        responseDTO.setBrandId(updatedProduct.getBrand() != null ? updatedProduct.getBrand().getBrandId() : null);
        responseDTO.setCategoryId(updatedProduct.getCategory() != null ? updatedProduct.getCategory().getCategoryId() : null);
        responseDTO.setProductStatus(updatedProduct.getProductStatus());

        return responseDTO;
    }

    @Override
    public String deleteProduct(Long productId) {
        log.info("Execute deleteProduct()");

        if (productId == null) {
            throw new CustomException(400, "Product ID cannot be null!");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(404, "Product not found with ID: " + productId));

        if (product.getProductStatus() == ProductStatus.DELETED) {
            throw new CustomException(400, "Product is already deleted!");
        }

        product.setProductStatus(ProductStatus.DELETED);
        productRepository.save(product);
        log.info("Product marked as DELETED for ID: {}", productId);

        return "Product deleted successfully!";
    }

    @Override
    public List<ProductDTO> getAllProducts() {
        log.info("Execute getAllProducts()");

        List<Product> productList = productRepository.findAll();
        List<ProductDTO> responseList = new ArrayList<>();

        for (Product product : productList) {
            if (product.getProductStatus() != ProductStatus.DELETED) {
                ProductDTO productDTO = new ProductDTO();
                productDTO.setProductId(product.getProductId());
                productDTO.setProductName(product.getProductName());
                productDTO.setProductPrice(product.getProductPrice());
                productDTO.setBrandId(product.getBrand() != null ? product.getBrand().getBrandId() : null);
                productDTO.setCategoryId(product.getCategory() != null ? product.getCategory().getCategoryId() : null);
                productDTO.setProductStatus(product.getProductStatus());

                responseList.add(productDTO);
            }
        }
        return responseList;
    }

    @Override
    public ProductDTO getProductById(Long productId) {
        log.info("Execute getProductById()");

        if (productId == null) {
            throw new CustomException(400, "Product ID cannot be null!");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(404, "Product not found with ID: " + productId));

        if (product.getProductStatus() == ProductStatus.DELETED) {
            throw new CustomException(404, "Product not found or has been deleted!");
        }

        ProductDTO productDTO = new ProductDTO();
        productDTO.setProductId(product.getProductId());
        productDTO.setProductName(product.getProductName());
        productDTO.setProductPrice(product.getProductPrice());
        productDTO.setBrandId(product.getBrand() != null ? product.getBrand().getBrandId() : null);
        productDTO.setCategoryId(product.getCategory() != null ? product.getCategory().getCategoryId() : null);
        productDTO.setProductStatus(product.getProductStatus());

        return productDTO;
    }
}