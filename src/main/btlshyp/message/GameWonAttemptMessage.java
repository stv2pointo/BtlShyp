package main.btlshyp.message;

import lombok.ToString;

@ToString
public class GameWonAttemptMessage extends Message  {

	public GameWonAttemptMessage(String username) {
		super(MessageType.GAME_WON_ATTEMPT, username);
	}

}
