package main.btlshyp.message;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import main.btlshyp.model.Coordinate;

@ToString
public class AttackResponseMessage extends Message {

  public enum HitOrMiss {
    HIT, MISS
  }

  public enum ShipSunk {
    NONE, PATROL_BOAT, SUBMARINE, DESTROYER, BATTLESHIP
  }

  @Getter
  @Setter
  private HitOrMiss hitOrMiss;
  @Getter
  @Setter
  private ShipSunk shipSunk;
  @Getter
  @Setter
  private Coordinate coordinate;

  public AttackResponseMessage(String username, HitOrMiss hitOrMiss, ShipSunk shipSunk, Coordinate coordinate) {
    super(MessageType.ATTACK_RESPONSE, username);
    this.hitOrMiss = hitOrMiss;
    this.shipSunk = shipSunk;
    this.coordinate = coordinate;
  }

}
