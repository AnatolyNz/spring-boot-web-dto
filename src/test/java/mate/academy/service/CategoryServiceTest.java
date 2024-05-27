package mate.academy.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import mate.academy.dto.CategoryDto;
import mate.academy.dto.CategoryResponseDto;
import mate.academy.exception.EntityNotFoundException;
import mate.academy.mapper.CategoryMapper;
import mate.academy.model.Category;
import mate.academy.repository.CategoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {
    @Mock
    private CategoryMapper categoryMapper;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    @DisplayName("""
       Verify search() for all books
            """)
    public void testFindAll_ValidSearchParameters_ShouldReturnListOfCategories() {
        Category category1 = new Category();
        category1.setId(1L);
        category1.setName("Mystery");
        Category category2 = new Category();
        category2.setId(2L);
        category2.setName("Science fiction");

        List<Category> categories = Arrays.asList(category1, category2);
        Pageable pageable = PageRequest.of(0, 10);
        PageImpl<Category> page = new PageImpl<>(categories, pageable, categories.size());

        // Mock repository method
        when(categoryRepository.findAll(pageable)).thenReturn(page);

        // Mock mapper method
        CategoryResponseDto categoryResponseDto1 = new CategoryResponseDto();
        categoryResponseDto1.setId(1L);
        categoryResponseDto1.setName("Mystery");
        CategoryResponseDto categoryResponseDto2 = new CategoryResponseDto();
        categoryResponseDto2.setId(2L);
        categoryResponseDto2.setName("Science fiction");
        when(categoryMapper.toDto(category1)).thenReturn(categoryResponseDto1);
        when(categoryMapper.toDto(category2)).thenReturn(categoryResponseDto2);

        // Call the service method
        List<CategoryResponseDto> result = categoryService.findAll(pageable);

        // Verify the result
        assertEquals(2, result.size());
        assertEquals("Mystery", result.get(0).getName());
        assertEquals("Science fiction", result.get(1).getName());
    }

    @Test
    @DisplayName("""
       Verify the correct category
            """)
    public void getCategoryById_WithValidCategoryId_ShouldReturnValidCategory() {
        Long id = 1L;
        Category category = new Category();
        category.setId(id);
        when(categoryRepository.findById(id)).thenReturn(Optional.of(category));

        CategoryResponseDto expected = new CategoryResponseDto();
        expected.setId(id);
        when(categoryMapper.toDto(category)).thenReturn(expected);

        CategoryResponseDto actual = categoryService.getById(id);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("""
       Verify the correct category was not found     
            """)
    public void getCategoryById_WithNonExistingCategoryId_ShouldThrowException() {
        Long id = 100L;
        when(categoryRepository.findById(id)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            categoryService.getById(id);
        });

        String expected = "Can't find category with id " + id;
        String actual = exception.getMessage();
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("""
       Verify save and return for the correct category     
            """)
    void saveCategory_WithValidCategoryId_ShouldReturnValidCategory() {
        // Mock data
        CategoryDto categoryDto = new CategoryDto();
        Category category = new Category();
        Category savedCategory = new Category();
        CategoryResponseDto expectedDto = new CategoryResponseDto();

        // Mock mapper behavior
        when(categoryMapper.toEntity(categoryDto)).thenReturn(category);
        when(categoryMapper.toDto(savedCategory)).thenReturn(expectedDto);

        // Mock repository behavior
        when(categoryRepository.save(category)).thenReturn(savedCategory);

        // Test
        CategoryResponseDto actualDto = categoryService.save(categoryDto);

        // Assertions
        assertEquals(expectedDto, actualDto);
    }

    @Test
    @DisplayName("""
       Verify update() updated categories with valid ID
            """)
    public void testUpdateById_WithValidCategoryId_ShouldUpdateBook() {
        // Mock data
        Long categoryId = 1L;
        CategoryDto updatedCategoryDto = new CategoryDto();
        Category existingCategory =
                new Category(); // Assuming Category is your domain model
        Category updatedCategory = new Category(); // Assuming Category is your domain model
        CategoryResponseDto expectedDto =
                new CategoryResponseDto(); // Assuming CategoryResponseDto is your DTO model

        // Mock repository behavior
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.save(existingCategory)).thenReturn(updatedCategory);

        // Mock mapper behavior
        when(categoryMapper.toDto(updatedCategory)).thenReturn(expectedDto);

        // Test
        CategoryResponseDto actualDto = categoryService.update(categoryId, updatedCategoryDto);

        // Assertions
        assertEquals(expectedDto, actualDto);
        assertEquals(updatedCategoryDto.getName(), existingCategory.getName());
        assertEquals(updatedCategoryDto.getDescription(), existingCategory.getDescription());
    }

    @Test
    @DisplayName("""
       Verify update() updated categories with valid ID
            """)
    public void testUpdateById_WithNotValidCategoryId_ShouldThrowException() {
        // Mock data
        Long categoryId = 1L;
        CategoryDto updatedCategoryDto = new CategoryDto();

        // Mock repository behavior
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // Test and assertions
        assertThrows(EntityNotFoundException.class, () -> categoryService
                .update(categoryId, updatedCategoryDto));
    }

    @Test
    @DisplayName("""
       Verify deleteById() calls repository with valid ID
            """)
    public void testDeleteById_WithValidCategoryId_ShouldDeleteCategory() {
        // Given
        Long id = 1L;

        // When
        categoryService.deleteById(id);

        // Then
        verify(categoryRepository, times(1)).deleteById(id);
    }
}
