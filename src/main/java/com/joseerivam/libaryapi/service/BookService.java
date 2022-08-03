package com.joseerivam.libaryapi.service;

import java.util.Optional;
import com.joseerivam.libaryapi.model.entity.Book;

public interface BookService {

  public Book save(Book any);

  Optional<Book> getById(Long id);

  public void delete(Book book);

}
