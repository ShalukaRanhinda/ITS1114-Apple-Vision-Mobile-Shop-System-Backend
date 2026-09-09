package lk.ijse.Apple.Vision.Mobile.Shop.DTO;

import lk.ijse.Apple.Vision.Mobile.Shop.Enumeration.BrandStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BrandDTO {
    private Long brandId;
    private String brandName;
    private String brandDescription;
    private String country;
    private BrandStatus brandStatus;
}