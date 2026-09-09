package lk.ijse.Apple.Vision.Mobile.Shop.Service.Impl;

import jakarta.transaction.Transactional;
import lk.ijse.Apple.Vision.Mobile.Shop.DTO.ProductDTO;
import lk.ijse.Apple.Vision.Mobile.Shop.Entity.Brand;
import lk.ijse.Apple.Vision.Mobile.Shop.Entity.Category;
import lk.ijse.Apple.Vision.Mobile.Shop.Entity.Product;
import lk.ijse.Apple.Vision.Mobile.Shop.Enumeration.ProductStatus;
import lk.ijse.Apple.Vision.Mobile.Shop.Repository.BrandRepository;
import lk.ijse.Apple.Vision.Mobile.Shop.Repository.CategoryRepository;
import lk.ijse.Apple.Vision.Mobile.Shop.Repository.ProductRepository;
import lk.ijse.Apple.Vision.Mobile.Shop.Service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

        Brand brand = brandRepository.findById(productDTO.getBrandId()).orElse(null);
        Category category = categoryRepository.findById(productDTO.getCategoryId()).orElse(null);

        Product product = new Product();
        product.setProductName(productDTO.getProductName());
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

        Product product = productRepository.findById(productDTO.getProductId()).orElse(new Product());
        Brand brand = brandRepository.findById(productDTO.getBrandId()).orElse(null);
        Category category = categoryRepository.findById(productDTO.getCategoryId()).orElse(null);

        product.setProductName(productDTO.getProductName());
        product.setProductPrice(productDTO.getProductPrice());
        product.setBrand(brand);
        product.setCategory(category);

        if (productDTO.getProductStatus() != null) {
            product.setProductStatus(productDTO.getProductStatus());
        }

        Product updatedProduct = productRepository.save(product);
        log.info("Product updated successfully!");

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

        Product product = productRepository.findById(productId).orElse(null);
        if (product != null) {
            product.setProductStatus(ProductStatus.DELETED);
            productRepository.save(product);
        }

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

        Product product = productRepository.findById(productId).orElse(new Product());

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