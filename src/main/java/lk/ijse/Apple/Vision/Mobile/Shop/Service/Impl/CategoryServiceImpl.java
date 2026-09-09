package lk.ijse.Apple.Vision.Mobile.Shop.Service.Impl;

import jakarta.transaction.Transactional;
import lk.ijse.Apple.Vision.Mobile.Shop.DTO.CategoryDTO;
import lk.ijse.Apple.Vision.Mobile.Shop.Entity.Category;
import lk.ijse.Apple.Vision.Mobile.Shop.Enumeration.CategoryStatus;
import lk.ijse.Apple.Vision.Mobile.Shop.Repository.CategoryRepository;
import lk.ijse.Apple.Vision.Mobile.Shop.Service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public CategoryDTO saveCategory(CategoryDTO categoryDTO) {
        log.info("Execute saveCategory()");

        Category category = new Category();
        category.setCategoryName(categoryDTO.getCategoryName());
        category.setCategoryDescription(categoryDTO.getCategoryDescription());
        category.setCategoryStatus(CategoryStatus.ACTIVE);

        Category savedCategory = categoryRepository.save(category);
        log.info("Category saved successfully with ID: {}", savedCategory.getCategoryId());

        categoryDTO.setCategoryId(savedCategory.getCategoryId());
        categoryDTO.setCategoryStatus(savedCategory.getCategoryStatus());
        return categoryDTO;
    }

    @Override
    public CategoryDTO updateCategory(CategoryDTO categoryDTO) {
        log.info("Execute updateCategory()");

        Category category = categoryRepository.findById(categoryDTO.getCategoryId()).orElse(new Category());

        category.setCategoryName(categoryDTO.getCategoryName());
        category.setCategoryDescription(categoryDTO.getCategoryDescription());

        if (categoryDTO.getCategoryStatus() != null) {
            category.setCategoryStatus(categoryDTO.getCategoryStatus());
        }

        Category updatedCategory = categoryRepository.save(category);
        log.info("Category updated successfully!");

        CategoryDTO responseDTO = new CategoryDTO();
        responseDTO.setCategoryId(updatedCategory.getCategoryId());
        responseDTO.setCategoryName(updatedCategory.getCategoryName());
        responseDTO.setCategoryDescription(updatedCategory.getCategoryDescription());
        responseDTO.setCategoryStatus(updatedCategory.getCategoryStatus());

        return responseDTO;
    }

    @Override
    public String deleteCategory(Long categoryId) {
        log.info("Execute deleteCategory()");

        Category category = categoryRepository.findById(categoryId).orElse(null);
        if (category != null) {
            category.setCategoryStatus(CategoryStatus.DELETED);
            categoryRepository.save(category);
        }

        return "Category deleted successfully!";
    }

    @Override
    public List<CategoryDTO> getAllCategories() {
        log.info("Execute getAllCategories()");

        List<Category> categoryList = categoryRepository.findAll();
        List<CategoryDTO> responseList = new ArrayList<>();

        for (Category category : categoryList) {
            if (category.getCategoryStatus() != CategoryStatus.DELETED) {
                CategoryDTO categoryDTO = new CategoryDTO();
                categoryDTO.setCategoryId(category.getCategoryId());
                categoryDTO.setCategoryName(category.getCategoryName());
                categoryDTO.setCategoryDescription(category.getCategoryDescription());
                categoryDTO.setCategoryStatus(category.getCategoryStatus());

                responseList.add(categoryDTO);
            }
        }
        return responseList;
    }

    @Override
    public CategoryDTO getCategoryById(Long categoryId) {
        log.info("Execute getCategoryById()");

        Category category = categoryRepository.findById(categoryId).orElse(new Category());

        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setCategoryId(category.getCategoryId());
        categoryDTO.setCategoryName(category.getCategoryName());
        categoryDTO.setCategoryDescription(category.getCategoryDescription());
        categoryDTO.setCategoryStatus(category.getCategoryStatus());

        return categoryDTO;
    }
}