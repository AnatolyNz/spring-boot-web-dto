package mate.academy.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Optional;
import mate.academy.model.Category;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CategoryRepositoryTest {
    private static final int PAGE_NUMBER = 0;
    private static final int PAGE_SIZE = 2;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    @DisplayName("""
       Get category by valid ID
            """)
    @Sql(scripts = {
            "classpath:database/books/add-category-to-categories-table.sql",
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/books/remove-from-categories.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getCategoryById_WithValidId_ShouldReturnOptionalCategory() {
        Category expected = new Category();
        expected.setId(1L);
        expected.setName("Poetry");
        expected.setDescription("Poems that you will love");
        expected.setDeleted(false);
        Optional<Category> actualOptional = categoryRepository.getCategoryById(1L);

        Assertions.assertTrue(actualOptional.isPresent(), "Category with id 1 should be present");
        Category actual = actualOptional.get();

        Assertions.assertEquals(expected.getId(), actual.getId());
        Assertions.assertEquals(expected.getName(), actual.getName());
        Assertions.assertEquals(expected.getDescription(), actual.getDescription());
        Assertions.assertEquals(expected.isDeleted(), actual.isDeleted());
    }

    @Test
    @DisplayName("""
       Find all categories with pagination
            """)
    @Sql(scripts = {
            "classpath:database/books/add-category-to-categories-table.sql",
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/books/remove-from-categories.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findAll_WithPagination_ShouldReturnPage() {
        Pageable pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
        Page<Category> actual = categoryRepository.findAll(pageable);
        assertFalse(actual.isEmpty());
        assertEquals(PAGE_SIZE, actual.getContent().size());
    }
}
