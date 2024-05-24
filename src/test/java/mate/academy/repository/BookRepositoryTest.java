package mate.academy.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import mate.academy.model.Book;
import mate.academy.repository.book.BookRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class BookRepositoryTest {
    @Autowired
    private BookRepository bookRepository;

    @Test
    @DisplayName("""
       Find all by valid category ID
            """)
    @Sql(scripts = {
            "classpath:database/books/add-books-to-books-table.sql",
            "classpath:database/books/add-category-to-categories-table.sql",
            "classpath:database/books/add-category-to-book.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/books/remove-from-book_category.sql",
            "classpath:database/books/remove-from-books.sql",
            "classpath:database/books/remove-from-categories.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findAllByCategoryId_WithValidCategoryId_ShouldReturnBookList() {
        List<Book> actual = bookRepository.findAllByCategoryId(1L);
        Assertions.assertEquals(3, actual.size());
    }

    @Test
    @DisplayName("""
       Save book
            """)
    @Sql(scripts = {
            "classpath:database/books/add-books-to-books-table.sql",
            "classpath:database/books/add-category-to-categories-table.sql",
            "classpath:database/books/add-category-to-book.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/books/remove-from-book_category.sql",
            "classpath:database/books/remove-from-books.sql",
            "classpath:database/books/remove-from-categories.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
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
    @Sql(scripts = {
            "classpath:database/books/add-books-to-books-table.sql",
            "classpath:database/books/add-category-to-categories-table.sql",
            "classpath:database/books/add-category-to-book.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/books/remove-from-book_category.sql",
            "classpath:database/books/remove-from-books.sql",
            "classpath:database/books/remove-from-categories.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
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
    @Sql(scripts = {
            "classpath:database/books/add-books-to-books-table.sql",
            "classpath:database/books/add-category-to-categories-table.sql",
            "classpath:database/books/add-category-to-book.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/books/remove-from-book_category.sql",
            "classpath:database/books/remove-from-books.sql",
            "classpath:database/books/remove-from-categories.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findAll_WithValidBook_ShouldReturnBookList() {
        List<Book> actual = bookRepository.findAll();
        Assertions.assertEquals(3, actual.size());
    }
}
