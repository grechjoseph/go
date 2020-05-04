package com.jg.gopractical.api.controller.advice;

import com.jg.gopractical.domain.exception.BaseException;
import com.jg.gopractical.domain.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import ma.glasnost.orika.MapEntry;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Validation Handler for the application's REST Controllers.
 */
@ControllerAdvice
public class ControllerValidationHandler extends ResponseEntityExceptionHandler {

    /**
     * Handle Http Message Not Readable (eg: Broken JSON).
     */
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(final HttpMessageNotReadableException ex,
                                                                  final HttpHeaders headers,
                                                                  final HttpStatus status,
                                                                  final WebRequest request) {
        return new ResponseEntity<>(new MapEntry<>("error", ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle cases that throw {@link BaseException}.
     */
    @ExceptionHandler(BaseException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<MapEntry<String, ErrorCode>> processBaseException(final BaseException ex) {
        return new ResponseEntity<>(new MapEntry<>("error", ex.getErrorCode()), HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle Method Argument Not Valid (eg: null for not-nullable field).
     */
    @Override
    public ResponseEntity handleMethodArgumentNotValid(final MethodArgumentNotValidException exception,
                                                       final HttpHeaders headers,
                                                       final HttpStatus status,
                                                       final WebRequest request) {

        final BeanPropertyBindingResult bindingResult = (BeanPropertyBindingResult) exception.getBindingResult().getModel().get(
                exception.getBindingResult().getModel().keySet().toArray()[1]);


        final InvalidArgumentInfo invalidArgumentInfo = new InvalidArgumentInfo(
                (String) exception.getBindingResult().getModel().keySet().toArray()[0],
                bindingResult.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList())
        );

        return new ResponseEntity<>(invalidArgumentInfo, headers, status);
    }

    @Data
    @AllArgsConstructor
    class InvalidArgumentInfo {
        private final String argumentName;
        private final List<String> errors;
    }
}
