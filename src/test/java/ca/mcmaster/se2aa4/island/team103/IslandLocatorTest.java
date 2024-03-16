package ca.mcmaster.se2aa4.island.team103;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.json.JSONObject;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class IslandLocatorTest {

	JSONObject detection;
	JSONObject expected;
	Optional<JSONObject> expectedOp;
	History<JSONObject> history;
	Drone drone;
	Drone drone_reference;
	DroneController locator;
	Optional<JSONObject> result;
	JSONObject last_history;
	JSONObject extras;
	final Logger logger = LogManager.getLogger();

	@BeforeEach
	public void reset() {
		detection = new JSONObject();
		expected = new JSONObject();
		history = new ResponseHistory();
		drone = new Drone(Direction.EAST,100000);
		drone_reference = new Drone(Direction.EAST,100000);
		locator = new IslandLocator(drone, history);
	}

	public void addOORtoHistory() {
		// Adds a simulated out_of_range response to history
		last_history = new JSONObject();
		extras = new JSONObject();
		extras.put("found", "OUT_OF_RANGE");
		extras.put("range", 10);
		last_history.put("extras", extras);
		history.addItem(last_history);
	}

	public void addGroundtoHistory() {
		// Adds a simulated GROUND response to history
		last_history = new JSONObject();
		extras = new JSONObject();
		extras.put("found", "GROUND");
		extras.put("range", 10);
		last_history.put("extras", extras);
		history.addItem(last_history);
	}

	public void addOORtoHistory(int dist) {
		// Adds a simulated out_of_range response to history with option for custom distance
		last_history = new JSONObject();
		extras = new JSONObject();
		extras.put("found", "OUT_OF_RANGE");
		extras.put("range", dist);
		last_history.put("extras", extras);
		history.addItem(last_history);
	}

	public void addGroundtoHistory(int dist) {
		// Adds a simulated GROUND response to history with option for custom distance
		last_history = new JSONObject();
		extras = new JSONObject();
		extras.put("found", "GROUND");
		extras.put("range", dist);
		last_history.put("extras", extras);
		history.addItem(last_history);
	}

	public void searchFwrd(int dist) {
		// Move forward a specified distance
		// Drone MUST be in either SEARCH or FF state!!
		// Last history MUST be OOR
		for (int i = 0; i < dist; i++) {
			locator.nextAction(drone, history);
			locator.nextAction(drone, history);
			locator.nextAction(drone, history);
			locator.nextAction(drone, history);
		}
	}

	public void performUTURN() {
		// Causes drone to complete UTURN maneuver (UTURN_R or UTURN_L, NOT UTURN_F)
		// Must be at the start of the maneurver already
		locator.nextAction(drone, history);
		locator.nextAction(drone, history);
		locator.nextAction(drone, history);
		locator.nextAction(drone, history);
		locator.nextAction(drone, history);
	}

	public void setupFinalForwards() {
		// Handles drone from itialialization to FF, leaves drone after having drone perform FF scan
		addOORtoHistory();
		searchFwrd(5);
		locator.nextAction(drone, history);
		locator.nextAction(drone, history);
		locator.nextAction(drone, history);
		addGroundtoHistory();
		locator.nextAction(drone, history);
		performUTURN();
		locator.nextAction(drone, history);
	}

	public void setupUturnF() {
		addOORtoHistory();
		searchFwrd(3);
		locator.nextAction(drone, history);
		locator.nextAction(drone, history);
		addGroundtoHistory(2);
		locator.nextAction(drone, history);
		locator.nextAction(drone, history);
		addGroundtoHistory(4);
		locator.nextAction(drone, history);
		locator.nextAction(drone, history);
		locator.nextAction(drone, history);
	}

	@Test
	public void testSearch() {
		result = locator.nextAction(drone, history);

		expected = drone.echoRight();
		assertTrue(result.isPresent());
		assertEquals(expected.toString(), result.get().toString());

		addOORtoHistory();
		
		result = locator.nextAction(drone, history);

		expected = drone.echoForward();
		assertTrue(result.isPresent());
		assertEquals(expected.toString(), result.get().toString());

		result = locator.nextAction(drone, history);

		expected = drone.echoLeft();
		assertTrue(result.isPresent());
		assertEquals(expected.toString(), result.get().toString());

		result = locator.nextAction(drone, history);

		expected = drone.flyForwards();
		assertTrue(result.isPresent());
		assertEquals(expected.toString(), result.get().toString());
	} 

	@Test
	public void testFoundR() {
		locator.nextAction(drone, history);
		addGroundtoHistory();

		result = locator.nextAction(drone, history);

		expected = drone_reference.turnRight();
		assertTrue(result.isPresent());
		assertEquals(expected.toString(), result.get().toString());
	}

	@Test
	public void testFoundF() {
		locator.nextAction(drone, history);

		addOORtoHistory();

		locator.nextAction(drone, history);

		addGroundtoHistory(2);

		result = locator.nextAction(drone, history);

		expected = drone_reference.turnRight();
		assertTrue(result.isPresent());
		assertEquals(expected.toString(), result.get().toString());

	}

	@Test
	public void testFoundL() {
		locator.nextAction(drone, history);

		addOORtoHistory();
		
		locator.nextAction(drone, history);
		locator.nextAction(drone, history);

		addGroundtoHistory();

		result = locator.nextAction(drone, history);
		expected = drone_reference.turnLeft();
		assertTrue(result.isPresent());
		assertEquals(expected.toString(), result.get().toString());
	}
	

	@Test
	public void testFFclose() {
		// Tests FF edge case where drone is next to shore immidiately after performing UTURN
		setupFinalForwards();
		addGroundtoHistory(0);
		result = locator.nextAction(drone, history);
		assertTrue(result.isEmpty());
	}
	
	@Test
	public void testFFfar() {
		// Tests FF normal case of shore 5 blocks away
		setupFinalForwards();
		addGroundtoHistory(5);
		locator.nextAction(drone, history);
		locator.nextAction(drone, history);
		locator.nextAction(drone, history);
		locator.nextAction(drone, history);
		result = locator.nextAction(drone, history);
		expectedOp = Optional.empty();
		assertEquals(expectedOp.toString(), result.toString());

	}


	@Test
	public void testUTURNF() {
		drone_reference = new Drone(Direction.SOUTH,100000);
		setupUturnF();

		result = locator.nextAction(drone, history);
		expected = drone_reference.turnLeft();
		assertEquals(expected.toString(), result.get().toString());

		result = locator.nextAction(drone, history);
		expected = drone_reference.turnLeft();
		assertEquals(expected.toString(), result.get().toString());	
	}
}