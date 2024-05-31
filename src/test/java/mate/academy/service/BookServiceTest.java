package mate.academy.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import mate.academy.dto.BookDto;
import mate.academy.dto.BookSearchParameters;
import mate.academy.dto.CreateBookRequestDto;
import mate.academy.mapper.BookMapper;
import mate.academy.model.Book;
import mate.academy.repository.book.BookRepository;
import mate.academy.repository.book.BookSpecificationBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {
    private static final String BOOK_AUTHOR_1 = "Author 1";
    private static final String BOOK_TITLE_1 = "Book 1";
    private static final String BOOK_PRICE_1 = "300";
    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    @Mock
    private BookSpecificationBuilder bookSpecificationBuilder;

    @Mock
    private BookMapper bookMapper;

    @BeforeEach
    public void setUp() {
        reset(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("Verify save and return for the correct book")
    void saveBook_WithValidBookId_ShouldReturnValidBook() {
        // Mock data
        CreateBookRequestDto bookRequestDto = new CreateBookRequestDto();
        Book book = new Book();
        Book savedBook = new Book();
        BookDto expectedDto = new BookDto();

        // Mock mapper behavior
        when(bookMapper.toModel(bookRequestDto)).thenReturn(book);
        when(bookMapper.toDto(savedBook)).thenReturn(expectedDto);

        // Mock repository behavior
        when(bookRepository.save(book)).thenReturn(savedBook);

        // Test
        BookDto actualDto = bookService.save(bookRequestDto);

        // Assertions
        assertEquals(expectedDto, actualDto);
    }

    @Test
    @DisplayName("Verify the correct book was returned when book exists")
    public void getBookById_WithValidBookId_ShouldReturnValidBook() {
        Long id = 1L;
        Book book = new Book();
        book.setId(id);
        when(bookRepository.getBookById(id)).thenReturn(Optional.of(book));

        BookDto expected = new BookDto();
        expected.setId(id);
        when(bookMapper.toDto(book)).thenReturn(expected);

        BookDto actual = bookService.getBookById(id);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Verify the correct book was not found")
    public void getBookById_WithNonExistingBookId_ShouldThrowException() {
        Long id = 100L;
        when(bookRepository.getBookById(id)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            bookService.getBookById(id);
        });

        String expected = "Can't find book with id " + id;
        String actual = exception.getMessage();
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Verify search() for all books")
    public void findAll_ValidSearchParameters_ShouldReturnListOfBooks() {
        Book book1 = new Book();
        book1.setId(1L);
        book1.setTitle("Book 1");
        Book book2 = new Book();
        book2.setId(2L);
        book2.setTitle("Book 2");

        List<Book> books = Arrays.asList(book1, book2);
        Pageable pageable = PageRequest.of(0, 10);
        PageImpl<Book> page = new PageImpl<>(books, pageable, books.size());

        // Mock repository method
        when(bookRepository.findAll(pageable)).thenReturn(page);

        // Mock mapper method
        BookDto bookDto1 = new BookDto();
        bookDto1.setId(1L);
        bookDto1.setTitle("Book 1");
        BookDto bookDto2 = new BookDto();
        bookDto2.setId(2L);
        bookDto2.setTitle("Book 2");
        when(bookMapper.toDto(book1)).thenReturn(bookDto1);
        when(bookMapper.toDto(book2)).thenReturn(bookDto2);

        // Call the service method
        List<BookDto> result = bookService.findAll(pageable);

        // Verify the result
        assertEquals(2, result.size());
        assertEquals("Book 1", result.get(0).getTitle());
        assertEquals("Book 2", result.get(1).getTitle());
    }

    @Test
    @DisplayName("Verify update() updated books with valid ID")
    public void updateById_WithValidBookId_ShouldUpdateBook() {
        Long id = 1L;
        CreateBookRequestDto bookRequestDto = new CreateBookRequestDto();
        Book book = new Book();
        book.setId(id);

        when(bookRepository.findById(id)).thenReturn(Optional.of(book));

        bookService.updateById(id, bookRequestDto);

        verify(bookRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("""
       Verify deleteById() calls repository with valid ID
            """)
    public void testDeleteById_WithValidBookId_ShouldDeleteBook() {
        // Given
        Long id = 1L;

        // When
        bookService.deleteById(id);

        // Then
        verify(bookRepository, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("""
       Verify search() returns list of BookDto based on parameters
            """)
    public void testSearch_ReturnsListOfBookDtoBasedOnParameters_ShouldReturnListOfBooks() {
        String[] authorParams = {BOOK_AUTHOR_1, "Author 2"};
        String[] titleParams = {BOOK_TITLE_1, "Test Book 2"};
        String[] prices = {BOOK_PRICE_1, "300"};
        List<Book> books = Arrays.asList(new Book(), new Book()); // Sample list of books

        BookSearchParameters params = new BookSearchParameters(
                authorParams, titleParams, prices);

        Specification<Book> bookSpecification = mock(Specification.class);
        when(bookSpecificationBuilder.build(params)).thenReturn(bookSpecification);

        when(bookRepository.findAll(bookSpecification)).thenReturn(books);

        List<BookDto> expected = books.stream().map(bookMapper::toDto).toList();

        List<BookDto> actual = bookService.search(params);

        assertEquals(expected.size(), actual.size());
    }
}
