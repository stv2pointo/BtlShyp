package main.btlshyp.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@EqualsAndHashCode
@ToString
@Slf4j
public class Ship {
  public final static int PATROL_SIZE = 2;
  public final static int SUB_SIZE = 3;
  public final static int DESTROYER_SIZE = 3;
  public final static int BATTLESHIP_SIZE = 4;
	@Getter
	@Setter
	private ArrayList<Coordinate> shipCoordinates;
	@Getter
	private int shipSize;

	@Getter
	private final ShipType shipType;

	public Ship(ShipType type) {
		shipType = type;
		setSize(type);
	}

	public void setSize(ShipType type) {
	  switch(type) {
	  case PATROLBOAT:
	    shipSize = PATROL_SIZE;
	    break;
	  case SUBMARINE:
	    shipSize = SUB_SIZE;
	    break;
	  case DESTROYER:
	    shipSize = DESTROYER_SIZE;
	    break;
	  case BATTLESHIP:
	    shipSize =  BATTLESHIP_SIZE;
	    break;
	    default:
	      shipSize = 0;
	      break;
	  }
	}

	public boolean isValid() {
		log.info("Validating Ship {}", this);

		// valid ships have coordinates and a size
		if(shipCoordinates == null || shipSize == 0 ) {
			log.info("Ship is null or has no size. Rejecting.");
			return false;
		}

	  // size must match number of coordinates
    if(shipSize !=  shipCoordinates.size() ) {
			log.info("Number of coordinates and ship size don't match. Rejecting.");
      return false;
    }

		// can not have coordinates out of range
		for(Coordinate c : shipCoordinates) {
		  if(c.x < 0 || c.x >= Board.WIDTH || c.y < 0 || c.y >= Board.HEIGHT ) {
		  	log.info("Coordinate {} is out of range. Rejecting.", c);
		    return false;
		  }
		}

    // can not be separated
		ArrayList<Integer> xs = new ArrayList<>();
    ArrayList<Integer> ys = new ArrayList<>();
    for (Coordinate c : shipCoordinates) {
      xs.add(c.x);
      ys.add(c.y);
    }
		Set<Integer> xTest = new HashSet<>(xs);
		Set<Integer> yTest = new HashSet<>(ys);
    if (yTest.size() == 1 && xTest.size() == shipSize) {
      Collections.sort(xs);
      for (int i = 0; i < shipSize-1; i++) {
        if (xs.get(i + 1) != (xs.get(i) + 1)) {
        	log.info("Coordinates are not contiguous. Rejecting.");
          return false;
        }
      }
    }

    // can not be diagonal
		if (!  ((xTest.size() == 1 && yTest.size() == shipSize) || (yTest.size() == 1 && xTest.size() == shipSize)) ){
    	log.info("Coordinates are diagonal. Rejecting.");
			return false;
		}

		// well, we made it, it must be a good ship
		log.info("Ship is valid.");
		return true;
	}
}
