package com.joseerivam.libaryapi;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.joseerivam.libaryapi.api.dto.BookDTO;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest
@AutoConfigureMockMvc
public class BookControllerTest {

  static String BOOK_API = "/api/books";

  @Autowired
  MockMvc mvc;

  @Test
  @DisplayName("Must successfully create a book")
  public void createBookTest() throws Exception {
    BookDTO dto = BookDTO.builder().author("Arthur").title("As Aventuras").isbn("001").build();
    String json = new ObjectMapper().writeValueAsString(dto);

    MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(BOOK_API)
        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(json);

    mvc.perform(request).andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("title").value(dto.getTitle()))
        .andExpect(MockMvcResultMatchers.jsonPath("author").value(dto.getAuthor()))
        .andExpect(MockMvcResultMatchers.jsonPath("isbn").value(dto.getIsbn()));
  }

  @Test
  @DisplayName("should throw validation error when not enough data to create book")
  public void createIvalidBookTest() {

  }
}
