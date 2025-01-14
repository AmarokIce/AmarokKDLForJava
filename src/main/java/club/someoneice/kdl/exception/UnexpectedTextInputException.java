package club.someoneice.kdl.exception;

@SuppressWarnings("unused")
public class UnexpectedTextInputException extends RuntimeException {

    private static final long serialVersionUID = -8268047723037339004L;

    protected UnexpectedTextInputException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public UnexpectedTextInputException(Throwable cause) {
        super(cause);
    }

    public UnexpectedTextInputException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnexpectedTextInputException(String message) {
        super(message);
    }

    public UnexpectedTextInputException() {
        super();
    }
}
