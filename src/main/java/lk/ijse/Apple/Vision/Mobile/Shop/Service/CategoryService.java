package lk.ijse.Apple.Vision.Mobile.Shop.Service;

import lk.ijse.Apple.Vision.Mobile.Shop.DTO.CategoryDTO;
import java.util.List;

public interface CategoryService {
    CategoryDTO saveCategory(CategoryDTO categoryDTO);
    CategoryDTO updateCategory(CategoryDTO categoryDTO);
    String deleteCategory(Long categoryId);
    List<CategoryDTO> getAllCategories();
    CategoryDTO getCategoryById(Long categoryId);
}