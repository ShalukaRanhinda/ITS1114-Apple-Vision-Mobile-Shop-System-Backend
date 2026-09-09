package lk.ijse.Apple.Vision.Mobile.Shop.Controller;

import lk.ijse.Apple.Vision.Mobile.Shop.Constant.CommonResponse;
import lk.ijse.Apple.Vision.Mobile.Shop.DTO.CategoryDTO;
import lk.ijse.Apple.Vision.Mobile.Shop.Service.CategoryService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static lk.ijse.Apple.Vision.Mobile.Shop.Constant.ResponseMessage.SUCCESS_MESSAGE;
import static lk.ijse.Apple.Vision.Mobile.Shop.Constant.ResponseStatusCode.OPERATION_SUCCESS;

@RestController
@RequestMapping("api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping(value = "/saveCategory", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse saveCategory(@RequestBody CategoryDTO categoryDTO) {
        CategoryDTO savedCategoryDTO = categoryService.saveCategory(categoryDTO);
        return new CommonResponse(OPERATION_SUCCESS, savedCategoryDTO, SUCCESS_MESSAGE);
    }

    @PutMapping(value = "/updateCategory", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse updateCategory(@RequestBody CategoryDTO categoryDTO) {
        CategoryDTO updatedCategoryDTO = categoryService.updateCategory(categoryDTO);
        return new CommonResponse(OPERATION_SUCCESS, updatedCategoryDTO, SUCCESS_MESSAGE);
    }

    @DeleteMapping(value = "/deleteCategory/{categoryId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse deleteCategory(@PathVariable Long categoryId) {
        String message = categoryService.deleteCategory(categoryId);
        return new CommonResponse(OPERATION_SUCCESS, message, SUCCESS_MESSAGE);
    }

    @GetMapping(value = "/getAllCategories", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getAllCategories() {
        List<CategoryDTO> categoryList = categoryService.getAllCategories();
        return new CommonResponse(OPERATION_SUCCESS, categoryList, SUCCESS_MESSAGE);
    }

    @GetMapping(value = "/getCategory/{categoryId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getCategory(@PathVariable Long categoryId) {
        CategoryDTO categoryDTO = categoryService.getCategoryById(categoryId);
        return new CommonResponse(OPERATION_SUCCESS, categoryDTO, SUCCESS_MESSAGE);
    }
}