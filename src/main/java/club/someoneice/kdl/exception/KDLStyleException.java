package club.someoneice.kdl.exception;

public class KDLStyleException extends Exception {
  private static final long serialVersionUID = -7164454391696940498L;

  public KDLStyleException() {
    super();
  }

  public KDLStyleException(String message) {
    super(message);
  }

  public KDLStyleException(Throwable e) {
    super(e);
  }

  public KDLStyleException(String message, Throwable e) {
    super(message, e);
  }
}
