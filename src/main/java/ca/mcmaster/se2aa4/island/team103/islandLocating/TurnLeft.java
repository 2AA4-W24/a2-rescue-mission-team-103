package ca.mcmaster.se2aa4.island.team103.islandLocating;
import ca.mcmaster.se2aa4.island.team103.drone.Drone;

import java.util.Optional;
import org.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TurnLeft implements Command {
    private Counter stage = new Counter();
    private JSONObject decision;
    private Drone drone;
    private final Logger logger = LogManager.getLogger();

    public TurnLeft(Drone drone_in) {
        this.drone = drone_in;
    }

    public Optional<JSONObject> execute() {
        // Performs an on-spot left turn via Left->Left->Forwards->Left->Left->Left
        switch (this.stage.value()) {
            case 0, 1:
                this.decision = this.drone.turnLeft();
                break;
            case 2:
                this.decision = this.drone.flyForwards();
                break;
            case 3, 4:
                this.decision = this.drone.turnLeft();
                break;
            case 5:
                this.decision = this.drone.turnLeft();
                break;
            case 6:
                logger.info("TurnLeft Complete, returning empty");
                stage.reset();
                return Optional.empty();
            default:
                logger.error("Stage outside acceptable range");
        }
        this.stage.next();
        return Optional.of(decision);
    }
    
}
