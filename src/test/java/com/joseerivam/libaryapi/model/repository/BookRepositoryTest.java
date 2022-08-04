package com.joseerivam.libaryapi.model.repository;

import static org.assertj.core.api.Assertions.assertThat;
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
    Book book = Book.builder().title("As Aventuras").author("Fulano").isbn(isbn).build();
    entityManager.persist(book);

    boolean exists = repository.existsByIsbn(isbn);

    assertThat(exists).isTrue();
  }

  @Test
  @DisplayName("should return false when there is no book in the base with isbn already informed")
  public void returnFalseWhenIsbnDoenstExists() {

    String isbn = "123";

    boolean exists = repository.existsByIsbn(isbn);

    assertThat(exists).isFalse();
  }


}
