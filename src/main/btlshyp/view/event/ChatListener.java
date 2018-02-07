package main.btlshyp.view.event;
import java.util.EventListener;

public interface ChatListener extends EventListener{
  public void chatEventOccurred(ChatEvent ce);
}
