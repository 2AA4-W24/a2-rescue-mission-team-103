package ca.mcmaster.se2aa4.island.team103;

public class Inlet implements PointOfIntrest {
	private String id;
	private Coordinate coordinate;

	public Inlet(String id_in, Coordinate coord) {
		id = id_in;
		coordinate = coord;
	}

	public String id() {
		return id;
	}

	public Coordinate coord() {
		return coordinate;
	}
}
