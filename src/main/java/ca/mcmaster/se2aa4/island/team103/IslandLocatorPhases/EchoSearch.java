package ca.mcmaster.se2aa4.island.team103.IslandLocatorPhases;
import ca.mcmaster.se2aa4.island.team103.*;
import java.util.Optional;
import org.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ca.mcmaster.se2aa4.island.team103.Command;

public class EchoSearch implements Command {
    private Counter stage = new Counter();
    private Optional<JSONObject> decision;
    private Drone drone;
    private final Logger logger = LogManager.getLogger();

    public EchoSearch (Drone drone_in) {
        this.drone = drone_in;
    }

    public Optional<JSONObject> execute() {

        switch (this.stage.value()) {

            case 0:
                decision = Optional.of(drone.echoRight());
                break;

            case 1:
                decision = Optional.of(drone.echoForward());
                break;

            case 2:
                decision = Optional.of(drone.echoLeft());
                break;

            case 3:
                this.stage.reset();
                return Optional.empty();
        }
        this.stage.next();
        return decision;
    }
}
