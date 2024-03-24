package ca.mcmaster.se2aa4.island.team103.drone;

import java.util.Optional;
import org.json.JSONObject;

// Interace for island finding / island scanning classes.
public interface DroneController {
	public Optional<JSONObject> nextAction();
}
