package com.joseerivam.libaryapi.service;

import java.util.Optional;
import com.joseerivam.libaryapi.model.entity.Loan;

public interface LoanService {

  Loan save(Loan loan);

  Optional<Loan> getById(Long id);

  Loan update(Loan loan);

}
