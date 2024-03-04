package ca.mcmaster.se2aa4.island.team103;
import org.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Drone {

    private Movements controls = new Movements();
    private Radar radar = new Radar();
    private Direction heading;
    private ActionUsage actions = new ActionUsage();
    private Battery battery;
    private Logger logger = LogManager.getLogger();

    public Drone(Direction start_heading, int battery_level) {
        // Initializes starting heading
        heading = start_heading;
        battery = new Battery(battery_level);
    }

    public void logCost(int cost) {
        battery.log(cost);
        logger.info("Battery Remaining: {}", battery.getBattery());
    }

    public JSONObject flyForwards() {
        actions.log(Action.FORWARD);
        if (battery.canContinue()) {
            return controls.flyForward();
        } else {
            logger.error("OUT OF BATTERY");
            logger.info(actions.getSummary());
            return controls.stop();
        }
    }

    public JSONObject turnRight() {
        actions.log(Action.TRIGHT);

        if (battery.canContinue()) {

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

        } else {
            logger.error("OUT OF BATTERY");
            logger.info(actions.getSummary());
            return controls.stop();
        }
    }

    public JSONObject turnLeft() {
        actions.log(Action.TLEFT);

        if (battery.canContinue()) {

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

        } else {
            logger.error("OUT OF BATTERY");
            logger.info(actions.getSummary());
            return controls.stop();
        }
    }

    public JSONObject scan() {
        actions.log(Action.SCAN);
        if (battery.canContinue()) {
            return radar.scan();
        } else {
            logger.error("OUT OF BATTERY");
            logger.info(actions.getSummary());
            return controls.stop();
        }
    }

	public JSONObject scanLeft(){
        actions.log(Action.ECHO_LEFT);
		if (battery.canContinue()) {
            return radar.scanLeft(heading);
        } else {
            logger.error("OUT OF BATTERY");
            logger.info(actions.getSummary());
            return controls.stop();
        }
	}

	public JSONObject scanRight(){
        actions.log(Action.ECHO_RIGHT);
		if (battery.canContinue()) {
            return radar.scanRight(heading);
        } else {
            logger.error("OUT OF BATTERY");
            logger.info(actions.getSummary());
            return controls.stop();
        }
	}

	public JSONObject scanForward(){
        actions.log(Action.ECHO_FORWARD);
		if (battery.canContinue()) {
            return radar.scanForward(heading);
        } else {
            logger.error("OUT OF BATTERY");
            logger.info(actions.getSummary());
            return controls.stop();
        }
	}

    public JSONObject stop() {
        logger.info("Stopping");
        logger.info(actions.getSummary());
        return controls.stop();
    }

	public Direction getHeading(){
		return this.heading;
	}
}
