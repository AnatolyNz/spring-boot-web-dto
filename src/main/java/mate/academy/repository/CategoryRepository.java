package mate.academy.repository;

import java.util.Optional;
import mate.academy.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> getCategoryById(Long id);

    Page<Category> findAll(Pageable pageable);
}
