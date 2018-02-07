package main.btlshyp.view.event;

import java.util.EventObject;

import lombok.Getter;
import lombok.Setter;

public class ChatEvent extends EventObject {
  @Getter @Setter
  private String chat;
  
	public ChatEvent(Object source) {
		super(source);
	}
	public ChatEvent(Object source, String chat) {
		super(source);
		this.chat = chat;
	}

}
