package com.joseerivam.libaryapi.service.impl;

import java.util.Optional;
import org.springframework.stereotype.Service;
import com.joseerivam.libaryapi.exception.BusinessException;
import com.joseerivam.libaryapi.model.entity.Book;
import com.joseerivam.libaryapi.model.repository.BookRepository;
import com.joseerivam.libaryapi.service.BookService;

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

}
