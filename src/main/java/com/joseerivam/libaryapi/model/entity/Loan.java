package com.joseerivam.libaryapi.model.entity;

import java.time.LocalDate;
import com.joseerivam.libaryapi.api.dto.LoanDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Loan {

  private Long id;
  private String customer;
  private Book book;
  private LocalDate loanDate;
  private Boolean retorned;

}
