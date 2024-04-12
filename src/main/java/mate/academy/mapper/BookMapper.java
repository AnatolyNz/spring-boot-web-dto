package mate.academy.mapper;

import mate.academy.config.MapperConfig;
import mate.academy.dto.BookDto;
import mate.academy.dto.BookDtoWithoutCategoryIds;
import mate.academy.dto.CreateBookRequestDto;
import mate.academy.model.Book;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface BookMapper {

    BookDto toDto(Book book);

    Book toModel(CreateBookRequestDto requestDto);

    Book toEntity(CreateBookRequestDto bookDto);

    BookDtoWithoutCategoryIds toDtoWithoutCategories(Book book);

    @AfterMapping
    default void setCategoryIds(@MappingTarget BookDto bookDto, Book book) {
    }
}
