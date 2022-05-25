package guru.springframework.jdbc.dao;

import java.util.List;

import guru.springframework.jdbc.domain.Book;

public interface BookDao {
    List<Book> findAll();
    Book findBookByISBN(String isbn);
    Book getById(Long id);
    Book findBookByTitle(String title);
    Book findBookByTitleCriteria(String title);
    Book saveNewBook(Book book);
    void updateBook(Book book);
    void deleteBookById(Long id);
}
