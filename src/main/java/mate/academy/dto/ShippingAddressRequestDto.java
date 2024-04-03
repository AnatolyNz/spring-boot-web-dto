package mate.academy.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ShippingAddressRequestDto {
    private String shippingAddress;
}
