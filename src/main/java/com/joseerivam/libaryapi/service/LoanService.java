package com.joseerivam.libaryapi.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.joseerivam.libaryapi.api.dto.LoanFilterDTO;
import com.joseerivam.libaryapi.model.entity.Loan;

public interface LoanService {

  Loan save(Loan loan);

  Optional<Loan> getById(Long id);

  Loan update(Loan loan);

  Page<Loan> find(LoanFilterDTO filterDTO, Pageable pageable);

}
