package ca.mcmaster.se2aa4.island.team103.drone;

import org.json.JSONObject;

public class Movements {

	private static final String ACTION = "action";
	private static final String HEADING = "heading";
	private static final String DIRECTION = "direction";
	private static final String PARAMS = "parameters";

    public JSONObject flyForward(){
        JSONObject fly = new JSONObject();
        fly.put(ACTION, "fly");
        return fly;
    }

    public JSONObject flyNorth() {
        JSONObject north = new JSONObject();
        JSONObject parameters = new JSONObject();
        parameters.put(DIRECTION, "N");
        north.put(ACTION, HEADING);
        north.put(PARAMS, parameters); 
        return north;
    }

    public JSONObject flySouth() {
        JSONObject south = new JSONObject();
        JSONObject parameters = new JSONObject();
        parameters.put(DIRECTION, "S");
        south.put(ACTION, HEADING);
        south.put(PARAMS, parameters); 
        return south;
    }

    public JSONObject flyEast() {
        JSONObject east = new JSONObject();
        JSONObject parameters = new JSONObject();
        parameters.put(DIRECTION, "E");
        east.put(ACTION, HEADING);
        east.put(PARAMS, parameters); 
        return east;
    }

    public JSONObject flyWest() {
        JSONObject west = new JSONObject();
        JSONObject parameters = new JSONObject();
        parameters.put(DIRECTION, "W");
        west.put(ACTION, HEADING);
        west.put(PARAMS, parameters); 
        return west;
    }

    public JSONObject stop() {
        JSONObject output = new JSONObject();
        output.put(ACTION, "stop");
        return output;
    }

}
