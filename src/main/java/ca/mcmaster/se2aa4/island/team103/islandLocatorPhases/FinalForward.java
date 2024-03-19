package ca.mcmaster.se2aa4.island.team103.islandLocatorPhases;
import ca.mcmaster.se2aa4.island.team103.*;
import org.json.JSONObject;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FinalForward implements Command {
    private Counter stage = new Counter();
    private Drone drone;
    private History<JSONObject> history;
    private final Logger logger = LogManager.getLogger();
    private int distance = 0;

    public FinalForward(Drone drone_in, History<JSONObject> history_in) {
        this.drone = drone_in;
        this.history = history_in;
    }

    public Optional<JSONObject> execute() {
        Optional<JSONObject> decision;
        JSONObject last_result;

        switch (this.stage.value()) {
            case 0:
                decision = Optional.of(this.drone.echoForward());
                break;

            case 1:
                last_result = this.history.getLast();
                distance = last_result.getJSONObject("extras").getInt("range");
                if(distance == 0) {
                    logger.info("Exiting FINAL_FRWD -> Returning empty (dist == 0 immediately after uturn)");
                    this.stage.reset();
                    return Optional.empty();
                } else {
                    decision = Optional.of(this.drone.flyForwards());
                }
                break;

            default:
                if(this.stage.value() < distance){
                    decision = Optional.of(drone.flyForwards());
                } else {
                    logger.info("Exiting FINAL_FRWD -> Returning empty");
                    this.stage.reset();
                    return Optional.empty();
                }
                break;
        }

        this.stage.next();
        return decision;
    }
}
