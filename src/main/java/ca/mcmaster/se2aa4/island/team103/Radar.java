package ca.mcmaster.se2aa4.island.team103;

import org.json.JSONObject;

public class Radar {

	public JSONObject scanForward(Direction current_heading){
		/*  Returns JSON required for scanning forwards - requires current heading as argument */
		JSONObject scan = new JSONObject();
		JSONObject parameters = new JSONObject();
		switch(current_heading) {
			case Direction.NORTH:
				parameters.put("direction", "N");
				break;
			case Direction.EAST:
				parameters.put("direction", "E");
				break;
			case Direction.SOUTH:
				parameters.put("direction", "S");
				break;
			case Direction.WEST:
				parameters.put("direction", "W");
				break;
		}
		scan.put("action", "echo");
		scan.put("parameters", parameters);
		return scan;
	}

	public JSONObject scanLeft(Direction current_heading){
		/*  Returns JSON required for scanning left - requires current heading as argument */
		JSONObject scan = new JSONObject();
		JSONObject parameters = new JSONObject();
		switch(current_heading) {
			case Direction.NORTH:
				parameters.put("direction", "W");
				break;
			case Direction.EAST:
				parameters.put("direction", "N");
				break;
			case Direction.SOUTH:
				parameters.put("direction", "E");
				break;
			case Direction.WEST:
				parameters.put("direction", "S");
				break;
		}
		scan.put("action", "echo");
		scan.put("parameters", parameters);
		return scan;
	}

	public JSONObject scanRight(Direction current_heading){
		/*  Returns JSON required for scanning right - requires current heading as argument */
		JSONObject scan = new JSONObject();
		JSONObject parameters = new JSONObject();
		switch(current_heading) {
			case Direction.NORTH:
				parameters.put("direction", "E");
				break;
			case Direction.EAST:
				parameters.put("direction", "S");
				break;
			case Direction.SOUTH:
				parameters.put("direction", "W");
				break;
			case Direction.WEST:
				parameters.put("direction", "N");
				break;
		}
		scan.put("action", "echo");
		scan.put("parameters", parameters);
		return scan;
	}
}
