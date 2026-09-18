package lk.ijse.Apple.Vision.Mobile.Shop.Service.Impl;

import jakarta.transaction.Transactional;
import lk.ijse.Apple.Vision.Mobile.Shop.DTO.BrandDTO;
import lk.ijse.Apple.Vision.Mobile.Shop.Entity.Brand;
import lk.ijse.Apple.Vision.Mobile.Shop.Enumeration.BrandStatus;
import lk.ijse.Apple.Vision.Mobile.Shop.Exception.CustomException;
import lk.ijse.Apple.Vision.Mobile.Shop.Repository.BrandRepository;
import lk.ijse.Apple.Vision.Mobile.Shop.Service.BrandService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@Slf4j
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;

    public BrandServiceImpl(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }

    @Override
    public BrandDTO saveBrand(BrandDTO brandDTO) {
        log.info("Execute saveBrand()");

        if (brandDTO == null) {
            throw new CustomException(400, "Brand data cannot be null!");
        }
        if (brandDTO.getBrandName() == null || brandDTO.getBrandName().trim().isEmpty()) {
            throw new CustomException(400, "Brand name cannot be empty!");
        }

        Brand brand = new Brand();
        brand.setBrandName(brandDTO.getBrandName().trim());
        brand.setBrandDescription(brandDTO.getBrandDescription());
        brand.setCountry(brandDTO.getCountry());
        brand.setBrandStatus(BrandStatus.ACTIVE);

        Brand savedBrand = brandRepository.save(brand);
        log.info("Brand saved successfully with ID: {}", savedBrand.getBrandId());

        brandDTO.setBrandId(savedBrand.getBrandId());
        brandDTO.setBrandStatus(savedBrand.getBrandStatus());
        return brandDTO;
    }

    @Override
    public BrandDTO updateBrand(BrandDTO brandDTO) {
        log.info("Execute updateBrand()");

        if (brandDTO == null) {
            throw new CustomException(400, "Brand update data cannot be null!");
        }
        if (brandDTO.getBrandId() == null) {
            throw new CustomException(400, "Brand ID cannot be null for update!");
        }
        if (brandDTO.getBrandName() == null || brandDTO.getBrandName().trim().isEmpty()) {
            throw new CustomException(400, "Brand name cannot be empty!");
        }

        Brand brand = brandRepository.findById(brandDTO.getBrandId())
                .orElseThrow(() -> new CustomException(404, "Brand not found with ID: " + brandDTO.getBrandId()));

        if (brand.getBrandStatus() == BrandStatus.DELETED) {
            throw new CustomException(400, "Cannot update a deleted brand!");
        }

        brand.setBrandName(brandDTO.getBrandName().trim());
        brand.setBrandDescription(brandDTO.getBrandDescription());
        brand.setCountry(brandDTO.getCountry());

        if (brandDTO.getBrandStatus() != null) {
            brand.setBrandStatus(brandDTO.getBrandStatus());
        }

        Brand updatedBrand = brandRepository.save(brand);
        log.info("Brand updated successfully with ID: {}", updatedBrand.getBrandId());

        BrandDTO responseDTO = new BrandDTO();
        responseDTO.setBrandId(updatedBrand.getBrandId());
        responseDTO.setBrandName(updatedBrand.getBrandName());
        responseDTO.setBrandDescription(updatedBrand.getBrandDescription());
        responseDTO.setCountry(updatedBrand.getCountry());
        responseDTO.setBrandStatus(updatedBrand.getBrandStatus());

        return responseDTO;
    }

    @Override
    public String deleteBrand(Long brandId) {
        log.info("Execute deleteBrand()");

        if (brandId == null) {
            throw new CustomException(400, "Brand ID cannot be null!");
        }

        Brand brand = brandRepository.findById(brandId)
                .orElseThrow(() -> new CustomException(404, "Brand not found with ID: " + brandId));

        if (brand.getBrandStatus() == BrandStatus.DELETED) {
            throw new CustomException(400, "Brand is already deleted!");
        }

        brand.setBrandStatus(BrandStatus.DELETED);
        brandRepository.save(brand);
        log.info("Brand marked as DELETED for ID: {}", brandId);

        return "Brand deleted successfully!";
    }

    @Override
    public List<BrandDTO> getAllBrands() {
        log.info("Execute getAllBrands()");

        List<Brand> brandList = brandRepository.findAll();
        List<BrandDTO> responseList = new ArrayList<>();

        for (Brand brand : brandList) {
            if (brand.getBrandStatus() != BrandStatus.DELETED) {
                BrandDTO brandDTO = new BrandDTO();
                brandDTO.setBrandId(brand.getBrandId());
                brandDTO.setBrandName(brand.getBrandName());
                brandDTO.setBrandDescription(brand.getBrandDescription());
                brandDTO.setCountry(brand.getCountry());
                brandDTO.setBrandStatus(brand.getBrandStatus());

                responseList.add(brandDTO);
            }
        }
        return responseList;
    }

    @Override
    public BrandDTO getBrandById(Long brandId) {
        log.info("Execute getBrandById()");

        if (brandId == null) {
            throw new CustomException(400, "Brand ID cannot be null!");
        }

        Brand brand = brandRepository.findById(brandId)
                .orElseThrow(() -> new CustomException(404, "Brand not found with ID: " + brandId));

        if (brand.getBrandStatus() == BrandStatus.DELETED) {
            throw new CustomException(404, "Brand not found or has been deleted!");
        }

        BrandDTO brandDTO = new BrandDTO();
        brandDTO.setBrandId(brand.getBrandId());
        brandDTO.setBrandName(brand.getBrandName());
        brandDTO.setBrandDescription(brand.getBrandDescription());
        brandDTO.setCountry(brand.getCountry());
        brandDTO.setBrandStatus(brand.getBrandStatus());

        return brandDTO;
    }
}