package com.management.exception;

import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.management.dto.ResponseStructure;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ResponseStructure<String>> handleResourceNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ResponseStructure.<String>builder()
                        .statusCode(HttpStatus.NOT_FOUND.value())
                        .message(ex.getMessage()).data(null).build());
    }

    @ExceptionHandler(IdNotFoundException.class)
    public ResponseEntity<ResponseStructure<String>> handleIdNotFound(IdNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ResponseStructure.<String>builder()
                        .statusCode(HttpStatus.NOT_FOUND.value())
                        .message(ex.getMessage()).data(null).build());
    }

    @ExceptionHandler(RuleViolationException.class)
    public ResponseEntity<ResponseStructure<String>> handleRuleViolation(RuleViolationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ResponseStructure.<String>builder()
                        .statusCode(HttpStatus.CONFLICT.value())
                        .message(ex.getMessage()).data(null).build());
    }

    @ExceptionHandler(DuplicateEntryException.class)
    public ResponseEntity<ResponseStructure<String>> handleDuplicate(DuplicateEntryException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ResponseStructure.<String>builder()
                        .statusCode(HttpStatus.CONFLICT.value())
                        .message(ex.getMessage()).data(null).build());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseStructure<String>> handleValidation(MethodArgumentNotValidException ex) {
        String msg = ex.getBindingResult().getFieldErrors()
                .stream().map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseStructure.<String>builder()
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .message(msg).data(null).build());
    }
}
