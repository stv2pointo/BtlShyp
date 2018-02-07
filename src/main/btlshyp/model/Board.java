package main.btlshyp.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ToString
public class Board {
  ArrayList<Ship> ships;
  static Square[][] squares;
  public final static int WIDTH = 5;
  public final static int HEIGHT = 5;
  Set<Coordinate>hitCoordinatesOnOpponent = new HashSet<>();
  Set<Ship>sunkenOpponentShips = new HashSet<>();

  /**
   * A new board gets
   * an empty list of ships
   *  a 2d array of squares
   *      each square is unoccupied and undamaged
   */
  public Board() {
    ships =  new ArrayList<Ship>();
    squares = new Square[WIDTH][HEIGHT];
    for (int i = 0; i < WIDTH; i++) {
      for (int j = 0; j < HEIGHT; j++) {
        squares[i][j] = new Square();
      }
    }
  }

  /**
   * Verifies if a ship placement will work
   * 
   * @param ship
   * @return
   */
  public boolean isShipPlacementValid(Ship ship) {
    log.info("Validating ship placement");

    // bad ships can't be placed
    if (!ship.isValid()) {
      log.info("Ship isn't valid. Rejecting.");
      return false;
    }
    // ships can't be placed on top of each other
    for (Coordinate coordinate : ship.getShipCoordinates()) {
      if (squares[coordinate.x][coordinate.y].isOccupied) {
        log.info("Ship's coordinate {} is already occupied. Rejecting.", coordinate);
        return false;
      }
    }
    // must be good
    log.info("Ship placement is valid.");
    return true;
  }

  /**
   * Adds ship to a list of ships 
   * Marks that ships coordinates as occupied
   * 
   * @param ship
   */
  public void placeShip(Ship ship) {
    log.info("Placing ship {}", ship);
    ships.add(ship);
    for (Coordinate coordinate : ship.getShipCoordinates()) {
      squares[coordinate.x][coordinate.y].isOccupied = true;
    }
  }

  /**
   * Evaluate if the attack coordinates are a "hit" Put a red peg in that hole if so
   * @param coordinate
   * @return true if there is a boat in that spot
   */
  public boolean isHit(Coordinate coordinate) {
    log.info("Evaluating hit.");
    if (squares[coordinate.x][coordinate.y].isOccupied) {
      log.info("Hit has occurred at {}", coordinate);
      squares[coordinate.x][coordinate.y].isDamaged = true;
      return true;
    } else {
      log.info("Miss has occurred at {}", coordinate);
      return false;
    }
  }

  /**
   * Finds out who lives at this address, if anybody
   * 
   * @param strikeCoordinate
   * @return
   */
  public Ship getShipAt(Coordinate strikeCoordinate) {
    for (Ship ship : ships) {
      for (Coordinate coordinate : ship.getShipCoordinates()) {
        if (coordinate.equals(strikeCoordinate)) {
          log.info("Ship at {} is {}", coordinate, ship);
          return ship;
        }
      }
    }
    log.info("No ship at {}", strikeCoordinate);
    return null;
  }

  /**
   * Evaluates a ship's status
   * 
   * @param ship
   * @return
   */
  public boolean isShipSunk(Ship ship) {
    log.info("Determining if ship {} was sunk", ship);
    for (Coordinate coordinate : ship.getShipCoordinates()) {
      if (squares[coordinate.x][coordinate.y].isDamaged() == false) {
        log.info("Ship was not sunk.");
        return false;
      }
    }
    // all of this ship's coordinates were damaged
    log.info("Ship is sunk.");
    return true;
  }

  /**
   * 
   * @return
   */
  public boolean isGameLost() {
    log.info("Determining is game is lost and all ships are sunk");
    for (Ship ship : ships) {
      if (isShipSunk(ship) == false) {
        log.info("A ship still survives. Hope is not lost.");
        return false;
      }
    }
    // all must be sunk
    log.info("All ships are sunk. Hope is lost...");
    return true;
  }
  
  public void storeResultOfAttackOnOpponent(boolean isSuccessfullAttack, Coordinate coordinate) {
    if(isSuccessfullAttack) {
      hitCoordinatesOnOpponent.add(coordinate);
    }
    
  }
  
  public void storeSunkenOpponentShip(Ship sunkenShip) {
    sunkenOpponentShips.add(sunkenShip);
  }
  
  public boolean isGameWon() {
    log.info("Determining whether game is won...");
    if (hitCoordinatesOnOpponent.size() == (Ship.PATROL_SIZE + Ship.SUB_SIZE + Ship.DESTROYER_SIZE + Ship.BATTLESHIP_SIZE)
      || sunkenOpponentShips.size() == 4) {
      log.info("Game is won! All ships are sunk. Victory is ours!");
      return true;
    } else {
      log.info("Game is not won... yet...");
      return false;
    }
  }

  /**
   * A Square can be occupied or not A Square can be hit or not
   */
  public class Square {
    @Getter
    @Setter
    boolean isOccupied;
    @Getter
    @Setter
    boolean isDamaged;

    public Square() {
      this.isOccupied = false;
      this.isDamaged = false;
    }

  }
}
