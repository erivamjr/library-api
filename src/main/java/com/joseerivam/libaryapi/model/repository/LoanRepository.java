package com.joseerivam.libaryapi.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.joseerivam.libaryapi.model.entity.Book;
import com.joseerivam.libaryapi.model.entity.Loan;

public interface LoanRepository extends JpaRepository<Loan, Long> {

  boolean existsByBookAndNorReturned(Book book);

}
