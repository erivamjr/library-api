package com.joseerivam.libaryapi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.mockitoSession;
import java.time.LocalDate;
import java.util.Optional;
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

    Mockito.when(repository.existsByBookAndNotReturned(book)).thenReturn(false);
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

    Mockito.when(repository.existsByBookAndNotReturned(book)).thenReturn(true);


    Throwable exception = catchThrowable(() -> service.save(savingLoan));

    assertThat(exception).isInstanceOf(BusinessException.class).hasMessage("Book already loaned");

    Mockito.verify(repository, Mockito.never()).save(savingLoan);

  }

  @Test
  @DisplayName("Should get information about a loan by ID")
  public void getLoanDetailsTest() {

    Long id = 1L;

    Loan loan = createLoan();
    loan.setId(id);

    Mockito.when(repository.findById(id)).thenReturn(Optional.of(loan));

    Optional<Loan> result = service.getById(id);

    assertThat(result.isPresent()).isTrue();
    assertThat(result.get().getId()).isEqualTo(id);
    assertThat(result.get().getCustomer()).isEqualTo(loan.getCustomer());
    assertThat(result.get().getBook()).isEqualTo(loan.getBook());
    assertThat(result.get().getLoanDate()).isEqualTo(loan.getLoanDate());

    Mockito.verify(repository).findById(id);

  }

  @Test
  @DisplayName("Should")
  public void updateLoanTest() {
    Loan loan = createLoan();
    loan.setId(1L);
    loan.setReturned(true);

    Mockito.when(repository.save(loan)).thenReturn(loan);

    Loan updatedLoan = service.update(loan);

    assertThat(updatedLoan.getReturned()).isTrue();
    Mockito.verify(repository).save(loan);
  }

  public static Loan createLoan() {

    Book book = Book.builder().id(1L).build();
    String customer = "Fulano";

    return Loan.builder().book(book).customer(customer).loanDate(LocalDate.now()).build();

  }

}
