package com.joseerivam.libaryapi.api.resource;


import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.joseerivam.libaryapi.api.dto.BookDTO;
import com.joseerivam.libaryapi.model.Book;
import com.joseerivam.libaryapi.service.BookService;

@RestController
@RequestMapping("/api/books")
public class BookController {

  private BookService service;
  private ModelMapper modelMapper;

  public BookController(BookService service, ModelMapper mapper) {
    this.service = service;
    this.modelMapper = mapper;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public BookDTO create(@RequestBody BookDTO dto) {
    Book entity = modelMapper.map(dto, Book.class);
    entity = service.save(entity);

    return modelMapper.map(entity, BookDTO.class);
  }

}
