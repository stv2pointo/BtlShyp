package main.btlshyp.message;

import lombok.Getter;
import lombok.ToString;

@ToString
public class JoinResponseMessage extends Message {
	
	public enum ConfirmJoin {ACCEPT, REJECT}
	
	@Getter
	private ConfirmJoin confirmJoin;
	
	public JoinResponseMessage(ConfirmJoin confirmJoin) {
		super(MessageType.JOIN_RESPONSE, "Server");
		this.confirmJoin = confirmJoin;
	}

}
