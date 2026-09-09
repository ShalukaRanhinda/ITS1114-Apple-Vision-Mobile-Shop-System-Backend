package lk.ijse.Apple.Vision.Mobile.Shop.DTO;

import lk.ijse.Apple.Vision.Mobile.Shop.Enumeration.CategoryStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDTO {
    private Long categoryId;
    private String categoryName;
    private String categoryDescription;
    private CategoryStatus categoryStatus;
}