package ca.mcmaster.se2aa4.island.team103;

import java.util.Optional;
import org.json.JSONObject;

public interface DroneController {
	public Optional<JSONObject> nextAction();
}