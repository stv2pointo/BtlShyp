package main.btlshyp.network.exception;

/**
 * An exception which is thrown if there is an issue in the Input/Output stream or the associated Reader/Writer.
 */
public class NetCommunicationException extends RuntimeException {

  public NetCommunicationException(String message) {
    super(message);
  }

  public NetCommunicationException(String message, Throwable cause) {
    super(message, cause);
  }
}
