package mate.academy.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class OrderItemResponseDto {
    private Long id;
    private Long bookId;
    private int quantity;
}
