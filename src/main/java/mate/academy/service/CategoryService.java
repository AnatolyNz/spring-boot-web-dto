package mate.academy.service;

import java.util.List;
import mate.academy.dto.CategoryDto;
import mate.academy.dto.CategoryResponseDto;
import org.springframework.data.domain.Pageable;

public interface CategoryService {

    List findAll(Pageable pageable);

    CategoryResponseDto getById(Long id);

    CategoryResponseDto save(CategoryDto categoryDto);

    CategoryResponseDto update(Long id, CategoryDto categoryDto);

    void deleteById(Long id);
}
