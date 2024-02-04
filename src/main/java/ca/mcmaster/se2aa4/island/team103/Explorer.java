package ca.mcmaster.se2aa4.island.team103;

import java.io.StringReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.ace_design.island.bot.IExplorerRaid;
import org.json.JSONObject;
import org.json.JSONTokener;

public class Explorer implements IExplorerRaid {

    private final Logger logger = LogManager.getLogger();
    public int counter = 0;
    boolean clear = true;
    boolean scanned = true;
    boolean moved = false;

    @Override
    public void initialize(String s) {
        logger.info("** Initializing the Exploration Command Center");
        JSONObject info = new JSONObject(new JSONTokener(new StringReader(s)));
        logger.info("** Initialization info:\n {}",info.toString(2));
        String direction = info.getString("heading");
        Integer batteryLevel = info.getInt("budget");
        logger.info("The drone is facing {}", direction);
        logger.info("Battery level is {}", batteryLevel);
    }

    @Override
    public String takeDecision() {
        JSONObject decision = new JSONObject();
        logger.info(String.valueOf(clear) + " - " + String.valueOf(scanned));
        if (clear & scanned & !moved) {
            decision.put("action", "fly");
            scanned = false;
            moved = true;
        } else if (!scanned) {
            scanned =  true;
            JSONObject parameters = new JSONObject();
            parameters.put("direction", "E");
            decision.put("action", "echo");
            decision.put("parameters", parameters);
        } else if (moved) {
            decision.put("action", "scan");
            moved = false;
        } else {
            decision.put("action", "stop");
        }

        counter++;
        logger.info("** Decision: {}",decision.toString());
        return decision.toString();
    }

    @Override
    public void acknowledgeResults(String s) {
        JSONObject response = new JSONObject(new JSONTokener(new StringReader(s)));
        logger.info("** Response received:\n"+response.toString(2));
        Integer cost = response.getInt("cost");
        logger.info("The cost of the action was {}", cost);
        String status = response.getString("status");
        logger.info("The status of the drone is {}", status);
        JSONObject extraInfo = response.getJSONObject("extras");
        logger.info("Additional information received: {}", extraInfo);
        try {
            Integer range = extraInfo.getInt("range");
            if (range == 0) {
                clear = false;
            } else {
                clear = true;
            }
        } catch (Exception e) {

        }
    }

    @Override
    public String deliverFinalReport() {
        return "no creek found";
    }

}
