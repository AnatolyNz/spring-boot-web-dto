package mate.academy.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import mate.academy.dto.BookDto;
import mate.academy.dto.CreateBookRequestDto;
import mate.academy.service.BookService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc // Automatically configure MockMvc
public class BookControllerTest {
    private static final Long VALID_ID = 1L;
    @Autowired
    private static MockMvc mockMvc;
    @Autowired
    private BookService bookService;
    @Autowired
    private DataSource dataSource;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void beforeEach(
            @Autowired DataSource dataSource,
            @Autowired WebApplicationContext applicationContext
    ) throws SQLException {
        mockMvc = MockMvcBuilders
              .webAppContextSetup(applicationContext)
              .apply(springSecurity())
              .build();
        teardown(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                  new ClassPathResource("database/books/add-three-default-books.sql")
            );
        }
    }

    @AfterEach
      void afterEach(
              @Autowired DataSource dataSource
    ) {
        teardown(dataSource);
    }

    @SneakyThrows
    static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                      connection,
                    new ClassPathResource("database/books/remove-all-books.sql")
            );
        }
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Create a new Book")
    void createBook_validCreateBookRequestDto_Success() throws Exception {
        //Given
        CreateBookRequestDto requestDto = new CreateBookRequestDto()
                .setTitle("Book 1")
                .setPrice(BigDecimal.valueOf(100))
                .setAuthor("Author 1")
                .setIsbn("123456");

        BookDto expected = new BookDto()
                .setTitle(requestDto.getTitle())
                .setPrice(requestDto.getPrice())
                .setAuthor(requestDto.getAuthor())
                .setIsbn(requestDto.getIsbn());
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        //When
        MvcResult result = mockMvc.perform(
                        post("/books")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andReturn();

        //Then
        BookDto actual = objectMapper.readValue(result.getResponse()
                .getContentAsString(), BookDto.class);
        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertEquals(expected.getTitle(), actual.getTitle());
        assertEquals(expected.getPrice(), actual.getPrice());
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Find all books")
    void findAll_GivenBookInCatalog_ShouldReturnAllBooks() throws Exception {
        //Given
        List<BookDto> expected = new ArrayList<>();
        expected.add(new BookDto().setId(1L).setTitle("Book 1").setAuthor("Author 1")
                .setIsbn("ISBN-123456").setPrice(BigDecimal.valueOf(100)).setCategoryIds(Set.of()));
        expected.add(new BookDto().setId(2L).setTitle("Book 2").setAuthor("Author 2")
                .setIsbn("ISBN-654321").setPrice(BigDecimal.valueOf(200)).setCategoryIds(Set.of()));
        expected.add(new BookDto().setId(3L).setTitle("Book 3").setAuthor("Author 3")
                .setIsbn("ISBN-908765").setPrice(BigDecimal.valueOf(250)).setCategoryIds(Set.of()));

        //When
        MvcResult result = mockMvc.perform(
                get("/books")
                    .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andReturn();

        //Then
        BookDto[] actual = objectMapper.readValue(result.getResponse()
                    .getContentAsByteArray(), BookDto[].class);
        assertEquals(3, actual.length);
        assertEquals(expected, Arrays.stream(actual).toList());
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Get book by id")
    void getBookById_GivenBookInCatalog_ShouldReturnBookById() throws Exception {
        //Given
        BookDto expected = new BookDto()
                .setId(1L)
                .setTitle("Book 1")
                .setAuthor("Author 1")
                .setIsbn("ISBN-123456")
                .setPrice(BigDecimal.valueOf(100)).setCategoryIds(Set.of());

        //When
        MvcResult result = mockMvc.perform(
                        get("/books/{id}", VALID_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        String jsonResponse = result.getResponse().getContentAsString();

        //Then
        BookDto actual = objectMapper.readValue(jsonResponse, BookDto.class);
        assertEquals(expected, actual);
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Update book with valid parameters")
    void updateBook_WithValidIdAndCreateBookRequestDto_Success() throws Exception {
        CreateBookRequestDto updateBookRequestDto = new CreateBookRequestDto()
                .setTitle("Updated Title")
                .setAuthor("Updated Author")
                .setIsbn("12345")
                .setPrice(BigDecimal.valueOf(250))
                .setDescription("Updated description");

        BookDto expected = new BookDto()
                .setTitle(updateBookRequestDto.getTitle())
                .setAuthor(updateBookRequestDto.getAuthor())
                .setIsbn(updateBookRequestDto.getIsbn())
                .setPrice(updateBookRequestDto.getPrice())
                .setDescription(updateBookRequestDto.getDescription())
                .setId(VALID_ID)
                .setCategoryIds(Set.of());

        String jsonRequest = objectMapper.writeValueAsString(updateBookRequestDto);
        MvcResult result = mockMvc.perform(
                        put("/books/{id}", VALID_ID)
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        BookDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                BookDto.class);
        assertEquals(expected, actual);
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Verify delete() with valid will delete book")
    void deleteBookById_ValidId_Success() throws Exception {
        BookDto expected = new BookDto()
                .setId(1L)
                .setTitle("Book 1")
                .setAuthor("Author 1")
                .setIsbn("ISBN-123456")
                .setPrice(BigDecimal.valueOf(100));

        mockMvc.perform(delete("/books/{id}", VALID_ID))
                .andExpect(status().isNoContent())
                .andReturn();
    }
}
