package com.joseerivam.libaryapi.api.resourse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.joseerivam.libaryapi.api.dto.BookDTO;
import com.joseerivam.libaryapi.api.resource.BookController;
import com.joseerivam.libaryapi.exception.BusinessException;
import com.joseerivam.libaryapi.model.entity.Book;
import com.joseerivam.libaryapi.service.BookService;
import com.joseerivam.libaryapi.service.LoanService;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;
import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = BookController.class)
@AutoConfigureMockMvc
public class BookControllerTest {

    static String BOOK_API = "/api/books";

    @Autowired
    MockMvc mvc;

    @MockBean
    BookService service;

    @MockBean
    LoanService loanService;

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
                .andExpect(jsonPath("id").value(10L))
                .andExpect(jsonPath("title").value(dto.getTitle()))
                .andExpect(jsonPath("author").value(dto.getAuthor()))
                .andExpect(jsonPath("isbn").value(dto.getIsbn()));
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
                .andExpect(jsonPath("errors", Matchers.hasSize(1)))
                .andExpect(jsonPath("errors[0]").value(messageError));
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
                .andExpect(jsonPath("id").value(id))
                .andExpect(jsonPath("title").value(createNewBook().getTitle()))
                .andExpect(jsonPath("author").value(createNewBook().getAuthor()))
                .andExpect(jsonPath("isbn").value(createNewBook().getIsbn()));
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
                .andExpect(jsonPath("id").value(id))
                .andExpect(jsonPath("title").value(createNewBook().getTitle()))
                .andExpect(jsonPath("author").value(createNewBook().getAuthor()))
                .andExpect(jsonPath("isbn").value("321"));

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

    @Test
    @DisplayName("should to filter the book")
    public void findBookTest() throws Exception {

        Long id = 1L;
        Book book = Book.builder().id(id).title(createNewBook().getTitle())
                .author(createNewBook().getAuthor()).isbn(createNewBook().getAuthor()).build();

        BDDMockito.given(service.find(Mockito.any(Book.class), Mockito.any(Pageable.class)))
                .willReturn(new PageImpl<Book>(Arrays.asList(book), PageRequest.of(0, 100), 1));

        String queryString =
                String.format("?title=%s&author=%spage=0&size=100", book.getTitle(), book.getAuthor());

        MockHttpServletRequestBuilder request =
                MockMvcRequestBuilders.get(BOOK_API.concat(queryString)).accept(MediaType.APPLICATION_JSON);

        mvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("content", Matchers.hasSize(1)))
                .andExpect(jsonPath("totalElements").value(1))
                .andExpect(jsonPath("pageable.pageSize").value(100))
                .andExpect(jsonPath("pageable.pageNumber").value(0));
    }

    private BookDTO createNewBook() {
        return BookDTO.builder().author("Arthur").title("As Aventuras").isbn("001").build();
    }
}
