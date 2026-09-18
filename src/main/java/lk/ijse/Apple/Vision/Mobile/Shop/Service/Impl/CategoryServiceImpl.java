package lk.ijse.Apple.Vision.Mobile.Shop.Service.Impl;

import jakarta.transaction.Transactional;
import lk.ijse.Apple.Vision.Mobile.Shop.DTO.CategoryDTO;
import lk.ijse.Apple.Vision.Mobile.Shop.Entity.Category;
import lk.ijse.Apple.Vision.Mobile.Shop.Enumeration.CategoryStatus;
import lk.ijse.Apple.Vision.Mobile.Shop.Exception.CustomException;
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

        if (categoryDTO == null) {
            throw new CustomException(400, "Category data cannot be null!");
        }
        if (categoryDTO.getCategoryName() == null || categoryDTO.getCategoryName().trim().isEmpty()) {
            throw new CustomException(400, "Category name cannot be empty!");
        }

        Category category = new Category();
        category.setCategoryName(categoryDTO.getCategoryName().trim());
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

        if (categoryDTO == null) {
            throw new CustomException(400, "Category update data cannot be null!");
        }
        if (categoryDTO.getCategoryId() == null) {
            throw new CustomException(400, "Category ID cannot be null for update!");
        }
        if (categoryDTO.getCategoryName() == null || categoryDTO.getCategoryName().trim().isEmpty()) {
            throw new CustomException(400, "Category name cannot be empty!");
        }

        Category category = categoryRepository.findById(categoryDTO.getCategoryId())
                .orElseThrow(() -> new CustomException(404, "Category not found with ID: " + categoryDTO.getCategoryId()));

        if (category.getCategoryStatus() == CategoryStatus.DELETED) {
            throw new CustomException(400, "Cannot update a deleted category!");
        }

        category.setCategoryName(categoryDTO.getCategoryName().trim());
        category.setCategoryDescription(categoryDTO.getCategoryDescription());

        if (categoryDTO.getCategoryStatus() != null) {
            category.setCategoryStatus(categoryDTO.getCategoryStatus());
        }

        Category updatedCategory = categoryRepository.save(category);
        log.info("Category updated successfully with ID: {}", updatedCategory.getCategoryId());

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

        if (categoryId == null) {
            throw new CustomException(400, "Category ID cannot be null!");
        }

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CustomException(404, "Category not found with ID: " + categoryId));

        if (category.getCategoryStatus() == CategoryStatus.DELETED) {
            throw new CustomException(400, "Category is already deleted!");
        }

        category.setCategoryStatus(CategoryStatus.DELETED);
        categoryRepository.save(category);
        log.info("Category marked as DELETED for ID: {}", categoryId);

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

        if (categoryId == null) {
            throw new CustomException(400, "Category ID cannot be null!");
        }

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CustomException(404, "Category not found with ID: " + categoryId));

        if (category.getCategoryStatus() == CategoryStatus.DELETED) {
            throw new CustomException(404, "Category not found or has been deleted!");
        }

        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setCategoryId(category.getCategoryId());
        categoryDTO.setCategoryName(category.getCategoryName());
        categoryDTO.setCategoryDescription(category.getCategoryDescription());
        categoryDTO.setCategoryStatus(category.getCategoryStatus());

        return categoryDTO;
    }
}