package at.ac.tuwien.sepm.fridget.server.configuration;

import at.ac.tuwien.sepm.fridget.common.exception.InvalidArgumentException;
import at.ac.tuwien.sepm.fridget.common.exception.PersistenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Optional;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(PersistenceException.class)
    protected ResponseEntity<Object> handlePersistenceException(PersistenceException ex, WebRequest request) {
        return new ResponseEntity<>(Optional.of(ex.getMessage()).orElse("Unknown Error"), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(InvalidArgumentException.class)
    protected ResponseEntity<Object> handleInvalidArgumentException(InvalidArgumentException ex, WebRequest request) {
        return new ResponseEntity<>(Optional.of(ex.getMessage()).orElse("Unknown Error"), HttpStatus.BAD_REQUEST);
    }

}
