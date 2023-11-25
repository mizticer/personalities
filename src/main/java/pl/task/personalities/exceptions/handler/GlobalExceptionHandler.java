package pl.task.personalities.exceptions.handler;

import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pl.task.personalities.exceptions.ExceptionResponse;
import pl.task.personalities.exceptions.InvalidRequestException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, List<String>>> handleValidationErrors(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getFieldErrors()
                .stream().map(FieldError::getDefaultMessage).collect(Collectors.toList());
        Map<String, List<String>> body = new HashMap<>();
        body.put("ERRORS :", errors);
        return new ResponseEntity<>(body, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, List<String>>> handleConstraintViolationException(ConstraintViolationException ex) {
        List<String> errors = ex.getConstraintViolations()
                .stream().map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .collect(Collectors.toList());
        Map<String, List<String>> body = new HashMap<>();
        body.put("ERRORS", errors);
        return new ResponseEntity<>(body, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        String errorMessage = "Unique constraint violation. This record already exists.";
        Map<String, String> body = new HashMap<>();
        body.put("ERROR", errorMessage);
        return new ResponseEntity<>(body, new HttpHeaders(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ExceptionResponse> handleMethodArgumentNotValidException(InvalidRequestException e) {
        ExceptionResponse response = new ExceptionResponse(List.of(e.getMessage()), "BAD_REQUEST", LocalDateTime.now());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

}