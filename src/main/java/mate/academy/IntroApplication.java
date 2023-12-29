package mate.academy;

import java.math.BigDecimal;
import mate.academy.model.Book;
import mate.academy.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class IntroApplication {
    @Autowired
    private BookService bookService;

    public static void main(String[] args) {
        SpringApplication.run(IntroApplication.class, args);
    }

    public CommandLineRunner commandLineRunner() {
        return args -> {
            Book history = new Book();
            history.setTitle("history");
            history.setPrice(BigDecimal.valueOf(99));
            bookService.save(history);
            System.out.println(bookService.findAll());
        };
    }
}
