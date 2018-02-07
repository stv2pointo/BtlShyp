package main.btlshyp.model;

import lombok.Getter;
import lombok.Setter;

public class Model {

  @Getter @Setter
  String userName;

  @Getter @Setter
  String oppUserName;

  private Board board;

  public Model() {
    board = new Board();
  }

  public boolean isShipPlacementValid(Ship ship) {
    return board.isShipPlacementValid(ship);
  }
  
  public void placeShip(Ship ship) {
    board.placeShip(ship);
  }

  public void storeResultOfAttackOnOpponent(boolean isSuccessfullAttack, Coordinate coordinate) {
    board.storeResultOfAttackOnOpponent(isSuccessfullAttack, coordinate);
  }
  
  public void storeSunkenOpponentShip(Ship sunkenShip) {
    board.storeSunkenOpponentShip(sunkenShip);
  }
  
  public boolean isGameWon() {
    return board.isGameWon();
  }
  
  public boolean isGameLost() {
    return board.isGameLost();
  }
  /**
   * 
   * @param coordinate
   * @return an object with all the results of the attack
   * whether it was a hit, sunk a boat, if boat sunk, which boat
   *    if boat sunk, was it the last boat
   */
  public AttackResult getAttackResults(Coordinate coordinate) {
    AttackResult attackResult = new AttackResult();
    
    if(coordinate == null || coordinate.x < 0 || coordinate.x >= Board.WIDTH || coordinate.y < 0 || coordinate.y >= Board.HEIGHT ) {
      attackResult.isHit = false;
      return attackResult;
    }
    
    attackResult.isHit = board.isHit(coordinate);
    if(attackResult.isHit == true) {
      attackResult.ship = board.getShipAt(coordinate);
      attackResult.isSunk = board.isShipSunk(attackResult.ship);
      attackResult.isGameLost = board.isGameLost();
    }
    return attackResult;
  }

  public class AttackResult {
    public boolean isHit;
    public boolean isSunk;
    public boolean isGameLost;
    
    public Ship ship;
    
    public AttackResult() {
    }
  }
}
