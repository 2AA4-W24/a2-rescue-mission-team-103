package ca.mcmaster.se2aa4.island.team103.islandLocating;
import ca.mcmaster.se2aa4.island.team103.drone.Drone;
import ca.mcmaster.se2aa4.island.team103.history.History;

import org.json.JSONObject;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TravelToEnd implements Command {
    private Counter stage = new Counter();
    private Drone drone;
    private History<JSONObject> history;
    private final Logger logger = LogManager.getLogger();
    private int distance = 0;

    public TravelToEnd(Drone drone_in, History<JSONObject> history_in) {
        this.drone = drone_in;
        this.history = history_in;
    }

    public Optional<JSONObject> execute() {
        // Turns right, then flies until it reaches the edge of the map.
        Optional<JSONObject> decision;
        JSONObject last_result;

        switch (this.stage.value()) {
            case 0:
                decision = Optional.of(drone.turnRight());
                break;

            case 1:
                decision = Optional.of(drone.echoForward());
                break;

            case 2:
                last_result = this.history.getLast();
                distance = last_result.getJSONObject("extras").getInt("range");

                if(distance <= 1) {
                    logger.info("Exiting TravelToEnd -> Returning empty, dist <= 1");
                    this.stage.reset();
                    return Optional.empty();
                } else {
                    decision = Optional.of(drone.flyForwards());
                }
                break;

            default:
                if(this.stage.value() - 1 < distance) {
                    decision = Optional.of(drone.flyForwards());
                } else {
                    logger.info("Exiting TravelToEnd -> Returning empty");
                    this.stage.reset();
                    return Optional.empty();
                }
                break;
        }
        this.stage.next();
        return decision;
    }
}