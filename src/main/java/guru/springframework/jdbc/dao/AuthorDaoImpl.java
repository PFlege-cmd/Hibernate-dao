package guru.springframework.jdbc.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import guru.springframework.jdbc.domain.Author;
import org.springframework.stereotype.Component;

/**
 * Created by jt on 8/28/21.
 */
@Component
public class AuthorDaoImpl implements AuthorDao {

    private final EntityManagerFactory emf;

    public AuthorDaoImpl(EntityManagerFactory entityManagerFactory){
        this.emf = entityManagerFactory;
    }

    @Override public List<Author> findAll() {
        EntityManager em = getEntityManager();

        try {
            TypedQuery<Author> query = em.createNamedQuery("find_all_authors", Author.class);
            return query.getResultList();
        } finally {
            em.close();
        }

    }

    @Override
    public Author getById(Long id) {
        return getEntityManager().find(Author.class, id);
    }

    @Override public List<Author> findAuthorListByLastNameLike(String lastName) {

        EntityManager em = getEntityManager();

        try {
            em.joinTransaction();
            Query query = em.createQuery("SELECT a from Author a where a.lastName like :last_name");
            query.setParameter("last_name", lastName + "%");
            return query.getResultList();

        } finally {
            em.close();
        }
    }

    @Override
    public Author findAuthorByName(String firstName, String lastName) {

        EntityManager em = getEntityManager();

        TypedQuery<Author> typedQuery = em.createNamedQuery("find_author_by_name", Author.class);
        typedQuery.setParameter("first_name", firstName);
        typedQuery.setParameter("last_name", lastName);

        Author author = typedQuery.getSingleResult();

        em.close();
        return author;
    }

    @Override
    public Author saveNewAuthor(Author author) {
        EntityManager em = getEntityManager();

        em.joinTransaction();
        em.persist(author);
        em.flush();
        em.getTransaction().commit();
        em.close();

        return author;
    }

    @Override
    public Author updateAuthor(Author author) {
        EntityManager em = getEntityManager();
        em.joinTransaction();
        em.merge(author);
        em.flush();
        em.clear();
        var auth = em.find(Author.class, author.getId());
        em.close();

        return auth;
    }

    @Override
    public void deleteAuthorById(Long id) {
        EntityManager em = getEntityManager();
        em.joinTransaction();
        Author authorToDelete = em.find(Author.class, id);
        em.remove(authorToDelete);
        em.flush();
        em.getTransaction().commit();
        em.close();
    }

    private EntityManager getEntityManager(){
        return this.emf.createEntityManager();
    }
}
