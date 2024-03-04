package ca.mcmaster.se2aa4.island.team103;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.json.JSONObject;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class IslandScannerTest {

	JSONObject detection;
	JSONObject expected;
	Optional<JSONObject> expectedOp;
	ResponseHistory history;
	Drone drone;
	Drone drone_reference;
	IslandRecon scanner;
	Optional<JSONObject> result;
	JSONObject last_history;
	JSONObject extras;
	final Logger logger = LogManager.getLogger();

	@BeforeEach
	public void reset() {
		detection = new JSONObject();
		expected = new JSONObject();
		history = new ResponseHistory();
		drone = new Drone(Direction.EAST);
		drone_reference = new Drone(Direction.EAST);
		scanner = new IslandRecon();
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
	/*
	@Test
	public void specialTurn() {
		scanner.islandScan(drone,history);
		addOORtoHistory();
		expected = drone_reference.turnRight();
		result = scanner.islandScan(drone,history);
		assertTrue(result.isPresent());
		assertEquals(expected.toString(), result.get().toString());
	}
	*/
}