package com.joseerivam.libaryapi.api.exception;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;
import com.joseerivam.libaryapi.exception.BusinessException;

public class ApiErrors {

  private List<String> errors;

  public ApiErrors(BindingResult bindingResult) {
    this.errors = new ArrayList<>();
    bindingResult.getAllErrors().forEach(error -> this.errors.add(error.getDefaultMessage()));
  }

  public ApiErrors(BusinessException ex) {
    this.errors = Arrays.asList(ex.getMessage());
  }

  public ApiErrors(ResponseStatusException ex) {
    this.errors = Arrays.asList(ex.getReason());
  }

  public List<String> getErrors() {
    return errors;
  }

}
