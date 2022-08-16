package com.joseerivam.libaryapi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import com.joseerivam.libaryapi.exception.BusinessException;
import com.joseerivam.libaryapi.model.entity.Book;
import com.joseerivam.libaryapi.model.entity.Loan;
import com.joseerivam.libaryapi.model.repository.LoanRepository;
import com.joseerivam.libaryapi.service.impl.LoanServiceImpl;

@ExtendWith(SpringExtension.class)
public class LoanServiceTest {

  LoanService service;

  @MockBean
  LoanRepository repository;

  @BeforeEach
  public void setUp() {
    this.service = new LoanServiceImpl(repository);
  }

  @Test
  @DisplayName("Should save a loan")
  public void saveLoan() {

    Book book = Book.builder().id(1L).build();
    String customer = "Fulano";
    Loan savingLoan =
        Loan.builder().book(book).customer(customer).loanDate(LocalDate.now()).build();

    Loan savedLoan =
        Loan.builder().id(1L).loanDate(LocalDate.now()).customer(customer).book(book).build();

    Mockito.when(repository.existsByBookAndNorReturned(book)).thenReturn(false);
    Mockito.when(repository.save(savingLoan)).thenReturn(savedLoan);

    Loan loan = service.save(savingLoan);

    assertThat(loan.getId()).isEqualTo(savedLoan.getId());
    assertThat(loan.getBook().getId()).isEqualTo(savedLoan.getBook().getId());
    assertThat(loan.getCustomer()).isEqualTo(savedLoan.getCustomer());
    assertThat(loan.getLoanDate()).isEqualTo(savedLoan.getLoanDate());
  }

  @Test
  @DisplayName("Should throw a business error when saving a loan with a book already saved")
  public void loanedBookSaveTest() {

    Book book = Book.builder().id(1L).build();
    String customer = "Fulano";
    Loan savingLoan =
        Loan.builder().book(book).customer(customer).loanDate(LocalDate.now()).build();

    Mockito.when(repository.existsByBookAndNorReturned(book)).thenReturn(true);


    Throwable extension = catchThrowable(() -> service.save(savingLoan));

    assertThat(extension).isInstanceOf(BusinessException.class).hasMessage("Book already loaned");

    Mockito.verify(repository, Mockito.never()).save(savingLoan);

  }

}
