package at.ac.tuwien.sepm.fridget.common.exception;

// @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class PersistenceException extends BaseException {

    public PersistenceException(String message, Throwable parent) {
        super(message, parent);
    }

    public PersistenceException(String message) {
        super(message);
    }

    public PersistenceException(Throwable t) {
        super(t);
    }
}
