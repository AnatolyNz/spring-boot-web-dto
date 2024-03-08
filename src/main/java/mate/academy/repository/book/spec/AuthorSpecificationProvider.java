package mate.academy.repository.book.spec;

import java.util.Arrays;
import mate.academy.model.Book;
import mate.academy.repository.SpecificationProvider;
import mate.academy.repository.book.BookSpecificationBuilder;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class AuthorSpecificationProvider implements SpecificationProvider<Book> {

    @Override
    public String getKey() {
        return BookSpecificationBuilder.AUTHOR_OUT_PARAM;
    }

    public Specification<Book> getSpecification(String[] params) {
        return (root, query, criteriaBuilder) -> root
                .get(BookSpecificationBuilder.AUTHOR_OUT_PARAM)
                .in(Arrays.stream(params).toArray());
    }
}
