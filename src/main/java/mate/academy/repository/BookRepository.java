package mate.academy.repository;

import mate.academy.model.Book;
import java.util.List;
import org.springframework.stereotype.Repository;


@Repository
public interface BookRepository {
    Book save(Book book);

    List<Book> findAll();
}
