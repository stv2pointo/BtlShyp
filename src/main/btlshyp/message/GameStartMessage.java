package main.btlshyp.message;

import lombok.Getter;
import lombok.ToString;

@ToString
public class GameStartMessage extends Message  {

	public GameStartMessage(String username) {
		super(MessageType.GAME_START, username);
		this.opponentUsername = username;
	}

	@Getter
	private String opponentUsername;

}
