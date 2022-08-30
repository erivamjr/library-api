package com.joseerivam.libaryapi.model.repository;

import static com.joseerivam.libaryapi.model.repository.BookRepositoryTest.createNewBook;
import static org.assertj.core.api.Assertions.assertThat;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import com.joseerivam.libaryapi.model.entity.Book;
import com.joseerivam.libaryapi.model.entity.Loan;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class LoanRepositoryTest {

  @Autowired
  private LoanRepository repository;

  @Autowired
  private TestEntityManager entityManager;

  @Test
  @DisplayName("Should check if there is unreturned loan for the book")
  public void existsByBookAndNotReturnedTest() {


    Book book = createNewBook("123");
    entityManager.persist(book);

    Loan loan = Loan.builder().book(book).customer("Fulano").loanDate(LocalDate.now()).build();
    entityManager.persist(loan);

    boolean exists = repository.existsByBookAndNotReturned(book);

    assertThat(exists).isTrue();
  }

}
