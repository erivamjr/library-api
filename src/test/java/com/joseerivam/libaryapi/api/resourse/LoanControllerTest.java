package com.joseerivam.libaryapi.api.resourse;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.time.LocalDate;
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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.joseerivam.libaryapi.api.dto.LoanDTO;
import com.joseerivam.libaryapi.api.dto.ReturnedLoanDTO;
import com.joseerivam.libaryapi.api.resource.LoanController;
import com.joseerivam.libaryapi.exception.BusinessException;
import com.joseerivam.libaryapi.model.entity.Book;
import com.joseerivam.libaryapi.model.entity.Loan;
import com.joseerivam.libaryapi.service.BookService;
import com.joseerivam.libaryapi.service.LoanService;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = LoanController.class)
@AutoConfigureMockMvc
public class LoanControllerTest {

  static final String LOAN_API = "/api/loans";

  @Autowired
  MockMvc mvc;

  @MockBean
  private BookService bookService;

  @MockBean
  private LoanService loanService;

  @Test
  @DisplayName("Must take out a loan")
  public void createLoanTest() throws Exception {

    LoanDTO dto = LoanDTO.builder().isbn("123").customer("Fulano").build();

    String json = new ObjectMapper().writeValueAsString(dto);

    Book book = Book.builder().id(1L).isbn("123").build();
    BDDMockito.given(bookService.getBookByIsbn("123")).willReturn(Optional.of(book));

    Loan loan =
        Loan.builder().id(1L).customer("Fulano").book(book).loanDate(LocalDate.now()).build();
    BDDMockito.given(loanService.save(Mockito.any(Loan.class))).willReturn(loan);

    MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(LOAN_API)
        .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).content(json);

    mvc.perform(request).andExpect(status().isCreated()).andExpect(content().string("1"));
  }

  @Test
  @DisplayName("Should return an error when trying to return a non-existent book")
  public void invalidIsbnCreateLoanTest() throws Exception {

    LoanDTO dto = LoanDTO.builder().isbn("123").customer("Fulano").build();
    String json = new ObjectMapper().writeValueAsString(dto);

    BDDMockito.given(bookService.getBookByIsbn("123")).willReturn(Optional.empty());

    MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(LOAN_API)
        .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).content(json);

    mvc.perform(request).andExpect(status().isBadRequest())
        .andExpect(jsonPath("errors", Matchers.hasSize(1)))
        .andExpect(jsonPath("errors[0]").value("Book not found for passed isbn"));
  }

  @Test
  @DisplayName("Should return an error when trying to return a loaned book")
  public void LoanedBookErrorOnCreateLoanTest() throws Exception {

    LoanDTO dto = LoanDTO.builder().isbn("123").customer("Fulano").build();
    String json = new ObjectMapper().writeValueAsString(dto);

    Book book = Book.builder().id(1L).isbn("123").build();
    BDDMockito.given(bookService.getBookByIsbn("123")).willReturn(Optional.of(book));

    BDDMockito.given(loanService.save(Mockito.any(Loan.class)))
        .willThrow(new BusinessException("Book already loaned"));

    MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(LOAN_API)
        .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).content(json);

    mvc.perform(request).andExpect(status().isBadRequest())
        .andExpect(jsonPath("errors", Matchers.hasSize(1)))
        .andExpect(jsonPath("errors[0]").value("Book already loaned"));
  }

  @Test
  @DisplayName("Should return a book")
  public void returnBookTest() throws Exception {
    ReturnedLoanDTO dto = ReturnedLoanDTO.builder().returned(true).build();
    Loan loan = Loan.builder().id(1L).build();
    BDDMockito.given(loanService.getById(Mockito.anyLong())).willReturn(Optional.of(loan));

    String json = new ObjectMapper().writeValueAsString(dto);

    mvc.perform(patch(LOAN_API.concat("/1")).accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isOk());

    Mockito.verify(loanService, Mockito.times(1)).update(loan);
  }

}
