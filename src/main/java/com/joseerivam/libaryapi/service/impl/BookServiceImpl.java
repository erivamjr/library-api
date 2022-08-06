package com.joseerivam.libaryapi.service.impl;

import java.util.Optional;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.joseerivam.libaryapi.exception.BusinessException;
import com.joseerivam.libaryapi.model.entity.Book;
import com.joseerivam.libaryapi.model.repository.BookRepository;
import com.joseerivam.libaryapi.service.BookService;
import net.bytebuddy.matcher.StringMatcher;

@Service
public class BookServiceImpl implements BookService {

  private BookRepository repository;

  public BookServiceImpl(BookRepository repository) {
    this.repository = repository;
  }


  @Override
  public Book save(Book book) {
    if (repository.existsByIsbn(book.getIsbn())) {
      throw new BusinessException("Isbn já cadastrado.");
    }
    return repository.save(book);
  }


  @Override
  public Optional<Book> getById(Long id) {
    return this.repository.findById(id);
  }


  @Override
  public void delete(Book book) {
    if (book == null || book.getId() == null) {
      throw new IllegalArgumentException("Book id can be null");
    }
    this.repository.delete(book);
  }


  @Override
  public Book update(Book book) {
    if (book == null || book.getId() == null) {
      throw new IllegalArgumentException("Book id can be null");
    }
    return this.repository.save(book);
  }


  @Override
  public Page<Book> find(Book filter, Pageable pageRequest) {
    Example<Book> example = Example.of(filter, ExampleMatcher.matching().withIgnoreCase()
        .withIgnoreNullValues().withStringMatcher(ExampleMatcher.StringMatcher.STARTING));
    // TODO Auto-generated method stub
    return repository.findAll(example, pageRequest);
  }


  @Override
  public Optional<Book> getBookByIsbn(String isbn) {
    // TODO Auto-generated method stub
    return null;
  }

}
