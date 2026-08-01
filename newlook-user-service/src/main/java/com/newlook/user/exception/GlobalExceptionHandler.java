package com.newlook.user.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserProfileNotFoundException.class)
    public ProblemDetail handleNotFound(UserProfileNotFoundException ex, HttpServletRequest req) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        pd.setTitle("User profile not found");
        pd.setProperty("path", req.getRequestURI());
        pd.setProperty("timestamp", Instant.now());
        return pd;
    }

    @ExceptionHandler(DuplicateUserException.class)
    public ProblemDetail handleDuplicate(DuplicateUserException ex, HttpServletRequest req) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        pd.setTitle("Duplicate user");
        pd.setProperty("path", req.getRequestURI());
        pd.setProperty("timestamp", Instant.now());
        return pd;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(
                fe -> fieldErrors.put(fe.getField(), fe.getDefaultMessage())
        );
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Validation failed");
        pd.setTitle("Invalid request");
        pd.setProperty("errors", fieldErrors);
        pd.setProperty("path", req.getRequestURI());
        pd.setProperty("timestamp", Instant.now());
        return pd;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArg(IllegalArgumentException ex, HttpServletRequest req) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        pd.setTitle("Bad request");
        pd.setProperty("path", req.getRequestURI());
        pd.setProperty("timestamp", Instant.now());
        return pd;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneric(Exception ex, HttpServletRequest req) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error occurred"
        );
        pd.setTitle("Internal server error");
        pd.setProperty("path", req.getRequestURI());
        pd.setProperty("timestamp", Instant.now());
        return pd;
    }
}
