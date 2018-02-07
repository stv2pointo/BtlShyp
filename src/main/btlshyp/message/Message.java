package main.btlshyp.message;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Base class for the Message hierarchy. Messages are sent and received to an from a connected server and represent
 * things such as Chat Messages, Game Play Messages, etc.
 */
@ToString
public abstract class Message {

  /**
   * Needed to conform with the application message structure defined by the server
   */
  @Getter
  @Setter
  private String module = "BtlShyp";

  @Getter
  public final MessageType type;

  @Getter
  @Setter
  private String username;


  protected Message(MessageType type, String username) {
    this.type = type;
    this.username = username;
  }
}
