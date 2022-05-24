package guru.springframework.jdbc.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Component;

import guru.springframework.jdbc.domain.Author;
import guru.springframework.jdbc.domain.Book;

@Component
public class BookDaoImpl implements BookDao{

    private final EntityManagerFactory emf;

    public BookDaoImpl(EntityManagerFactory emf){
        this.emf = emf;
    }

    @Override public List<Book> findAll(){

        EntityManager em = getEntityManager();

        try {
            TypedQuery<Book> typedQuery = em.createNamedQuery("find_all_books", Book.class);
            List<Book> bookList = typedQuery.getResultList();
            return bookList;
        } finally {
            em.close();
        }
    }

    @Override public Book findBookByISBN(String isbn) {
        EntityManager em = getEntityManager();

        try {
            em.joinTransaction();
            TypedQuery<Book> typedQuery = em.createQuery("SELECT b from Book b where b.isbn = :isbn", Book.class);
            typedQuery.setParameter("isbn", isbn);
            Book book = typedQuery.getSingleResult();
            return book;

        } finally {
            em.close();
        }
    }

    @Override public Book getById(Long id) {
        EntityManager em = getEntityManager();

        Book book = em.find(Book.class, id);
        em.close();
        return book;
    }

    @Override public Book findBookByTitle(String title) {
        EntityManager em = getEntityManager();

        TypedQuery<Book> query = em.createNamedQuery("find_book_by_title", Book.class);
        query.setParameter("taitoru", title);
        Book book = query.getSingleResult();
        em.close();

        return book;
    }

    @Override public Book saveNewBook(Book book) {

        EntityManager em = getEntityManager();
        if (!em.isJoinedToTransaction())
            em.joinTransaction();

        em.persist(book);
        em.flush();
        em.getTransaction().commit();
        em.close();

        return book;
    }

    @Override public void updateBook(Book book) {
        EntityManager em = getEntityManager();
        if (!em.isJoinedToTransaction())
            em.joinTransaction();

        // Cleans out the cache, to make sure that EntityManager will
        // actually search for the instance in the database, not in memory
        em.merge(book);
        em.flush();
        em.clear();
        em.getTransaction().commit(); // Necessary. Currently dirty state (not committed to DB). This will persist the new state.
        em.close();
    }

    @Override public void deleteBookById(Long id) {
        EntityManager em = getEntityManager();
        if (!em.isJoinedToTransaction())
            em.joinTransaction();

        Book bookToDelete = em.find(Book.class, id);
        em.remove(bookToDelete);
        em.flush();
        em.getTransaction().commit();
        em.close();
    }

    private EntityManager getEntityManager(){
        return this.emf.createEntityManager();
    }
}
