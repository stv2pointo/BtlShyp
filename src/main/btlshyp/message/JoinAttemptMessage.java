package main.btlshyp.message;

import lombok.ToString;

@ToString
public class JoinAttemptMessage extends Message{
	
	public JoinAttemptMessage(String username) {
		super(MessageType.JOIN_ATTEMPT, username);
	}

}
