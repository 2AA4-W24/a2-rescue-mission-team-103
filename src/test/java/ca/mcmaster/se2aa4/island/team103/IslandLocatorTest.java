package ca.mcmaster.se2aa4.island.team103;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.json.JSONObject;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class IslandLocatorTest {

	JSONObject detection = new JSONObject();
	JSONObject expected = new JSONObject();
	ResponseHistory history = new ResponseHistory();
	Drone drone = new Drone(Direction.EAST);
	IslandLocator locator = new IslandLocator();
	Optional<JSONObject> result;
	JSONObject last_history;
	JSONObject extras;
	Logger logger = LogManager.getLogger();

	@BeforeEach
	public void reset() {
		detection = new JSONObject();
		expected = new JSONObject();
		history = new ResponseHistory();
		drone = new Drone(Direction.EAST);
		locator = new IslandLocator();
	}
	/* 
	@Test
	public void testSearch() {
		result = locator.locate(drone, history, Direction.EAST);

		expected = drone.scanRight();
		assertTrue(result.isPresent());
		assertEquals(expected.toString(), result.get().toString());

		last_history = new JSONObject();
		extras = new JSONObject();
		extras.put("found", "OUT_OF_RANGE");
		extras.put("range", 10);
		last_history.put("extras", extras);
		history.addItem(last_history);
		
		result = locator.locate(drone, history, Direction.EAST);

		expected = drone.scanForward();
		assertTrue(result.isPresent());
		assertEquals(expected.toString(), result.get().toString());

		result = locator.locate(drone, history, Direction.EAST);

		expected = drone.scanLeft();
		assertTrue(result.isPresent());
		assertEquals(expected.toString(), result.get().toString());

		result = locator.locate(drone, history, Direction.EAST);

		expected = drone.flyForwards();
		assertTrue(result.isPresent());
		assertEquals(expected.toString(), result.get().toString());
	} */

	@Test
	public void testFoundR() {
		logger.info("Started TestFoundR");
		last_history = new JSONObject();
		extras = new JSONObject();
		extras.put("found", "GROUND");
		extras.put("range", 10);
		last_history.put("extras", extras);
		history.addItem(last_history);

		result = locator.locate(drone, history, Direction.EAST);

		expected = drone.turnRight();
		assertTrue(result.isPresent());
		assertEquals(expected.toString(), result.get().toString());
	}

	@Test
	public void testFoundF() {
		logger.info("started testfoundF");
		result = locator.locate(drone, history, Direction.EAST);

		last_history = new JSONObject();
		extras = new JSONObject();
		extras.put("found", "GROUND");
		extras.put("range", 2);
		last_history.put("extras", extras);
		history.addItem(last_history);

		result = locator.locate(drone, history, Direction.EAST);

		expected = drone.flyForwards();
		assertTrue(result.isPresent());
		assertEquals(expected.toString(), result.get().toString());

	}

	@Test
	public void testFoundL() {
		logger.info("started test found L");
		result = locator.locate(drone, history, Direction.EAST);

		last_history = new JSONObject();
		extras = new JSONObject();
		extras.put("found", "OUT_OF_RANGE");
		extras.put("range", 10);
		last_history.put("extras", extras);
		history.addItem(last_history);
		
		result = locator.locate(drone, history, Direction.EAST);

		last_history = new JSONObject();
		extras = new JSONObject();
		extras.put("found", "GROUND");
		extras.put("range", 10);
		last_history.put("extras", extras);
		history.addItem(last_history);

		result = locator.locate(drone, history, Direction.EAST);

		expected = drone.turnLeft();
		assertTrue(result.isPresent());
		assertEquals(expected.toString(), result.get().toString());
	}
}