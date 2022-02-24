package com.rostami.urlshortener.controller.api.errors;

import com.rostami.urlshortener.controller.api.core.ServiceResult;
import com.rostami.urlshortener.controller.api.errors.api.ApiError;
import com.rostami.urlshortener.exception.NullUrlException;
import com.rostami.urlshortener.exception.UrlNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.annotation.Nonnull;
import java.util.Objects;

import static org.springframework.http.HttpStatus.*;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
@Slf4j
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
    // ---------------------- CUSTOM Exception Handling -------------------------------------------

    @ExceptionHandler(UrlNotFoundException.class)
    protected ResponseEntity<ServiceResult<Void>> handleUrlnotFound(
            UrlNotFoundException ex) {
        var apiError = new ApiError(NOT_FOUND);
        apiError.setMessage(ex.getMessage());
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(NullUrlException.class)
    protected ResponseEntity<ServiceResult<Void>> handleNullUrl(
            NullUrlException ex) {
        var apiError = new ApiError(NOT_FOUND);
        apiError.setMessage(ex.getMessage());
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ServiceResult<Void>> finalHandlingIfExceptionHandlingWasNotFound(
            Exception ex) {
        var apiError = new ApiError(NOT_FOUND);
        apiError.setMessage(ex.getMessage());
        return buildResponseEntity(apiError);
    }


    // ---------------------- Default Exception Handling -------------------------------------------

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<ServiceResult<Void>> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        var apiError = new ApiError(BAD_REQUEST);
        apiError.setMessage(String.format("The parameter '%s' of value '%s' could not be converted to type '%s'", ex.getName(), ex.getValue(), Objects.requireNonNull(ex.getRequiredType()).getSimpleName()));
        apiError.setDebugMessage(ex.getMessage());
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<ServiceResult<Void>> handleBadRequest(
            IllegalArgumentException ex) {
        var apiError = new ApiError(BAD_REQUEST);
        apiError.setMessage(ex.getMessage());
        return buildResponseEntity(apiError);
    }


    @Override
    @Nonnull
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex,
            @Nonnull HttpHeaders headers,
            @Nonnull HttpStatus status,
            @Nonnull WebRequest request) {
        String error = ex.getParameterName() + " parameter is missing";
        return buildResponseEntityForOverrideMethods(new ApiError(BAD_REQUEST, error, ex));
    }

    @Override
    @Nonnull
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException ex,
            @Nonnull HttpHeaders headers,
            @Nonnull HttpStatus status,
            @Nonnull WebRequest request) {
        StringBuilder builder = new StringBuilder();
        builder.append(ex.getContentType());
        builder.append(" media type is not supported. Supported media types are ");
        ex.getSupportedMediaTypes().forEach(t -> builder.append(t).append(", "));
        return buildResponseEntityForOverrideMethods(new ApiError(HttpStatus.UNSUPPORTED_MEDIA_TYPE, builder.substring(0, builder.length() - 2), ex));
    }

    @Override
    @Nonnull
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            @Nonnull HttpHeaders headers,
            @Nonnull HttpStatus status,
            @Nonnull WebRequest request) {
        var apiError = new ApiError(BAD_REQUEST);
        apiError.setMessage("Validation error");
        apiError.addValidationErrors(ex.getBindingResult().getFieldErrors());
        apiError.addValidationError(ex.getBindingResult().getGlobalErrors());
        return buildResponseEntityForOverrideMethods(apiError);
    }

    @ExceptionHandler(javax.validation.ConstraintViolationException.class)
    protected ResponseEntity<Object> handleConstraintViolation(
            javax.validation.ConstraintViolationException ex) {
        var apiError = new ApiError(BAD_REQUEST);
        apiError.setMessage("Validation error");
        apiError.addValidationErrors(ex.getConstraintViolations());
        return buildResponseEntityForOverrideMethods(apiError);
    }

    @Override
    @Nonnull
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            @Nonnull HttpMessageNotReadableException ex,
            @Nonnull HttpHeaders headers,
            @Nonnull HttpStatus status,
            @Nonnull WebRequest request) {
        ServletWebRequest servletWebRequest = (ServletWebRequest) request;
        log.info("{} to {}", servletWebRequest.getHttpMethod(), servletWebRequest.getRequest().getServletPath());
        var error = "Malformed JSON request";
        return buildResponseEntityForOverrideMethods(new ApiError(HttpStatus.BAD_REQUEST, error, ex));
    }

    @Override
    @Nonnull
    protected ResponseEntity<Object> handleHttpMessageNotWritable(
            @Nonnull HttpMessageNotWritableException ex,
            @Nonnull HttpHeaders headers,
            @Nonnull HttpStatus status,
            @Nonnull WebRequest request) {
        var error = "Error writing JSON output";
        return buildResponseEntityForOverrideMethods(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, error, ex));
    }

    @Override
    @Nonnull
    protected ResponseEntity<Object> handleNoHandlerFoundException(
            NoHandlerFoundException ex,
            @Nonnull HttpHeaders headers,
            @Nonnull HttpStatus status,
            @Nonnull WebRequest request) {
        var apiError = new ApiError(BAD_REQUEST);
        apiError.setMessage(String.format("Could not find the %s method for URL %s", ex.getHttpMethod(), ex.getRequestURL()));
        apiError.setDebugMessage(ex.getMessage());
        return buildResponseEntityForOverrideMethods(apiError);
    }


    // --------------------------- Util Functions ---------------------------------------

    private ResponseEntity<ServiceResult<Void>> buildResponseEntity(ApiError apiError) {
        var serviceResult = ServiceResult.fail(apiError);
        return new ResponseEntity<>(serviceResult, apiError.getStatus());
    }

    private ResponseEntity<Object> buildResponseEntityForOverrideMethods(ApiError apiError) {
        var serviceResult = ServiceResult.fail(apiError);
        return new ResponseEntity<>(serviceResult, apiError.getStatus());
    }
}
