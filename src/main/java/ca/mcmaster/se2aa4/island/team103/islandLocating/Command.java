package ca.mcmaster.se2aa4.island.team103.islandLocating;
import java.util.Optional;
import org.json.JSONObject;

public interface Command {
    public Optional<JSONObject> execute();
}
