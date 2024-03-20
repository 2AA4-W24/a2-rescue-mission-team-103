package ca.mcmaster.se2aa4.island.team103;

import org.json.JSONObject;

public class Radar {

	private static final String ACTION = "action";
	private static final String DIRECTION = "direction";
	private static final String PARAMS = "parameters";

	public JSONObject scan() {
		JSONObject scan = new JSONObject();
		scan.put(ACTION,"scan");
		return scan;
	}

	public JSONObject scanForward(Direction current_heading){
		/*  Returns JSON required for scanning forwards - requires current heading as argument */
		JSONObject scan = new JSONObject();
		JSONObject parameters = new JSONObject();
		switch(current_heading) {
			case Direction.NORTH:
				parameters.put(DIRECTION, "N");
				break;
			case Direction.EAST:
				parameters.put(DIRECTION, "E");
				break;
			case Direction.SOUTH:
				parameters.put(DIRECTION, "S");
				break;
			case Direction.WEST:
				parameters.put(DIRECTION, "W");
				break;
		}
		scan.put(ACTION, "echo");
		scan.put(PARAMS, parameters);
		return scan;
	}

	public JSONObject scanLeft(Direction current_heading){
		/*  Returns JSON required for scanning left - requires current heading as argument */
		JSONObject scan = new JSONObject();
		JSONObject parameters = new JSONObject();
		switch(current_heading) {
			case Direction.NORTH:
				parameters.put(DIRECTION, "W");
				break;
			case Direction.EAST:
				parameters.put(DIRECTION, "N");
				break;
			case Direction.SOUTH:
				parameters.put(DIRECTION, "E");
				break;
			case Direction.WEST:
				parameters.put(DIRECTION, "S");
				break;
		}
		scan.put(ACTION, "echo");
		scan.put(PARAMS, parameters);
		return scan;
	}

	public JSONObject scanRight(Direction current_heading){
		/*  Returns JSON required for scanning right - requires current heading as argument */
		JSONObject scan = new JSONObject();
		JSONObject parameters = new JSONObject();
		switch(current_heading) {
			case Direction.NORTH:
				parameters.put(DIRECTION, "E");
				break;
			case Direction.EAST:
				parameters.put(DIRECTION, "S");
				break;
			case Direction.SOUTH:
				parameters.put(DIRECTION, "W");
				break;
			case Direction.WEST:
				parameters.put(DIRECTION, "N");
				break;
		}
		scan.put(ACTION, "echo");
		scan.put(PARAMS, parameters);
		return scan;
	}
}
