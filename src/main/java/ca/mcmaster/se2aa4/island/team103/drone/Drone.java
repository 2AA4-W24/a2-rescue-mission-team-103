package ca.mcmaster.se2aa4.island.team103.drone;
import org.json.JSONObject;

import ca.mcmaster.se2aa4.island.team103.history.Action;
import ca.mcmaster.se2aa4.island.team103.history.ActionUsage;
import ca.mcmaster.se2aa4.island.team103.history.Coordinate;
import ca.mcmaster.se2aa4.island.team103.history.NavHistory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.List;

public class Drone {

    private Movements controls = new Movements();
    private Radar radar = new Radar();
    private Direction heading;
    private ActionUsage actions = new ActionUsage();
    private Battery battery;
    private Logger logger = LogManager.getLogger();
	private Coordinate currentPos = new Coordinate(0,0);
	private NavHistory coordHistory = new NavHistory();
	private static final String BATTERYOUT = "OUT OF BATTERY";

    public Drone(Direction start_heading, int battery_level) {
        // Initializes starting heading
        heading = start_heading;
        battery = new Battery(battery_level);
        coordHistory.addItem(new Coordinate(0,0));
    }

    public void logCost(int cost) {
        battery.log(cost);
        logger.info("Battery Remaining: {}", battery.getBattery());
    }

    public JSONObject flyForwards() {
        actions.log(Action.FORWARD);
		switch(heading){
			case Direction.NORTH:
				currentPos = new Coordinate(currentPos.x(),currentPos.y()-1);
				coordHistory.addItem(currentPos);
				break;
			case Direction.EAST:
				currentPos = new Coordinate(currentPos.x()+1,currentPos.y());
				coordHistory.addItem(currentPos);
				break;
			case Direction.SOUTH:
				currentPos = new Coordinate(currentPos.x(),currentPos.y()+1);
				coordHistory.addItem(currentPos);
				break;
			case Direction.WEST:
				currentPos = new Coordinate(currentPos.x()-1,currentPos.y());
				coordHistory.addItem(currentPos);
				break;
		}
        if (battery.canContinue()) {
            return controls.flyForward();
        } else {
            logger.info(BATTERYOUT);
            logger.info(actions.getSummary());
            return controls.stop();
        }
    }

    public JSONObject turnRight() {
        actions.log(Action.TRIGHT);
        if (battery.canContinue()) {
            if(heading == Direction.NORTH) {
				currentPos = new Coordinate(currentPos.x()+1,currentPos.y()-1);
				coordHistory.addItem(currentPos);
                heading = Direction.EAST;
                return controls.flyEast();
            } else if (heading == Direction.WEST) {
				currentPos = new Coordinate(currentPos.x()-1,currentPos.y()-1);
				coordHistory.addItem(currentPos);
                heading = Direction.NORTH;
                return controls.flyNorth();
            } else if (heading == Direction.SOUTH) {
				currentPos = new Coordinate(currentPos.x()-1,currentPos.y()+1);
				coordHistory.addItem(currentPos);
                heading = Direction.WEST;
                return controls.flyWest();
            } else {
				currentPos = new Coordinate(currentPos.x()+1,currentPos.y()+1);
				coordHistory.addItem(currentPos);
                heading = Direction.SOUTH;
                return controls.flySouth();
            }

        } else {
            logger.info(BATTERYOUT);
            logger.info(actions.getSummary());
            return controls.stop();
        }
    }

    public JSONObject turnLeft() {
        actions.log(Action.TLEFT);

        if (battery.canContinue()) {

            if(heading == Direction.NORTH) {
				currentPos = new Coordinate(currentPos.x()-1,currentPos.y()-1);
				coordHistory.addItem(currentPos);
                heading = Direction.WEST;
                return controls.flyWest();
            } else if (heading == Direction.WEST) {
				currentPos = new Coordinate(currentPos.x()-1,currentPos.y()+1);
				coordHistory.addItem(currentPos);
                heading = Direction.SOUTH;
                return controls.flySouth();
            } else if (heading == Direction.SOUTH) {
				currentPos = new Coordinate(currentPos.x()+1,currentPos.y()+1);
				coordHistory.addItem(currentPos);
                heading = Direction.EAST;
                return controls.flyEast();
            } else {
				currentPos = new Coordinate(currentPos.x()+1,currentPos.y()-1);
				coordHistory.addItem(currentPos);
                heading = Direction.NORTH;
                return controls.flyNorth();
            }

        } else {
            logger.info(BATTERYOUT);
            logger.info(actions.getSummary());
            return controls.stop();
        }
    }

    public JSONObject scan() {
		coordHistory.addItem(new Coordinate(currentPos.x(),currentPos.y()));
        actions.log(Action.SCAN);
        if (battery.canContinue()) {
            return radar.scan();
        } else {
            logger.info(BATTERYOUT);
            logger.info(actions.getSummary());
            return controls.stop();
        }
    }

	public JSONObject echoLeft(){
		coordHistory.addItem(new Coordinate(currentPos.x(),currentPos.y()));
        actions.log(Action.ECHO_LEFT);
		if (battery.canContinue()) {
            return radar.scanLeft(heading);
        } else {
            logger.info(BATTERYOUT);
            logger.info(actions.getSummary());
            return controls.stop();
        }
	}

	public JSONObject echoRight(){
		coordHistory.addItem(new Coordinate(currentPos.x(),currentPos.y()));
        actions.log(Action.ECHO_RIGHT);
		if (battery.canContinue()) {
            return radar.scanRight(heading);
        } else {
            logger.info(BATTERYOUT);
            logger.info(actions.getSummary());
            return controls.stop();
        }
	}

	public JSONObject echoForward(){
		coordHistory.addItem(new Coordinate(currentPos.x(),currentPos.y()));
        actions.log(Action.ECHO_FORWARD);
		if (battery.canContinue()) {
            return radar.scanForward(heading);
        } else {
            logger.info(BATTERYOUT);
            logger.info(actions.getSummary());
            return controls.stop();
        }
	}

    public JSONObject stop() {
		coordHistory.addItem(new Coordinate(currentPos.x(),currentPos.y()));
        logger.info("Stopping");
        logger.info(actions.getSummary());
        return controls.stop();
    }

	public Direction getHeading(){
		return this.heading;
	}

    public List<Coordinate> getNavHistory() {
        return coordHistory.getItems(0);
    }
}
