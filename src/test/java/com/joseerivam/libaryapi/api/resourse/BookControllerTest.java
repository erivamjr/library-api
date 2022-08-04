package com.joseerivam.libaryapi.api.resourse;

import java.util.Optional;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.joseerivam.libaryapi.api.dto.BookDTO;
import com.joseerivam.libaryapi.exception.BusinessException;
import com.joseerivam.libaryapi.model.entity.Book;
import com.joseerivam.libaryapi.service.BookService;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest
@AutoConfigureMockMvc
public class BookControllerTest {

  static String BOOK_API = "/api/books";

  @Autowired
  MockMvc mvc;

  @MockBean
  BookService service;

  @Test
  @DisplayName("Must successfully create a book")
  public void createBookTest() throws Exception {

    BookDTO dto = createNewBook();
    Book savedBook =
        Book.builder().id(10L).author("Arthur").title("As Aventuras").isbn("001").build();

    BDDMockito.given(service.save(Mockito.any(Book.class))).willReturn(savedBook);
    String json = new ObjectMapper().writeValueAsString(dto);

    MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(BOOK_API)
        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(json);

    mvc.perform(request).andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(10L))
        .andExpect(MockMvcResultMatchers.jsonPath("title").value(dto.getTitle()))
        .andExpect(MockMvcResultMatchers.jsonPath("author").value(dto.getAuthor()))
        .andExpect(MockMvcResultMatchers.jsonPath("isbn").value(dto.getIsbn()));
  }


  @Test
  @DisplayName("Should throw validation error when not enough data to create book")
  public void createIvalidBookTest() throws Exception {

    String json = new ObjectMapper().writeValueAsString(new BookDTO());
    MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(BOOK_API)
        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(json);

    mvc.perform(request).andExpect(MockMvcResultMatchers.status().isBadRequest())
        .andExpect(MockMvcResultMatchers.jsonPath("errors", Matchers.hasSize(3)));

  }

  @Test
  @DisplayName("Should throw an error when trying to register an existing book")
  public void createBookWithDuplicateIsbn() throws Exception {

    BookDTO dto = createNewBook();
    String json = new ObjectMapper().writeValueAsString(dto);

    String messageError = "Isbn já cadastrado.";
    BDDMockito.given(service.save(Mockito.any(Book.class)))
        .willThrow(new BusinessException(messageError));

    MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(BOOK_API)
        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(json);

    mvc.perform(request).andExpect(MockMvcResultMatchers.status().isBadRequest())
        .andExpect(MockMvcResultMatchers.jsonPath("errors", Matchers.hasSize(1)))
        .andExpect(MockMvcResultMatchers.jsonPath("errors[0]").value(messageError));
  }

  @Test
  @DisplayName("Must get information from a book")
  public void getBookDetailsTest() throws Exception {
    Long id = 1L;
    Book book = Book.builder().id(id).title(createNewBook().getTitle())
        .author(createNewBook().getAuthor()).isbn(createNewBook().getIsbn()).build();
    BDDMockito.given(service.getById(id)).willReturn(Optional.of(book));

    MockHttpServletRequestBuilder request =
        MockMvcRequestBuilders.get(BOOK_API.concat("/" + id)).accept(MediaType.APPLICATION_JSON);

    mvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(id))
        .andExpect(MockMvcResultMatchers.jsonPath("title").value(createNewBook().getTitle()))
        .andExpect(MockMvcResultMatchers.jsonPath("author").value(createNewBook().getAuthor()))
        .andExpect(MockMvcResultMatchers.jsonPath("isbn").value(createNewBook().getIsbn()));
  }

  @Test
  @DisplayName("Should return resource not found when a book does not exist")
  public void bookNotFoundTest() throws Exception {

    BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.empty());

    MockHttpServletRequestBuilder request =
        MockMvcRequestBuilders.get(BOOK_API.concat("/" + 1)).accept(MediaType.APPLICATION_JSON);

    mvc.perform(request).andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @DisplayName("Must delete a book")
  public void deleteBookTest() throws Exception {

    BDDMockito.given(service.getById(Mockito.anyLong()))
        .willReturn(Optional.of(Book.builder().id(1L).build()));

    MockHttpServletRequestBuilder request =
        MockMvcRequestBuilders.delete(BOOK_API.concat("/" + 1)).accept(MediaType.APPLICATION_JSON);

    mvc.perform(request).andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @DisplayName("Should return resource not found when a book does not exist to delete")
  public void deleteInexistentBookTest() throws Exception {

    BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.empty());

    MockHttpServletRequestBuilder request =
        MockMvcRequestBuilders.delete(BOOK_API.concat("/" + 1)).accept(MediaType.APPLICATION_JSON);

    mvc.perform(request).andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @DisplayName("Should update a book")
  public void updateBookTest() throws Exception {

    Long id = 1L;
    String json = new ObjectMapper().writeValueAsString(createNewBook());
    Book updatingBook =
        Book.builder().id(1L).title("some title").author("some author").isbn("321").build();

    BDDMockito.given(service.getById(id)).willReturn(Optional.of(updatingBook));
    Book updateBook =
        Book.builder().id(1L).author("Arthur").title("As Aventuras").isbn("321").build();
    BDDMockito.given(service.update(updatingBook)).willReturn(updateBook);

    MockHttpServletRequestBuilder request = MockMvcRequestBuilders.put(BOOK_API.concat("/" + 1))
        .content(json).accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON);

    mvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(id))
        .andExpect(MockMvcResultMatchers.jsonPath("title").value(createNewBook().getTitle()))
        .andExpect(MockMvcResultMatchers.jsonPath("author").value(createNewBook().getAuthor()))
        .andExpect(MockMvcResultMatchers.jsonPath("isbn").value("321"));

  }

  @Test
  @DisplayName("Should return 404 when trying to update a book that does not exist")
  public void updateInexistentBookTest() throws Exception {

    String json = new ObjectMapper().writeValueAsString(createNewBook());

    BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.empty());

    MockHttpServletRequestBuilder request = MockMvcRequestBuilders.put(BOOK_API.concat("/" + 1))
        .content(json).accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON);

    mvc.perform(request).andExpect(MockMvcResultMatchers.status().isNotFound());


  }

  private BookDTO createNewBook() {
    return BookDTO.builder().author("Arthur").title("As Aventuras").isbn("001").build();
  }
}
