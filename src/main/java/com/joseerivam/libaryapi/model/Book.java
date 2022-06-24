package com.joseerivam.libaryapi.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class Book {

  private Long id;
  private String title;
  private String author;
  private String isbn;

}
