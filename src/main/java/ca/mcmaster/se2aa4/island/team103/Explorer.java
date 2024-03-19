package ca.mcmaster.se2aa4.island.team103;

import java.io.StringReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.ace_design.island.bot.IExplorerRaid;
import org.json.JSONObject;
import org.json.JSONTokener;

public class Explorer implements IExplorerRaid {

    private static final Logger logger = LogManager.getLogger();
	private ExplorationManager manager; //Constructor is called in this.initialize() when initial info is available (heading etc.)

    @Override
    public void initialize(String s) {
        logger.info("** Initializing the Exploration Command Center");
        JSONObject info = new JSONObject(new JSONTokener(new StringReader(s)));
        logger.info("** Initialization info:\n {}",info.toString(2));
        String direction = info.getString("heading");
        Integer batteryLevel = info.getInt("budget");
        logger.info("The drone is facing {}", direction);
        logger.info("Battery level is {}", batteryLevel);
        manager = new ExplorationManager(direction, batteryLevel);
    }

    @Override
    public String takeDecision() {
        JSONObject decision = manager.getDecision();
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
		manager.addInfo(response);
    }

    @Override
    public String deliverFinalReport() {
        SiteTracker siteTracker;
        logger.info("Compiling final static Report");
		siteTracker = new SiteTracker();
		siteTracker.findPointsOfInterest(manager.getResponseReport(),manager.getNavReport());
        String closest_inlet = siteTracker.getClosestInlet();
        logger.info("Closest Inlet: {}", closest_inlet);
		return closest_inlet;
    }

}
