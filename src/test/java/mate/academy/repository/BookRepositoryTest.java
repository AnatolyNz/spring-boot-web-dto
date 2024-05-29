package mate.academy.repository;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;
import mate.academy.model.Book;
import mate.academy.repository.book.BookRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

@SpringBootTest
public class BookRepositoryTest {
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private DataSource dataSource;

    @BeforeEach
    void beforeEach() {
        executeScripts(
                "database/books/add-books-to-books-table.sql",
                "database/books/add-category-to-categories-table.sql",
                "database/books/add-category-to-book.sql"
        );
    }

    @AfterEach
    void afterEach() {
        executeScripts(
                "database/books/remove-from-book_category.sql",
                "database/books/remove-from-books.sql",
                "database/books/remove-from-categories.sql"
        );
    }

    private void executeScripts(String... scriptPaths) {
        try (Connection connection = dataSource.getConnection()) {
            for (String scriptPath : scriptPaths) {
                ScriptUtils.executeSqlScript(
                        connection,
                        new ClassPathResource(scriptPath)
                );
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    @DisplayName("""
       Find all by valid category ID
            """)
    void findAllByCategoryId_WithValidCategoryId_ShouldReturnBookList() {
        List<Book> actual = bookRepository.findAllByCategoryId(1L);
        Assertions.assertEquals(3, actual.size());
    }

    @Test
    @DisplayName("Save book")
    void saveBook_WithValidBook_ShouldReturnBook() {
        Book book = new Book();
        book.setTitle("Book 4");
        book.setId(4L);
        book.setPrice(BigDecimal.valueOf(300));
        book.setAuthor("Author 4");
        book.setIsbn("ISBN-908777");
        book.setDescription("Description for Book 4");
        book.setCoverImage("image4.jpg");
        book.setDeleted(false);
        Book actual = bookRepository.save(book);
        Book expected = book;
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("""
       Get book by valid ID
            """)
    void getBookById_WithValidId_ShouldReturnOptionalBook() {
        Book expected = new Book();
        expected.setTitle("Book 1");
        expected.setId(1L);
        expected.setPrice(BigDecimal.valueOf(100));
        expected.setAuthor("Author 1");
        expected.setIsbn("ISBN-123456");
        expected.setDescription("Description for Book 1");
        expected.setCoverImage("image1.jpg");
        expected.setDeleted(false);
        Optional<Book> actualOptional = bookRepository.getBookById(1L);

        Assertions.assertTrue(actualOptional.isPresent(), "Book with id 1 should be present");
        Book actual = actualOptional.get();
        Assertions.assertEquals(expected.getId(), actual.getId());
        Assertions.assertEquals(expected.getTitle(), actual.getTitle());
        Assertions.assertEquals(expected.getPrice(), actual.getPrice());
        Assertions.assertEquals(expected.getAuthor(), actual.getAuthor());
        Assertions.assertEquals(expected.getIsbn(), actual.getIsbn());
        Assertions.assertEquals(expected.getDescription(), actual.getDescription());
        Assertions.assertEquals(expected.getCoverImage(), actual.getCoverImage());
        Assertions.assertEquals(expected.isDeleted(), actual.isDeleted());
    }

    @Test
    @DisplayName("""
       Find all
            """)
    void findAll_WithValidBook_ShouldReturnBookList() {
        List<Book> actual = bookRepository.findAll();
        Assertions.assertEquals(3, actual.size());
    }
}
