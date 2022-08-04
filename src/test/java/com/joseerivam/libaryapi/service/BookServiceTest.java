package com.joseerivam.libaryapi.service;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import com.joseerivam.libaryapi.exception.BusinessException;
import com.joseerivam.libaryapi.model.entity.Book;
import com.joseerivam.libaryapi.model.repository.BookRepository;
import com.joseerivam.libaryapi.service.impl.BookServiceImpl;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceTest {

  BookService service;

  @MockBean
  BookRepository repository;

  @BeforeEach
  public void setUp() {
    this.service = new BookServiceImpl(repository);
  }

  @Test
  @DisplayName("Dust return a book")
  public void saveBookTest() {

    Book book = createValidBook();
    Mockito.when(repository.save(book)).thenReturn(
        Book.builder().id(1L).isbn("123").author("Erivam").title("As Aventuras").build());

    Book savedBook = service.save(book);

    assertThat(savedBook.getId()).isNotNull();
    assertThat(savedBook.getIsbn()).isEqualTo("123");
    assertThat(savedBook.getTitle()).isEqualTo("As Aventuras");
    assertThat(savedBook.getAuthor()).isEqualTo("Erivam");
  }

  private Book createValidBook() {
    return Book.builder().isbn("123").author("Erivam").title("As Aventuras").build();
  }

  @Test
  @DisplayName("Should throw a business error when saving a duplicate book")
  public void shouldNotSaveBookWithDuplicatedISBN() {

    Book book = createValidBook();
    Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(true);

    Throwable exception = Assertions.catchThrowable(() -> service.save(book));
    assertThat(exception).isInstanceOf(BusinessException.class).hasMessage("Isbn já cadastrado.");

    Mockito.verify(repository, Mockito.never()).save(book);
  }

  @Test
  @DisplayName("Should return one book by id")
  public void getByIdTest() {
    Long id = 1L;

    Book book = createValidBook();
    book.setId(id);
    Mockito.when(repository.findById(id)).thenReturn(Optional.of(book));

    Optional<Book> foundBook = service.getById(id);

    assertThat(foundBook.isPresent()).isTrue();
    assertThat(foundBook.get().getId()).isEqualTo(id);
    assertThat(foundBook.get().getAuthor()).isEqualTo(book.getAuthor());
    assertThat(foundBook.get().getIsbn()).isEqualTo(book.getIsbn());
    assertThat(foundBook.get().getTitle()).isEqualTo(book.getTitle());

  }

  @Test
  @DisplayName("Should return empty when id does not exist")
  public void getBookNotFoundByIdTest() {
    Long id = 1L;


    Mockito.when(repository.findById(id)).thenReturn(Optional.empty());

    Optional<Book> book = service.getById(id);

    assertThat(book.isPresent()).isFalse();

  }


}
