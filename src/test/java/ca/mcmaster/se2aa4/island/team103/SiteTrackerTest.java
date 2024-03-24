package ca.mcmaster.se2aa4.island.team103;

import org.junit.jupiter.api.Test;

import ca.mcmaster.se2aa4.island.team103.siteTracking.*;
import ca.mcmaster.se2aa4.island.team103.history.Coordinate;
import java.util.List;
import java.util.ArrayList;
import org.json.JSONObject;
import org.json.JSONArray;

import static org.junit.jupiter.api.Assertions.*;

// Testing of site and inlet tracking.
class SiteTrackerTest {
	SiteTracker siteTracker = new SiteTracker();

	public JSONObject addCreektoHistory(String id) {
		// Adds a simulated out_of_range response to history
		JSONObject history = new JSONObject();
		JSONObject extras = new JSONObject();
		JSONArray creeks = new JSONArray();
		JSONArray sites = new JSONArray();
		creeks.put(id);
		extras.put("creeks",creeks);
		extras.put("sites",sites);
		extras.put("biomes","blankfortesting");
		history.put("extras",extras);
		return history;
	}

	public JSONObject addSitetoHistory(String id) {
		// Adds a simulated out_of_range response to history
		JSONObject history = new JSONObject();
		JSONObject extras = new JSONObject();
		JSONArray creeks = new JSONArray();
		JSONArray sites = new JSONArray();
		sites.put(id);
		extras.put("creeks",creeks);
		extras.put("sites",sites);
		extras.put("biomes","blankfortesting");
		history.put("extras",extras);
		return history;
	}

	public Coordinate addCoordtoHistory(int x, int y){
		Coordinate c = new Coordinate(x,y);
		return c;
	}

	@Test
	void compilingPointsOfInterest() {
		List<JSONObject> respHistory = new ArrayList<JSONObject>();
		List<Coordinate> navHistory = new ArrayList<Coordinate>();
		JSONObject creekHistory = addCreektoHistory("12345");
		Coordinate creekCoord = addCoordtoHistory(5, 5);
		JSONObject siteHistory = addSitetoHistory("98765");
		Coordinate siteCoord = addCoordtoHistory(10, 10);
		respHistory.add(creekHistory);
		respHistory.add(siteHistory);
		navHistory.add(creekCoord);
		navHistory.add(siteCoord);
		siteTracker.compilePointsOfInterest(respHistory,navHistory);
		String closest = siteTracker.getClosestInlet();
		assertEquals(closest,"12345");
	}

	@Test
	void compilingPointsOfInterestNoSite() {
		List<JSONObject> respHistory = new ArrayList<JSONObject>();
		List<Coordinate> navHistory = new ArrayList<Coordinate>();
		JSONObject creekHistory = addCreektoHistory("12345");
		Coordinate creekCoord = addCoordtoHistory(5, 5);
		respHistory.add(creekHistory);
		navHistory.add(creekCoord);

		siteTracker.compilePointsOfInterest(respHistory,navHistory);
		String closest = siteTracker.getClosestInlet();
		assertEquals(closest,"12345");
	}

	@Test
	void compilingPointsOfInterestEmpty() {
		List<JSONObject> respHistory = new ArrayList<JSONObject>();
		List<Coordinate> navHistory = new ArrayList<Coordinate>();
		siteTracker.compilePointsOfInterest(respHistory,navHistory);
		String closest = siteTracker.getClosestInlet();
		assertEquals(closest,"no creek found");
	}

}
