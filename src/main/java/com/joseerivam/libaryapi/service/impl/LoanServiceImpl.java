package com.joseerivam.libaryapi.service.impl;

import com.joseerivam.libaryapi.exception.BusinessException;
import com.joseerivam.libaryapi.model.entity.Loan;
import com.joseerivam.libaryapi.model.repository.LoanRepository;
import com.joseerivam.libaryapi.service.LoanService;

public class LoanServiceImpl implements LoanService {

  private LoanRepository repository;

  public LoanServiceImpl(LoanRepository repository) {

    this.repository = repository;
  }

  @Override
  public Loan save(Loan loan) {
    if (repository.existsByBookAndNorReturned(loan.getBook())) {
      throw new BusinessException("Book already loaned");
    }
    return repository.save(loan);
  }



}
