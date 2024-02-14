package ca.mcmaster.se2aa4.island.team103;
import org.json.JSONObject;


public class Drone {

    Movements controls = new Movements();
    Radar radar = new Radar();
    Direction heading = Direction.EAST; //CHANGE -- Must be instantiated as the actual start heading returned by the game

    public JSONObject flyForwards() {
        return controls.flyForward();
    }

    public JSONObject turnRight() {
        if(heading == Direction.NORTH) {
            heading = Direction.EAST;
            return controls.flyEast();
        } else if (heading == Direction.WEST) {
            heading = Direction.NORTH;
            return controls.flyNorth();
        } else if (heading == Direction.SOUTH) {
            heading = Direction.WEST;
            return controls.flyWest();
        } else {
            heading = Direction.SOUTH;
            return controls.flySouth();
        }
        
    }

    public JSONObject turnLeft() {
        if(heading == Direction.NORTH) {
            heading = Direction.WEST;
            return controls.flyWest();
        } else if (heading == Direction.WEST) {
            heading = Direction.SOUTH;
            return controls.flySouth();
        } else if (heading == Direction.SOUTH) {
            heading = Direction.EAST;
            return controls.flyEast();
        } else {
            heading = Direction.NORTH;
            return controls.flyNorth();
        }
        
    }
    
}
