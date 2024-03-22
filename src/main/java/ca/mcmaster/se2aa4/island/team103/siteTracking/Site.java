package ca.mcmaster.se2aa4.island.team103.siteTracking;

import ca.mcmaster.se2aa4.island.team103.history.Coordinate;

public class Site implements PointOfInterest {
	private String id;
	private Coordinate coord;

	public Site(String id_in, Coordinate coordinate) {
		id = id_in;
		coord = coordinate;
	}

	public String id() {
		return id;
	}

	public Coordinate coord() {
		return coord;
	}
}
