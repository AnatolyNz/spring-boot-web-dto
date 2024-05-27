package mate.academy.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import mate.academy.dto.BookDtoWithoutCategoryIds;
import mate.academy.dto.CategoryDto;
import mate.academy.dto.CategoryResponseDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class CategoryControllerTest {
    private static final int PAGE_NUMBER = 0;
    private static final int PAGE_SIZE = 10;
    private static final Long VALID_ID = 1L;
    private static final Long INVALID_ID = 1000L;

    @Autowired
    private static MockMvc mockMvc;
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
                    new ClassPathResource("database/categories"
                            + "/add-category-to-categories-table.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/categories/add-books-to-books-table.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/categories/add-category-to-book.sql")
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
                    new ClassPathResource("database/categories/remove-from-categories.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/categories/remove-from-books.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/categories/remove-from-book_category.sql")
            );
        }
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Create a new Category")
    void createCategory_validCreateCategoryRequestDto_Success() throws Exception {
        CategoryDto requestDto = new CategoryDto()
                .setName("New Category")
                .setDescription("New description");

        CategoryResponseDto expected = new CategoryResponseDto()
                .setDescription(requestDto.getDescription())
                .setName(requestDto.getName());

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult mvcResult = mockMvc.perform(
                        post("/categories")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();

        CategoryResponseDto actual = objectMapper
                .readValue(jsonResponse, CategoryResponseDto.class);
        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getDescription(), actual.getDescription());
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Get all categories from DB")
    void getAll_WithPagination_ShouldReturnPageWithCategories() throws Exception {
        List<CategoryResponseDto> expected = new ArrayList<>();
        expected.add(new CategoryResponseDto()
                .setName("Poetry")
                .setId(1L)
                .setDescription("Poems that you will love")
        );
        expected.add(new CategoryResponseDto()
                .setName("Fiction")
                .setId(2L)
                .setDescription("Nice fiction to read")
        );

        Pageable pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);

        MvcResult mvcResult = mockMvc.perform(get("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("page", String.valueOf(
                                pageable.getPageNumber()))
                        .param("size", String.valueOf(pageable.getPageSize())))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        List<CategoryResponseDto> actual = objectMapper.readValue(
                jsonResponse,
                new TypeReference<>() {}
        );
        assertNotNull(actual);
        assertEquals(expected.size(), actual.size());
        assertEquals(expected, actual);
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Update category with valid parameters")
    void updateCategory_WithValidIdAndRequestDto_Success() throws Exception {
        CategoryDto updateRequestDto = new CategoryDto()
                .setName("Updated Name")
                .setDescription("Updated description");
        CategoryResponseDto expected = new CategoryResponseDto()
                .setName("Updated Name")
                .setDescription("Updated description")
                .setId(VALID_ID);

        String jsonRequest = objectMapper.writeValueAsString(updateRequestDto);

        MvcResult mvcResult = mockMvc.perform(
                        put("/categories/{id}", VALID_ID)
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        CategoryResponseDto actual = objectMapper
                .readValue(jsonResponse, CategoryResponseDto.class);

        assertEquals(expected, actual);
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Delete with valid Id will delete category")
    void delete_validId_Success() throws Exception {
        mockMvc.perform(delete("/categories/{id}", VALID_ID))
                .andExpect(status().isNoContent())
                .andReturn();
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Get category with valid Id from DB")
    void getCategoryById_validId_shouldReturnCategory() throws Exception {
        CategoryResponseDto expected = new CategoryResponseDto()
                .setName("Poetry")
                .setId(1L)
                .setDescription("Poems that you will love");

        MvcResult mvcResult = mockMvc.perform(
                        get("/categories/{id}", VALID_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        String jsonResponse = mvcResult.getResponse().getContentAsString();
        CategoryResponseDto actual = objectMapper
                .readValue(jsonResponse, CategoryResponseDto.class);
        assertEquals(expected, actual);
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Search books by category id")
    void getBooksByCategoryId_validId_success() throws Exception {
        List<BookDtoWithoutCategoryIds> expected = new ArrayList<>();
        expected.add(new BookDtoWithoutCategoryIds()
                .setId(1L)
                .setTitle("Book 1")
                .setAuthor("Author 1")
                .setIsbn("ISBN-123456")
                .setPrice(BigDecimal.valueOf(100))
                .setDescription("Description for Book 1")
                .setCoverImage("image1.jpg"));
        expected.add(new BookDtoWithoutCategoryIds()
                .setId(2L)
                .setTitle("Book 2")
                .setAuthor("Author 2")
                .setIsbn("ISBN-654321")
                .setPrice(BigDecimal.valueOf(200))
                .setDescription("Description for Book 2")
                .setCoverImage("image2.jpg"));
        expected.add(new BookDtoWithoutCategoryIds()
                .setId(3L)
                .setTitle("Book 3")
                .setAuthor("Author 3")
                .setIsbn("ISBN-908765")
                .setPrice(BigDecimal.valueOf(250))
                .setDescription("Description for Book 3")
                .setCoverImage("image3.jpg"));

        MvcResult mvcResult = mockMvc.perform(get("/categories/{id}/books", VALID_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String jsonResponse = mvcResult.getResponse().getContentAsString();
        List<BookDtoWithoutCategoryIds> actual = objectMapper
                .readValue(
                        jsonResponse, new TypeReference<>() {}
                );
        assertNotNull(actual);
        assertEquals(expected, actual);
    }
}
