package ca.mcmaster.se2aa4.island.team103;

import org.json.JSONObject;

public class Movements {

    public JSONObject flyForward(){
        JSONObject fly = new JSONObject();
        fly.put("action", "fly");
        return fly;
    }

    public JSONObject flyNorth() {
        JSONObject north = new JSONObject();
        JSONObject parameters = new JSONObject();
        parameters.put("direction", "N");
        north.put("action", "fly");
        north.put("parameters", parameters); 
        return north;
    }

    public JSONObject flySouth() {
        JSONObject south = new JSONObject();
        JSONObject parameters = new JSONObject();
        parameters.put("direction", "S");
        south.put("action", "fly");
        south.put("parameters", parameters); 
        return south;
    }

    public JSONObject flyEast() {
        JSONObject east = new JSONObject();
        JSONObject parameters = new JSONObject();
        parameters.put("direction", "E");
        east.put("action", "fly");
        east.put("parameters", parameters); 
        return east;
    }

    public JSONObject flyWest() {
        JSONObject west = new JSONObject();
        JSONObject parameters = new JSONObject();
        parameters.put("direction", "W");
        west.put("action", "fly");
        west.put("parameters", parameters); 
        return west;
    }

    public void UTurn(){
        // call of 2x left turn or 2x right turn
    }
}
