package ca.mcmaster.se2aa4.island.team103;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.json.JSONObject;
import org.json.JSONArray;
import java.util.Optional;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class IslandScannerTest {

	JSONObject detection;
	JSONObject expected;
	Optional<JSONObject> expectedOp;
	History<JSONObject> history;
	Drone drone;
	Drone drone_reference;
	DroneController scanner;
	Optional<JSONObject> result;
	JSONObject last_history;
	JSONObject extras;
	final static Logger logger = LogManager.getLogger();

	@BeforeEach
	public void reset() {
		detection = new JSONObject();
		expected = new JSONObject();
		history = new ResponseHistory();
		drone = new Drone(Direction.EAST, 100000);
		drone_reference = new Drone(Direction.EAST, 100000);
		scanner = new IslandScanner();
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
	
	public void addOceanBiometoHistory(){
		last_history = new JSONObject();
		extras = new JSONObject();
		JSONArray biomes = new JSONArray();
		biomes.put("OCEAN");
		extras.put("biomes",biomes);
		last_history.put("extras",extras);
		history.addItem(last_history);
	}

	public void addOtherBiometoHistory(){
		last_history = new JSONObject();
		extras = new JSONObject();
		JSONArray biomes = new JSONArray();
		biomes.put("TUNDRA");
		biomes.put("GRASSLAND");
		extras.put("biomes",biomes);
		last_history.put("extras",extras);
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

	@Test
	public void firstEchoTest(){
		result = scanner.nextAction(drone, history);
		expected = drone.echoForward();
		assertTrue(result.isPresent());
		assertEquals(expected.toString(), result.get().toString());
	}

	@Test
	public void StartofSlice(){
		result = scanner.nextAction(drone,history);
		addGroundtoHistory();
		result = scanner.nextAction(drone,history);
		expected = drone.scan();
		assertEquals(expected.toString(), result.get().toString());
	}

	@Test
	public void moveSlice(){
		result = scanner.nextAction(drone,history);
		addGroundtoHistory();
		result = scanner.nextAction(drone,history);
		addOtherBiometoHistory();
		result = scanner.nextAction(drone,history);
		expected = drone.flyForwards();
		assertEquals(expected.toString(), result.get().toString());
	}

	@Test
	public void turnwaitSliceComponent(){
		result = scanner.nextAction(drone,history);
		addGroundtoHistory();
		result = scanner.nextAction(drone,history);
		addOtherBiometoHistory();
		result = scanner.nextAction(drone,history);
		result = scanner.nextAction(drone,history);
		addOceanBiometoHistory();
		result = scanner.nextAction(drone,history);
		expected = drone_reference.turnRight();
		expected = drone_reference.echoLeft();
		assertEquals(expected.toString(), result.get().toString());
	}

	@Test
	public void endSlice(){
		result = scanner.nextAction(drone,history);
		addGroundtoHistory();
		result = scanner.nextAction(drone,history);
		addOtherBiometoHistory();
		result = scanner.nextAction(drone,history);
		result = scanner.nextAction(drone,history);
		addOceanBiometoHistory();
		result = scanner.nextAction(drone,history);
		addOORtoHistory();
		result = scanner.nextAction(drone,history);
		result = scanner.nextAction(drone,history);
		expected = drone_reference.turnLeft();
		
		logger.info(history.getLast());
		assertEquals(expected.toString(), result.get().toString());
	}

	@Test
	public void UTurnEast(){ // Ensuring U-Turn logic is successful.
		result = scanner.nextAction(drone,history);
		addGroundtoHistory();
		result = scanner.nextAction(drone,history);
		addOtherBiometoHistory();
		result = scanner.nextAction(drone,history);
		result = scanner.nextAction(drone,history);
		addOceanBiometoHistory();
		result = scanner.nextAction(drone,history);
		addOORtoHistory();
		result = scanner.nextAction(drone,history);
		result = scanner.nextAction(drone,history);
		result = scanner.nextAction(drone,history);

		expected = drone_reference.turnLeft();
		expected = drone_reference.turnLeft();

		assertEquals(expected.toString(), result.get().toString());
	}

	@Test
	public void SpecialTurn(){ 
		//Setup instructions
		result = scanner.nextAction(drone,history);
		addGroundtoHistory();
		result = scanner.nextAction(drone,history);
		addOtherBiometoHistory();
		result = scanner.nextAction(drone,history);
		result = scanner.nextAction(drone,history);
		addOceanBiometoHistory();
		result = scanner.nextAction(drone,history);
		addOORtoHistory();
		result = scanner.nextAction(drone,history);
		result = scanner.nextAction(drone,history);
		result = scanner.nextAction(drone,history);
		addOORtoHistory();
		result = scanner.nextAction(drone,history);
		// Performing special turn
		List<Coordinate> oldHistory = drone.getNavHistory();
		Coordinate c1 = oldHistory.get(oldHistory.size()-1);
		result = scanner.nextAction(drone,history);
		result = scanner.nextAction(drone,history);
		addOORtoHistory();

		result = scanner.nextAction(drone,history);
		result = scanner.nextAction(drone,history);
		result = scanner.nextAction(drone,history);
		result = scanner.nextAction(drone,history);
		result = scanner.nextAction(drone,history);
		List<Coordinate> newHistory = drone.getNavHistory();
		Coordinate c2 = newHistory.get(newHistory.size()-1);
		Coordinate reference = new Coordinate(c2.x(),c2.y()-1);
		assertEquals(c1.toString(),reference.toString());
	}
	
}