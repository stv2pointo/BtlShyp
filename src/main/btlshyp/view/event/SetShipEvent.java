package main.btlshyp.view.event;

import java.util.EventObject;

import lombok.Getter;
import lombok.Setter;
import main.btlshyp.model.Ship;

public class SetShipEvent extends EventObject {
  @Getter @Setter
  private Ship ship;
  
  public SetShipEvent(Object source, Ship ship) {
    super(source);
    this.ship = ship;
  }
}
