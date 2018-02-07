package main.btlshyp.view.event;

import java.util.EventObject;

import lombok.Getter;
import lombok.Setter;
import main.btlshyp.model.Coordinate;

public class AttackEvent extends EventObject {
  @Getter @Setter
  private Coordinate coordinate;
  
  public AttackEvent(Object source, Coordinate coordinate) {
    super(source);
    this.coordinate = coordinate;
  }

}