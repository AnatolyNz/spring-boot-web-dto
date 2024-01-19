package mate.academy.service;

import java.util.List;
import mate.academy.dto.BookDto;
import mate.academy.dto.CreateBookRequestDto;
import org.springframework.stereotype.Service;

@Service
public interface BookService {
    BookDto save(CreateBookRequestDto bookRequestDto);

    BookDto getBookById(Long id);

    void deleteById(Long id);

    void updateById(Long id, CreateBookRequestDto bookWithoutId);

    List<BookDto> findAll();
}
