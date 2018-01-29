package at.ac.tuwien.sepm.fridget.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class EmailException extends BaseException {

    public EmailException(String message, Throwable parent) {
        super(message, parent);
    }

    public EmailException(String message) {
        super(message);
    }

    public EmailException(Throwable t) {
        super(t);
    }
}
