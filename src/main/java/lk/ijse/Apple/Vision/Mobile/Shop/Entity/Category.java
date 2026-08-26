package lk.ijse.Apple.Vision.Mobile.Shop.Entity;

import jakarta.persistence.*;
import lk.ijse.Apple.Vision.Mobile.Shop.Enumeration.CategoryStatus;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;
    private String categoryName;
    private String categoryDescription;
    @Enumerated(EnumType.STRING)
    private CategoryStatus categoryStatus = CategoryStatus.ACTIVE;
}
