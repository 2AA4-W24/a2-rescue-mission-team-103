package ca.mcmaster.se2aa4.island.team103.islandLocating;
import ca.mcmaster.se2aa4.island.team103.drone.Drone;

import java.util.Optional;
import org.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UturnLeft implements Command{
    private Counter stage = new Counter();
    private Drone drone;
    private JSONObject decision;
    private final Logger logger = LogManager.getLogger();

    public UturnLeft(Drone drone_in) {
        this.drone = drone_in;
    }
    
    public Optional<JSONObject> execute() {

        switch (this.stage.value()) {
            case 0:
                this.decision = this.drone.turnLeft();
                break;
            case 1:
                this.decision = this.drone.turnLeft();
                break;
            case 2:
                this.stage.reset();
                logger.info("ForwardUturn Complete, returning empty");
                return Optional.empty();
            default:
                logger.error("Stage outside acceptable range");
        }
        this.stage.next();
        return Optional.of(decision);
    }
}
