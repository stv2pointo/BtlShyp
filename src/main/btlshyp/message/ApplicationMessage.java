package main.btlshyp.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import static main.btlshyp.message.ApplicationMessage.ApplicationMessageType.APPLICATION;

/**
 * Used to wrap around each message that is sent to the Server.
 */
@AllArgsConstructor
@RequiredArgsConstructor
public class ApplicationMessage {

  public enum ApplicationMessageType {
    ADMINISTRATION,
    APPLICATION,
    LOGIN,
    ACKNOWLEDGE,
    ERROR,
    CHAT;

    /**
     * Returns the enum's value in lowercase
     */
    @Override
    public String toString() {
      return name().toLowerCase();
    }
  }

  @Getter
  private String type = APPLICATION.toString();

  @NonNull
  private Message message;

}
