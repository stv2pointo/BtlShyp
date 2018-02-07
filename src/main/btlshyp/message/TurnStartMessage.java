package main.btlshyp.message;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class TurnStartMessage extends Message  {

	public enum Turn {
		START,
		END
	}

	@Getter
	@Setter
	private Turn turn;

	public TurnStartMessage(String username, Turn turn) {
		super(MessageType.TURN, username);
		this.turn = turn;
	}

}
