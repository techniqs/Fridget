package at.ac.tuwien.sepm.fridget.common.exception;

// @ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidArgumentException extends BaseException {

    public InvalidArgumentException(String message, Throwable parent) {
        super(message, parent);
    }

    public InvalidArgumentException(String message) {
        super(message);
    }

    public InvalidArgumentException(Throwable t) {
        super(t);
    }
}
