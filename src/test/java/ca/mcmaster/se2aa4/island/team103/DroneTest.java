package ca.mcmaster.se2aa4.island.team103;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.json.JSONObject;

class DroneTest {
	Drone drone;
	JSONObject expected;
	JSONObject result;
	JSONObject parameters;

	@BeforeEach
	public void setup() {
		expected = new JSONObject();
		parameters = new JSONObject();
	}

	@Test
	void TRightFacingE() {
		drone = new Drone(Direction.EAST, 100000);
		parameters.put("direction", "S");
		expected.put("action", "heading");
		expected.put("parameters", parameters);
		result = drone.turnRight();
		assertEquals(expected.toString(), result.toString());
	}

	@Test
	void TRightFacingS() {
		drone = new Drone(Direction.SOUTH,100000);
		parameters.put("direction", "W");
		expected.put("action", "heading");
		expected.put("parameters", parameters);
		result = drone.turnRight();
		assertEquals(expected.toString(), result.toString());
	}

	@Test
	void TRightFacingW() {
		drone = new Drone(Direction.WEST,100000);
		parameters.put("direction", "N");
		expected.put("action", "heading");
		expected.put("parameters", parameters);
		result = drone.turnRight();
		assertEquals(expected.toString(), result.toString());
	}

	@Test
	void TRightFacingN() {
		drone = new Drone(Direction.NORTH,100000);
		parameters.put("direction", "E");
		expected.put("action", "heading");
		expected.put("parameters", parameters);
		result = drone.turnRight();
		assertEquals(expected.toString(), result.toString());
	}

	@Test
	void TLeftFacingE() {
		drone = new Drone(Direction.EAST,100000);
		parameters.put("direction", "N");
		expected.put("action", "heading");
		expected.put("parameters", parameters);
		result = drone.turnLeft();
		assertEquals(expected.toString(), result.toString());	
	}

	@Test
	void TLeftFacingS() {
		drone = new Drone(Direction.SOUTH,100000);
		parameters.put("direction", "E");
		expected.put("action", "heading");
		expected.put("parameters", parameters);
		result = drone.turnLeft();
		assertEquals(expected.toString(), result.toString());
	}

	@Test
	void TLeftFacingW() {
		drone = new Drone(Direction.WEST,100000);
		parameters.put("direction", "S");
		expected.put("action", "heading");
		expected.put("parameters", parameters);
		result = drone.turnLeft();
		assertEquals(expected.toString(), result.toString());
	}
	
	@Test
	void TLeftFacingN() {
		drone = new Drone(Direction.NORTH,100000);
		parameters.put("direction", "W");
		expected.put("action", "heading");
		expected.put("parameters", parameters);
		result = drone.turnLeft();
		assertEquals(expected.toString(), result.toString());
	}

	@Test
	void Forwards() {
		drone = new Drone(Direction.NORTH,100000);
		expected.put("action", "fly");
		result = drone.flyForwards();
		assertEquals(expected.toString(), result.toString());
	}

	@Test
	void scan() {
		drone = new Drone(Direction.NORTH,100000);	
		expected.put("action", "scan");
		result = drone.scan();
		assertEquals(expected.toString(), result.toString());
	}

	@Test
	void echoLeft() {
		drone = new Drone(Direction.NORTH,100000);
		parameters.put("direction", "W");
		expected.put("action", "echo");
		expected.put("parameters", parameters);
		result = drone.echoLeft();
		assertEquals(expected.toString(), result.toString());
	}

	@Test
	void echoRight() {
		drone = new Drone(Direction.NORTH,100000);
		parameters.put("direction", "E");
		expected.put("action", "echo");
		expected.put("parameters", parameters);
		result = drone.echoRight();
		assertEquals(expected.toString(), result.toString());
	}

	@Test
	void echoForw() {
		drone = new Drone(Direction.NORTH,100000);
		parameters.put("direction", "N");
		expected.put("action", "echo");
		expected.put("parameters", parameters);
		result = drone.echoForward();
		assertEquals(expected.toString(), result.toString());
	}

	@Test
	void outOfBatteryFF() {
		drone = new Drone(Direction.NORTH,50);
		expected.put("action", "stop");
		result = drone.flyForwards();
		assertEquals(expected.toString(), result.toString());
	}

	@Test
	void outOfBatteryTR() {
		drone = new Drone(Direction.NORTH,50);
		expected.put("action", "stop");
		result = drone.turnRight();
		assertEquals(expected.toString(), result.toString());
	}

	@Test
	void outOfBatteryEF() {
		drone = new Drone(Direction.NORTH,50);
		expected.put("action", "stop");
		result = drone.echoForward();
		assertEquals(expected.toString(), result.toString());
	}

	@Test
	void outOfBatterySC() {
		drone = new Drone(Direction.NORTH,50);
		expected.put("action", "stop");
		result = drone.scan();
		assertEquals(expected.toString(), result.toString());
	}

	@Test
	void almostOutOfBatteryFF() {
		drone = new Drone(Direction.NORTH,51);
		expected.put("action", "fly");
		result = drone.flyForwards();
		assertEquals(expected.toString(), result.toString());
	}

	@Test
	void almostOutOfBatteryTR() {
		drone = new Drone(Direction.NORTH,51);
		parameters.put("direction", "E");
		expected.put("action", "heading");
		expected.put("parameters", parameters);
		result = drone.turnRight();
		assertEquals(expected.toString(), result.toString());
	}

	@Test
	void almostOutOfBatteryEF() {
		drone = new Drone(Direction.NORTH,51);
		parameters.put("direction", "N");
		expected.put("action", "echo");
		expected.put("parameters", parameters);
		result = drone.echoForward();
		assertEquals(expected.toString(), result.toString());
	}

	@Test
	void almostOutOfBatterySC() {
		drone = new Drone(Direction.NORTH,51);
		expected.put("action", "scan");
		result = drone.scan();
		assertEquals(expected.toString(), result.toString());
	}

}
