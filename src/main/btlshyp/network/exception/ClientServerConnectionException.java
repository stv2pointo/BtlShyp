package main.btlshyp.network.exception;

/**
 * An exception which is thrown if there is an issue with the Client and Server connecting
 */
public class ClientServerConnectionException extends RuntimeException {

  public ClientServerConnectionException(String message) {
    super(message);
  }

  public ClientServerConnectionException(String message, Throwable cause) {
    super(message, cause);
  }

}
