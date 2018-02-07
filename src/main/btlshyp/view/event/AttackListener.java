package main.btlshyp.view.event;

import java.util.EventListener;

public interface AttackListener extends EventListener{
  public void attackEventOccurred(AttackEvent ae);
}
