package ca.mcmaster.se2aa4.island.team103;

import org.junit.jupiter.api.Test;

import ca.mcmaster.se2aa4.island.team103.drone.Direction;
import ca.mcmaster.se2aa4.island.team103.drone.Drone;
import ca.mcmaster.se2aa4.island.team103.history.Coordinate;

import static org.junit.jupiter.api.Assertions.*;

// Testing of NavHistory and Coordinate systems.
class CoordinateTest {
	Drone drone;
	Coordinate expected;

	@Test
	void TRightFacingE() {
		drone = new Drone(Direction.EAST, 100000);
		drone.turnRight();
		Coordinate result = drone.getNavHistory().get(drone.getNavHistory().size()-1);
		expected = new Coordinate(1,1);
		assertEquals(expected.toString(),result.toString());
		assertTrue(expected.equalTo(result));
	}

	@Test
	void TRightFacingS() {
		drone = new Drone(Direction.SOUTH, 100000);
		drone.turnRight();
		Coordinate result = drone.getNavHistory().get(drone.getNavHistory().size()-1);
		expected = new Coordinate(-1,1);
		assertEquals(expected.toString(),result.toString());
		assertTrue(expected.equalTo(result));
	}

	@Test
	void TRightFacingW() {
		drone = new Drone(Direction.WEST, 100000);
		drone.turnRight();
		Coordinate result = drone.getNavHistory().get(drone.getNavHistory().size()-1);
		expected = new Coordinate(-1,-1);
		assertEquals(expected.toString(),result.toString());
		assertTrue(expected.equalTo(result));
	}

	@Test
	void TRightFacingN() {
		drone = new Drone(Direction.NORTH, 100000);
		drone.turnRight();
		Coordinate result = drone.getNavHistory().get(drone.getNavHistory().size()-1);
		expected = new Coordinate(1,-1);
		assertEquals(expected.toString(),result.toString());
		assertTrue(expected.equalTo(result));
	}

	@Test
	void TLeftFacingE() {
		drone = new Drone(Direction.EAST, 100000);
		drone.turnLeft();
		Coordinate result = drone.getNavHistory().get(drone.getNavHistory().size()-1);
		expected = new Coordinate(1,-1);
		assertEquals(expected.toString(),result.toString());
		assertTrue(expected.equalTo(result));
	}

	@Test
	void TLeftFacingS() { 
		drone = new Drone(Direction.SOUTH, 100000);
		drone.turnLeft();
		Coordinate result = drone.getNavHistory().get(drone.getNavHistory().size()-1);
		expected = new Coordinate(1,1);
		assertEquals(expected.toString(),result.toString());
		assertTrue(expected.equalTo(result));
	}

	@Test
	void TLeftFacingW() {
		drone = new Drone(Direction.WEST, 100000);
		drone.turnLeft();
		Coordinate result = drone.getNavHistory().get(drone.getNavHistory().size()-1);
		expected = new Coordinate(-1,1);
		assertEquals(expected.toString(),result.toString());
		assertTrue(expected.equalTo(result));
	}
	
	@Test
	void TLeftFacingN() {
		drone = new Drone(Direction.NORTH, 100000);
		drone.turnLeft();
		Coordinate result = drone.getNavHistory().get(drone.getNavHistory().size()-1);
		expected = new Coordinate(-1,-1);
		assertEquals(expected.toString(),result.toString());
		assertTrue(expected.equalTo(result));
	}

	@Test
	void ForwardsFacingE() {
		drone = new Drone(Direction.EAST, 100000);
		drone.flyForwards();
		Coordinate result = drone.getNavHistory().get(drone.getNavHistory().size()-1);
		expected = new Coordinate(1,0);
		assertEquals(expected.toString(),result.toString());
		assertTrue(expected.equalTo(result));
	}

	@Test
	void ForwardsFacingS() {
		drone = new Drone(Direction.SOUTH, 100000);
		drone.flyForwards();
		Coordinate result = drone.getNavHistory().get(drone.getNavHistory().size()-1);
		expected = new Coordinate(0,1);
		assertEquals(expected.toString(),result.toString());
		assertTrue(expected.equalTo(result));
	}

	@Test
	void ForwardsFacingW() {
		drone = new Drone(Direction.WEST, 100000);
		drone.flyForwards();
		Coordinate result = drone.getNavHistory().get(drone.getNavHistory().size()-1);
		expected = new Coordinate(-1,0);
		assertEquals(expected.toString(),result.toString());
		assertTrue(expected.equalTo(result));
	}

	@Test
	void ForwardsFacingN() {
		drone = new Drone(Direction.NORTH, 100000);
		drone.flyForwards();
		Coordinate result = drone.getNavHistory().get(drone.getNavHistory().size()-1);
		expected = new Coordinate(0,-1);
		assertEquals(expected.toString(),result.toString());
		assertTrue(expected.equalTo(result));
	}

	@Test
	void Echo() {
		drone = new Drone(Direction.NORTH, 100000);
		drone.echoForward();
		Coordinate result = drone.getNavHistory().get(drone.getNavHistory().size()-1);
		expected = new Coordinate(0,0);
		assertEquals(expected.toString(),result.toString());
		assertTrue(expected.equalTo(result));
	}

	@Test
	void Scan() {
		drone = new Drone(Direction.NORTH, 100000);
		drone.scan();
		Coordinate result = drone.getNavHistory().get(drone.getNavHistory().size()-1);
		expected = new Coordinate(0,0);
		assertEquals(expected.toString(),result.toString());
		assertTrue(expected.equalTo(result));
	}
}
