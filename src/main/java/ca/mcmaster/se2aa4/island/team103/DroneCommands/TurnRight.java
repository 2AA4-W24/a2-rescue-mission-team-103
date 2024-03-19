package ca.mcmaster.se2aa4.island.team103.DroneCommands;
import ca.mcmaster.se2aa4.island.team103.*;
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
        logger.info("Entering ShiftRight Decision: {}", this.stage.value());
        switch (this.stage.value()) {
            case 0:
                this.decision = this.drone.turnRight();
                break;
            case 1:
                this.decision = this.drone.flyForwards();
                break;
            case 2:
            case 3:
                this.decision = this.drone.turnRight();
                break;
            case 4:
                this.decision = this.drone.turnRight();
                break;
            case 5:
                logger.info("ShiftRight Complete, returning empty");
                this.stage.reset();
                return Optional.empty();
        }
        this.stage.next();
        return Optional.of(decision);
    }
    
}