package at.ac.tuwien.sepm.fridget.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BalanceNotZeroException extends BaseException {
    public BalanceNotZeroException(String message, Throwable parent) {
        super(message, parent);
    }

    public BalanceNotZeroException(String message) {
        super(message);
    }

    public BalanceNotZeroException(Throwable t) {
        super(t);
    }
}
