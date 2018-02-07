package main.btlshyp.message;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class GameWonResponseMessage extends Message  {

  public enum GameResult {WIN, LOSE}

	public GameWonResponseMessage(String username, GameResult gameResult) {
		super(MessageType.GAME_WON_RESPONSE, username);
		this.gameResult = gameResult;
	}

	@Getter
  @Setter
	private GameResult gameResult;
	
}
