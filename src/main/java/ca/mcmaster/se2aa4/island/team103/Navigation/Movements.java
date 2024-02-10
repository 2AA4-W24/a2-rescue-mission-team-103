package ca.mcmaster.se2aa4.island.team103.Navigation;

import org.json.JSONObject;

enum Direction {
    NORTH,
    WEST,
    SOUTH,
    EAST
}

public class Movements {

    public JSONObject flyForward(){
        JSONObject fly = new JSONObject();
        fly.put("action", "fly");
        return fly;
    }

    public JSONObject leftTurn(Direction current_heading){
        // Returns JSONObject required for turning left - requires the current heading as argument
        JSONObject leftTurn = new JSONObject();
        JSONObject parameters = new JSONObject();
        if(current_heading == Direction.NORTH) {
            parameters.put("direction", "W");
        } else if (current_heading == Direction.WEST) {
            parameters.put("direction", "S");
        } else if (current_heading == Direction.SOUTH) {
            parameters.put("direction", "E");
        } else if (current_heading == Direction.EAST) {
            parameters.put("direction", "N");
        }
        leftTurn.put("action", "fly");
        leftTurn.put("parameters", parameters);
        return leftTurn;
    }

    public JSONObject rightTurn(Direction current_heading){
        // Returns JSONObject required for turning right - requires the current heading as argument
        JSONObject rightTurn = new JSONObject();
        JSONObject parameters = new JSONObject();
        if(current_heading == Direction.NORTH) {
            parameters.put("direction", "E");
        } else if (current_heading == Direction.WEST) {
            parameters.put("direction", "N");
        } else if (current_heading == Direction.SOUTH) {
            parameters.put("direction", "W");
        } else if (current_heading == Direction.EAST) {
            parameters.put("direction", "S");
        }
        rightTurn.put("action", "fly");
        rightTurn.put("parameters", parameters);
        return rightTurn;
    }

    public void UTurn(){
        // call of 2x left turn or 2x right turn
    }
}
