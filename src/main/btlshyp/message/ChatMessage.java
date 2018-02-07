package main.btlshyp.message;

import lombok.Getter;
import lombok.ToString;

@ToString
public class ChatMessage extends Message{
	
	@Getter
	private String text;

	public ChatMessage(String text, String username) {
		super(MessageType.CHAT, username);
		setText(text);
	}

	private void setText(String text) {
		if (text == null) {
			throw new IllegalArgumentException("Chat text cannot be empty");
		}

		this.text = text;
	}

}
