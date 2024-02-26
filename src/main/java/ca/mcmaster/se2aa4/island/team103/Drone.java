package ca.mcmaster.se2aa4.island.team103;
import org.json.JSONObject;

public class Drone {

    private Movements controls = new Movements();
    private Radar radar = new Radar();
    private Direction heading;

    public Drone(Direction start_heading) {
        // Initializes starting heading
        heading = start_heading;
    }

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

    public JSONObject scan() {
        return radar.scan();
    }

	public JSONObject scanLeft(){
		return radar.scanLeft(heading);
	}

	public JSONObject scanRight(){
		return radar.scanRight(heading);
	}

	public JSONObject scanForward(){
		return radar.scanForward(heading);
	}

	public Direction getHeading(){
		return this.heading;
	}
}
