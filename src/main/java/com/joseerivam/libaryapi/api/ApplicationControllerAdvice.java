package com.joseerivam.libaryapi.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import com.joseerivam.libaryapi.api.exception.ApiErrors;
import com.joseerivam.libaryapi.exception.BusinessException;

@RestControllerAdvice
public class ApplicationControllerAdvice {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ApiErrors handleValidationException(MethodArgumentNotValidException ex) {
    BindingResult bindingResult = ex.getBindingResult();
    return new ApiErrors(bindingResult);
  }

  @ExceptionHandler(BusinessException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ApiErrors handleBusinessException(BusinessException ex) {
    return new ApiErrors(ex);
  }

  @ExceptionHandler(ResponseStatusException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity handleResponseStatusException(ResponseStatusException ex) {
    return new ResponseEntity(new ApiErrors(ex), ex.getStatus());
  }

}
