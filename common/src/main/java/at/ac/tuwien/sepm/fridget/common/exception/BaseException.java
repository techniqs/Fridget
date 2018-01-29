package at.ac.tuwien.sepm.fridget.common.exception;

abstract class BaseException extends Exception {

    public BaseException(String message, Throwable parent) {
        super(message, parent);
    }

    public BaseException(String message) {
        super(message);
    }

    public BaseException(Throwable t) {
        super(t);
    }

}