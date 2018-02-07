package main.btlshyp.message;

import lombok.ToString;

@ToString
public class LoginMessage extends Message {

  public LoginMessage(String username) {
    super(MessageType.LOGIN, username);
  }

}
