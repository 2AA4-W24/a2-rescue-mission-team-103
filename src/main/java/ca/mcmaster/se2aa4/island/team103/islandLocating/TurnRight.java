package ca.mcmaster.se2aa4.island.team103.islandLocating;
import ca.mcmaster.se2aa4.island.team103.drone.Drone;

import java.util.Optional;
import org.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TurnRight implements Command {
    private Counter stage = new Counter();
    private JSONObject decision;
    private Drone drone;
    private final Logger logger = LogManager.getLogger();

    public TurnRight(Drone drone_in) {
        this.drone = drone_in;
    }

    public Optional<JSONObject> execute() {
        // Performs on-spot right turn via Right->Right->Forwards->Right->Right->Right
        
        switch (this.stage.value()) {
            case 0:
                this.decision = this.drone.turnRight();
                break;
            case 1:
                this.decision = this.drone.flyForwards();
                break;
            case 2, 3:
                this.decision = this.drone.turnRight();
                break;
            case 4:
                this.decision = this.drone.turnRight();
                break;
            case 5:
                logger.info("TurnRight Complete, returning empty");
                this.stage.reset();
                return Optional.empty();
            default:
                logger.error("Stage outside acceptable range");
            
        }
        this.stage.next();
        return Optional.of(decision);
    }
    
}
