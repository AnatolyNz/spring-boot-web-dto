package mate.academy.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Data
@Table(name = "order_items")
@SQLDelete(sql = "UPDATE order_items SET is_Deleted=true WHERE id=?")
@Where(clause = "is_deleted=false")
@Accessors(chain = true)
@ToString(exclude = {"order"})
@EqualsAndHashCode(exclude = {"order"})
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;
    @NotNull
    private int quantity;
    @NotNull
    private BigDecimal price;
    @Column(nullable = false)
    private boolean isDeleted = false;

    public OrderItem(CartItem cartItem) {
    }
}
