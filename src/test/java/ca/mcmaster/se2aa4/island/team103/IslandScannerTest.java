package ca.mcmaster.se2aa4.island.team103;

import org.junit.jupiter.api.Test;

import ca.mcmaster.se2aa4.island.team103.drone.Direction;
import ca.mcmaster.se2aa4.island.team103.drone.Drone;
import ca.mcmaster.se2aa4.island.team103.drone.DroneController;
import ca.mcmaster.se2aa4.island.team103.history.Coordinate;
import ca.mcmaster.se2aa4.island.team103.history.History;
import ca.mcmaster.se2aa4.island.team103.history.ResponseHistory;
import ca.mcmaster.se2aa4.island.team103.islandScanning.IslandScanner;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.json.JSONObject;
import org.json.JSONArray;
import java.util.Optional;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class IslandScannerTest {

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
		expected = new JSONObject();
		history = new ResponseHistory();
		drone = new Drone(Direction.EAST, 100000);
		drone_reference = new Drone(Direction.EAST, 100000);
		scanner = new IslandScanner(drone, history);
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
	void firstEchoTest(){
		result = scanner.nextAction();
		expected = drone.echoForward();
		assertTrue(result.isPresent());
		assertEquals(expected.toString(), result.get().toString());
	}
	
	@Test
	void StartofSlice(){
		result = scanner.nextAction();
		addGroundtoHistory(1);
		result = scanner.nextAction();
		expected = drone.flyForwards();
		assertEquals(expected.toString(), result.get().toString());
	}
	
	@Test
	void moveSlice(){
		result = scanner.nextAction();
		addGroundtoHistory(1);
		result = scanner.nextAction();
		addOtherBiometoHistory();
		result = scanner.nextAction();
		result = scanner.nextAction();
		expected = drone.flyForwards();
		assertEquals(expected.toString(), result.get().toString());
	}
	
	@Test
	void SliceStartWithDistance(){
		result = scanner.nextAction();
		addGroundtoHistory(3);
		result = scanner.nextAction();
		expected = drone.flyForwards();
		assertEquals(expected.toString(), result.get().toString());
	}
    
	@Test
	void SliceWithDistance(){ // Case when land is not immediately present after turnaround.
		result = scanner.nextAction();
		addGroundtoHistory(3);
		result = scanner.nextAction();
		result = scanner.nextAction();
		result = scanner.nextAction();
		result = scanner.nextAction();
		expected = drone.scan();
		assertEquals(expected.toString(), result.get().toString());
	}

	
	@Test
	void turnwaitSliceComponent(){
		result = scanner.nextAction();
		addGroundtoHistory(1);
		result = scanner.nextAction();
		addOtherBiometoHistory();
		result = scanner.nextAction();
		result = scanner.nextAction();
		addOceanBiometoHistory();
		result = scanner.nextAction();
		result = scanner.nextAction();
		expected = drone_reference.turnRight();
		expected = drone_reference.echoLeft();
		assertEquals(expected.toString(), result.get().toString());
	}
	
	@Test
	void endSlice(){
		result = scanner.nextAction();
		addGroundtoHistory(1);
		result = scanner.nextAction();
		addOtherBiometoHistory();
		result = scanner.nextAction();
		result = scanner.nextAction();
		addOceanBiometoHistory();
		result = scanner.nextAction();
		result = scanner.nextAction();
		addOORtoHistory();
		result = scanner.nextAction();
		result = scanner.nextAction();
		
		expected = drone_reference.turnLeft();
		assertEquals(expected.toString(), result.get().toString());
	}
	
	@Test
	void UTurnEast(){ // Ensuring U-Turn logic is successful.
		result = scanner.nextAction();
		addGroundtoHistory(1);
		result = scanner.nextAction();
		addOtherBiometoHistory();
		result = scanner.nextAction();
		result = scanner.nextAction();
		addOceanBiometoHistory();
		result = scanner.nextAction();
		result = scanner.nextAction();
		addOORtoHistory();
		result = scanner.nextAction();
		result = scanner.nextAction();
		result = scanner.nextAction();
		
		expected = drone_reference.turnLeft();
		expected = drone_reference.turnLeft();
		assertEquals(expected.toString(), result.get().toString());
	}
    
	@Test
	void SpecialTurn(){
		//Setup instructions
		result = scanner.nextAction();
		addGroundtoHistory(1);
		result = scanner.nextAction();
		addOtherBiometoHistory();
		result = scanner.nextAction();
		result = scanner.nextAction();
		addOceanBiometoHistory();
		result = scanner.nextAction();
		result = scanner.nextAction();
		addOORtoHistory();
		result = scanner.nextAction();
		result = scanner.nextAction();
		result = scanner.nextAction();
		addOORtoHistory();
		result = scanner.nextAction();
		// Performing special turn
		
		List<Coordinate> oldHistory = drone.getNavHistory();
		Coordinate c1 = oldHistory.get(oldHistory.size()-1);
		
		result = scanner.nextAction();
		result = scanner.nextAction();
		addOORtoHistory();
		result = scanner.nextAction();
		result = scanner.nextAction();
		result = scanner.nextAction();
		result = scanner.nextAction();
		result = scanner.nextAction();
		
		List<Coordinate> newHistory = drone.getNavHistory();
		Coordinate c2 = newHistory.get(newHistory.size()-1);
		Coordinate reference = new Coordinate(c1.x(),c1.y()+1);
		assertEquals(c2.toString(),reference.toString());
	}
	
	@Test
	void SpecialTurnW(){ 

		drone = new Drone(Direction.WEST, 100000);
		scanner = new IslandScanner(drone, history);

		//Setup instructions
		result = scanner.nextAction();
		addGroundtoHistory(1);
		result = scanner.nextAction();
		addOtherBiometoHistory();
		result = scanner.nextAction();
		result = scanner.nextAction();
		addOceanBiometoHistory();
		result = scanner.nextAction();
		result = scanner.nextAction();
		addOORtoHistory();
		result = scanner.nextAction();
		result = scanner.nextAction();
		result = scanner.nextAction();
		addOORtoHistory();
		result = scanner.nextAction();
		// Performing special turn
		List<Coordinate> oldHistory = drone.getNavHistory();
		Coordinate c1 = oldHistory.get(oldHistory.size()-1);
		
		result = scanner.nextAction();
		result = scanner.nextAction();
		addOORtoHistory();
		result = scanner.nextAction();
		result = scanner.nextAction();
		result = scanner.nextAction();
		result = scanner.nextAction();
		result = scanner.nextAction();
		List<Coordinate> newHistory = drone.getNavHistory();
		Coordinate c2 = newHistory.get(newHistory.size()-1);
		Coordinate reference = new Coordinate(c1.x(),c1.y()+1);
		assertEquals(c2.toString(),reference.toString());
	}
	
	@Test
	void SpecialTurn2(){ 
		//Setup instructions
		result = scanner.nextAction();
		addGroundtoHistory(1);
		result = scanner.nextAction();
		addOtherBiometoHistory();
		result = scanner.nextAction();
		result = scanner.nextAction();
		addOceanBiometoHistory();
		result = scanner.nextAction();
		result = scanner.nextAction();
		addOORtoHistory();
		result = scanner.nextAction();
		result = scanner.nextAction();
		result = scanner.nextAction();
		addOORtoHistory();
		result = scanner.nextAction();

		// Performing special turn1
		List<Coordinate> oldHistory = drone.getNavHistory();
		Coordinate c1 = oldHistory.get(oldHistory.size()-1);
		result = scanner.nextAction();
		result = scanner.nextAction();
		addOORtoHistory();
		result = scanner.nextAction();
		result = scanner.nextAction();
		result = scanner.nextAction();
		result = scanner.nextAction();
		result = scanner.nextAction();

		// Performing special turn2
		addOORtoHistory();
		result = scanner.nextAction();
		result = scanner.nextAction();
		addOORtoHistory();
		result = scanner.nextAction();
		result = scanner.nextAction();
		result = scanner.nextAction();
		result = scanner.nextAction();
		result = scanner.nextAction();

		List<Coordinate> newHistory = drone.getNavHistory();
		Coordinate c2 = newHistory.get(newHistory.size()-1);
		Coordinate reference = new Coordinate(c2.x(),c2.y()-3);
		assertEquals(c1.toString(),reference.toString());
	}
	
	@Test
	void SpecialTurn2West(){ 
		drone = new Drone(Direction.WEST, 100000);
		scanner = new IslandScanner(drone, history);
		//Setup instructions
		result = scanner.nextAction();
		addGroundtoHistory(1);
		result = scanner.nextAction();
		addOtherBiometoHistory();
		result = scanner.nextAction();
		result = scanner.nextAction();
		addOceanBiometoHistory();
		result = scanner.nextAction();
		result = scanner.nextAction();
		addOORtoHistory();
		result = scanner.nextAction();
		result = scanner.nextAction();
		result = scanner.nextAction();
		addOORtoHistory();
		result = scanner.nextAction();

		// Performing special turn1
		List<Coordinate> oldHistory = drone.getNavHistory();
		Coordinate c1 = oldHistory.get(oldHistory.size()-1);
		result = scanner.nextAction();
		result = scanner.nextAction();
		addOORtoHistory();
		result = scanner.nextAction();
		result = scanner.nextAction();
		result = scanner.nextAction();
		result = scanner.nextAction();
		result = scanner.nextAction();

		// Performing special turn2
		addOORtoHistory();
		result = scanner.nextAction();
		result = scanner.nextAction();
		addOORtoHistory();
		result = scanner.nextAction();
		result = scanner.nextAction();
		result = scanner.nextAction();
		result = scanner.nextAction();
		result = scanner.nextAction();

		List<Coordinate> newHistory = drone.getNavHistory();
		Coordinate c2 = newHistory.get(newHistory.size()-1);
		Coordinate reference = new Coordinate(c2.x(),c2.y()-3);
		assertEquals(c1.toString(),reference.toString());
	}
}