package test;

import static main.btlshyp.model.ShipType.DESTROYER;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;

import main.btlshyp.model.Coordinate;
import main.btlshyp.model.Ship;

public class ShipTest {

	private Ship fixture;

	@Test
	public void testIsValid_Valid_Horizontal() throws Exception {
		// Create a new Ship
		fixture = new Ship(DESTROYER);

		// Create some Coordinates
		ArrayList<Coordinate> coordinates = Stream.of(new Coordinate(2, 2), new Coordinate(3, 2), new Coordinate(4, 2))
				.collect(Collectors.toCollection(ArrayList::new));

		// Set the Coordinates
		fixture.setShipCoordinates(coordinates);

		// Test the method
		boolean result = fixture.isValid();

		// Assert that the result is what we're expecting
		assertThat(result, is(true));
	}

	@Test
	public void testIsValid_Valid_Vertical() throws Exception {
		// GIVEN I have a Ship with some valid coordinates along the Y-axis
		fixture = new Ship(DESTROYER);

		ArrayList<Coordinate> coordinates = Stream.of(new Coordinate(2, 2), new Coordinate(2, 3), new Coordinate(2, 4))
				.collect(Collectors.toCollection(ArrayList::new));

		fixture.setShipCoordinates(coordinates);

		// WHEN I check if they are valid
		boolean result = fixture.isValid();

		// THEN it should return that the coordinates are valid
		assertThat(result, is(true));
	}

	@Test
	public void testIsValid_Invalid_Diagonal() throws Exception {
		fixture = new Ship(DESTROYER);

		ArrayList<Coordinate> coordinates = Stream.of(new Coordinate(2, 2), new Coordinate(3, 3), new Coordinate(4, 4))
				.collect(Collectors.toCollection(ArrayList::new));

		fixture.setShipCoordinates(coordinates);

		boolean result = fixture.isValid();

		assertThat(result, is(false));
	}

	@Test
	public void testIsValid_Invalid_SameCoordinates() throws Exception {
		fixture = new Ship(DESTROYER);

		ArrayList<Coordinate> coordinates = Stream.of(new Coordinate(2, 2), new Coordinate(2, 2), new Coordinate(2, 2))
				.collect(Collectors.toCollection(ArrayList::new));

		fixture.setShipCoordinates(coordinates);

		boolean result = fixture.isValid();

		assertThat(result, is(false));
	}

	@Test
	public void testIsValid_Invalid_WonkyCoordinates() throws Exception {
		fixture = new Ship(DESTROYER);

		ArrayList<Coordinate> coordinates = Stream.of(new Coordinate(2, 2), new Coordinate(2, 2), new Coordinate(2, 3))
				.collect(Collectors.toCollection(ArrayList::new));

		fixture.setShipCoordinates(coordinates);

		boolean result = fixture.isValid();

		assertThat(result, is(false));
	}

	@Test
	public void testIsValid_Invalid_GapHorizontal() throws Exception {
		fixture = new Ship(DESTROYER);

		ArrayList<Coordinate> coordinates = Stream.of(new Coordinate(2, 2), new Coordinate(3, 2), new Coordinate(5, 2))
				.collect(Collectors.toCollection(ArrayList::new));

		fixture.setShipCoordinates(coordinates);

		boolean result = fixture.isValid();

		assertThat(result, is(false));
	}

	@Test
	public void testIsValid_Invalid_GapVertical() throws Exception {
		fixture = new Ship(DESTROYER);

		ArrayList<Coordinate> coordinates = Stream.of(new Coordinate(2, 2), new Coordinate(2, 3), new Coordinate(2, 5))
				.collect(Collectors.toCollection(ArrayList::new));

		fixture.setShipCoordinates(coordinates);

		boolean result = fixture.isValid();

		assertThat(result, is(false));
	}

	@Test
	public void testIsValid_Invalid_TooManyCoordinates() throws Exception {
		fixture = new Ship(DESTROYER);

		ArrayList<Coordinate> coordinates = Stream
				.of(new Coordinate(2, 2), new Coordinate(3, 2), new Coordinate(4, 2), new Coordinate(5, 2))
				.collect(Collectors.toCollection(ArrayList::new));

		fixture.setShipCoordinates(coordinates);

		boolean result = fixture.isValid();

		assertThat(result, is(false));
	}

	@Test
	public void testIsValid_Invalid_TooFewCoordinates() throws Exception {
		fixture = new Ship(DESTROYER);

		ArrayList<Coordinate> coordinates = Stream.of(new Coordinate(2, 2), new Coordinate(3, 2))
				.collect(Collectors.toCollection(ArrayList::new));

		fixture.setShipCoordinates(coordinates);

		boolean result = fixture.isValid();

		assertThat(result, is(false));
	}

	@Test
	public void testIsValid_Invalid_ZeroCoordinates() throws Exception {
		fixture = new Ship(DESTROYER);

		boolean result = fixture.isValid();

		assertThat(result, is(false));
	}

	@Test
	public void testShip_Invalid_SizeZero() throws Exception {
		// What if I construct a ship with a size of 0? That doesn't seem like a
		// valid scenario.
		fixture = new Ship(DESTROYER);
		boolean result = fixture.isValid();

		assertThat(fixture, notNullValue());
		assertThat(result, is(false));
	}

}