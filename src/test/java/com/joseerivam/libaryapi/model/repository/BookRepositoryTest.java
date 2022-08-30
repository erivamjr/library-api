package com.joseerivam.libaryapi.model.repository;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import com.joseerivam.libaryapi.model.entity.Book;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class BookRepositoryTest {

  @Autowired
  TestEntityManager entityManager;

  @Autowired
  BookRepository repository;

  @Test
  @DisplayName("must return true when there is a book in the base with isbn already informed")
  public void returnTrueWhenIsbnExists() {

    String isbn = "123";
    Book book = createNewBook(isbn);
    entityManager.persist(book);

    boolean exists = repository.existsByIsbn(isbn);

    assertThat(exists).isTrue();
  }

  public static Book createNewBook(String isbn) {
    return Book.builder().title("As Aventuras").author("Fulano").isbn(isbn).build();
  }

  @Test
  @DisplayName("should return false when there is no book in the base with isbn already informed")
  public void returnFalseWhenIsbnDoenstExists() {

    String isbn = "123";

    boolean exists = repository.existsByIsbn(isbn);

    assertThat(exists).isFalse();
  }

  @Test
  @DisplayName("should get one book per id")
  public void findByIdTest() {

    Book book = createNewBook("123");
    entityManager.persist(book);

    Optional<Book> foundBook = repository.findById(book.getId());

    assertThat(foundBook.isPresent()).isTrue();
  }

  @Test
  @DisplayName("should to save a book")
  public void saveBookTest() {

    Book book = createNewBook("123");

    Book savedBook = repository.save(book);

    assertThat(savedBook.getId()).isNotNull();
  }

  @Test
  @DisplayName("should to delete a book")
  public void deleteBookTest() {

    Book book = createNewBook("123");

    entityManager.persist(book);

    Book foundBook = entityManager.find(Book.class, book.getId());

    repository.delete(foundBook);

    Book deletedBook = entityManager.find(Book.class, book.getId());
    assertThat(deletedBook).isNull();
  }



}
