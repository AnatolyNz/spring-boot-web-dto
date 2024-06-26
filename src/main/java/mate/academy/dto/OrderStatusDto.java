package mate.academy.dto;

import lombok.Data;
import lombok.experimental.Accessors;
import mate.academy.model.Order;

@Data
@Accessors(chain = true)
public class OrderStatusDto {
    private Order.Status status;
}
