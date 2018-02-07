package main.btlshyp.message;

import lombok.ToString;

@ToString
public class ShipsPlacedMessage extends Message  {

	public ShipsPlacedMessage(String username) {
		super(MessageType.SHIPS_PLACED, username);
	}

}
