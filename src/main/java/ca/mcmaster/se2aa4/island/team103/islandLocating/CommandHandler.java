package ca.mcmaster.se2aa4.island.team103.islandLocating;
import java.util.Optional;
import org.json.JSONObject;

public class CommandHandler {
    // Handles executing commands on behalf of IslandLocator
    private Command command;

    public void setCommand(Command command_in) {
        this.command = command_in;
    }

    public Optional<JSONObject> nextAction() {
        return command.execute();
    }
}
