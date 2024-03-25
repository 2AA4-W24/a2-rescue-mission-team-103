package ca.mcmaster.se2aa4.island.team103;
import ca.mcmaster.se2aa4.island.team103.siteTracking.*;
import ca.mcmaster.se2aa4.island.team103.history.*;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class SiteTrackerTest {
	SiteTracker tracker;
	ResponseHistory rHistory;
	NavHistory nHistory;

	@BeforeEach
	void setup() {
		tracker = new SiteTracker();
		rHistory = new ResponseHistory();
		nHistory = new NavHistory();
	}

	private void addFlyResponse() {
		JSONObject fly = new JSONObject();
		JSONObject extras = new JSONObject();
		fly.put("cost", 6);
		fly.put("extras", extras);
		fly.put("status", "ok");
		rHistory.addItem(fly);
	}

	private void addEchoResponse() {
		JSONObject fly = new JSONObject();
		JSONObject extras = new JSONObject();
		extras.put("found", "GROUND");
		extras.put("range", 6);
		fly.put("cost", 6);
		fly.put("extras", extras);
		fly.put("status", "ok");
		rHistory.addItem(fly);
	}

	private void addBlankScanResponse() {
		JSONObject fly = new JSONObject();
		JSONObject extras = new JSONObject();
		JSONArray creeks = new JSONArray();
		JSONArray sites = new JSONArray();
		extras.put("creeks", creeks);
		extras.put("sites", sites);
		fly.put("cost", 6);
		fly.put("extras", extras);
		fly.put("status", "ok");
		rHistory.addItem(fly);
	}

	private void addCreekScanResponse(String id) {
		JSONObject fly = new JSONObject();
		JSONObject extras = new JSONObject();
		JSONArray creeks = new JSONArray();
		JSONArray sites = new JSONArray();
		creeks.put(id);
		extras.put("creeks", creeks);
		extras.put("sites", sites);
		fly.put("cost", 6);
		fly.put("extras", extras);
		fly.put("status", "ok");
		rHistory.addItem(fly);
	}

	private void addSiteScanResponse(String id) {
		JSONObject fly = new JSONObject();
		JSONObject extras = new JSONObject();
		JSONArray creeks = new JSONArray();
		JSONArray sites = new JSONArray();
		sites.put(id);
		extras.put("creeks", creeks);
		extras.put("sites", sites);
		fly.put("cost", 6);
		fly.put("extras", extras);
		fly.put("status", "ok");
		rHistory.addItem(fly);
	}

	private void addCoordinate(int x, int y) {
		Coordinate coord = new Coordinate(x,y);
		nHistory.addItem(coord);
	}

	@Test
	void baseCase() {
		addCreekScanResponse("creek1");
		addCoordinate(0, 0);
		addSiteScanResponse("site");
		addCoordinate(0, 1);
		tracker.compilePointsOfInterest(rHistory.getItems(0), nHistory.getItems(0));
		String result = tracker.getClosestInlet();
		String expected = "creek1";
		assertEquals(expected, result);
	}

	@Test
	void noSite() {
		addCreekScanResponse("creek1");
		addCreekScanResponse("creek2");
		addCoordinate(0, 0);
		addCoordinate(0, 1);
		tracker.compilePointsOfInterest(rHistory.getItems(0), nHistory.getItems(0));
		String result = tracker.getClosestInlet();
		String expected = "creek1";
		assertEquals(expected, result);
	}

	@Test
	void multiCreek() {
		addCreekScanResponse("creek1");
		addCreekScanResponse("creek2");
		addCreekScanResponse("creek3");
		addCoordinate(1,1);
		addCoordinate(2,1);
		addCoordinate(1,5);
		addSiteScanResponse("site1");
		addCoordinate(1, 2);
		tracker.compilePointsOfInterest(rHistory.getItems(0), nHistory.getItems(0));
		String result = tracker.getClosestInlet();
		String expected = "creek1";
		assertEquals(expected, result);
	}

	@Test
	void otherCommands() {
		addFlyResponse();
		addCreekScanResponse("creek1");
		addEchoResponse();
		addCreekScanResponse("creek2");
		addBlankScanResponse();
		addCreekScanResponse("creek3");
		addCoordinate(0,0);
		addCoordinate(1,1);
		addCoordinate(1,1);
		addCoordinate(2,1);
		addCoordinate(1,2);
		addCoordinate(1,5);
		addSiteScanResponse("site1");
		addCoordinate(1, 2);
		tracker.compilePointsOfInterest(rHistory.getItems(0), nHistory.getItems(0));
		String result = tracker.getClosestInlet();
		String expected = "creek1";
		assertEquals(expected, result);
	}
}
