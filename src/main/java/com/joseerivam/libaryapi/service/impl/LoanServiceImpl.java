package com.joseerivam.libaryapi.service.impl;

import java.util.Optional;
import org.springframework.stereotype.Service;
import com.joseerivam.libaryapi.exception.BusinessException;
import com.joseerivam.libaryapi.model.entity.Loan;
import com.joseerivam.libaryapi.model.repository.LoanRepository;
import com.joseerivam.libaryapi.service.LoanService;

@Service
public class LoanServiceImpl implements LoanService {

  private LoanRepository repository;

  public LoanServiceImpl(LoanRepository repository) {

    this.repository = repository;
  }

  @Override
  public Loan save(Loan loan) {
    if (repository.existsByBookAndNotReturned(loan.getBook())) {
      throw new BusinessException("Book already loaned");
    }
    return repository.save(loan);
  }

  @Override
  public Optional<Loan> getById(Long id) {
    // TODO Auto-generated method stub
    return Optional.empty();
  }

  @Override
  public Loan update(Loan loan) {
    // TODO Auto-generated method stub
    return null;
  }



}
