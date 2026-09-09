package lk.ijse.Apple.Vision.Mobile.Shop.Service.Impl;

import jakarta.transaction.Transactional;
import lk.ijse.Apple.Vision.Mobile.Shop.DTO.BrandDTO;
import lk.ijse.Apple.Vision.Mobile.Shop.Entity.Brand;
import lk.ijse.Apple.Vision.Mobile.Shop.Enumeration.BrandStatus;
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

        Brand brand = new Brand();
        brand.setBrandName(brandDTO.getBrandName());
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

        Brand brand = brandRepository.findById(brandDTO.getBrandId()).orElse(new Brand());

        brand.setBrandName(brandDTO.getBrandName());
        brand.setBrandDescription(brandDTO.getBrandDescription());
        brand.setCountry(brandDTO.getCountry());

        if (brandDTO.getBrandStatus() != null) {
            brand.setBrandStatus(brandDTO.getBrandStatus());
        }

        Brand updatedBrand = brandRepository.save(brand);
        log.info("Brand updated successfully!");

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

        Brand brand = brandRepository.findById(brandId).orElse(null);
        if (brand != null) {
            brand.setBrandStatus(BrandStatus.DELETED);
            brandRepository.save(brand);
        }

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

        Brand brand = brandRepository.findById(brandId).orElse(new Brand());

        BrandDTO brandDTO = new BrandDTO();
        brandDTO.setBrandId(brand.getBrandId());
        brandDTO.setBrandName(brand.getBrandName());
        brandDTO.setBrandDescription(brand.getBrandDescription());
        brandDTO.setCountry(brand.getCountry());
        brandDTO.setBrandStatus(brand.getBrandStatus());

        return brandDTO;
    }
}