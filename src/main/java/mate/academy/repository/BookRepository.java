package mate.academy.repository;

import java.util.List;
import java.util.Optional;
import mate.academy.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    Book save(Book book);

    Optional<Book> getBookById(Long id);

    List<Book> findAll();
}

