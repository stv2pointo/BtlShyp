package main.btlshyp.message;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import main.btlshyp.model.Coordinate;

@ToString
public class AttackAttemptMessage extends Message  {

	public AttackAttemptMessage(String username, Coordinate coordinate) {
		super(MessageType.ATTACK_ATTEMPT, username);
		this.coordinate = coordinate;
	}

	@Getter
  @Setter
	private Coordinate coordinate;

}
