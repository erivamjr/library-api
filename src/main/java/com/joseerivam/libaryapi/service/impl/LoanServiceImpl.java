package com.joseerivam.libaryapi.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.joseerivam.libaryapi.api.dto.LoanFilterDTO;
import com.joseerivam.libaryapi.exception.BusinessException;
import com.joseerivam.libaryapi.model.entity.Book;
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
    return repository.findById(id);
  }

  @Override
  public Loan update(Loan loan) {
    // TODO Auto-generated method stub
    return repository.save(loan);
  }

  @Override
  public Page<Loan> find(LoanFilterDTO filterDTO, Pageable pageable) {
    // TODO Auto-generated method stub
    return repository.findByBookIsbnOrCustomer(filterDTO.getIsbn(), filterDTO.getCustomer(),
        pageable);
  }

  @Override
  public Page<Loan> getLoansByBook(Book book, Pageable pageable) {
    // TODO Auto-generated method stub
    return repository.findByBook(book, pageable);
  }

  @Override
  public List<Loan> getAllLateLoans() {
    // TODO Auto-generated method stub
    final Integer loanDays = 4;
    LocalDate threeDaysAgo = LocalDate.now().minusDays(loanDays);
    return repository.findByLoanDateLessThanAndNotReturned(threeDaysAgo);
  }



}
