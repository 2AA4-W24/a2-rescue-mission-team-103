package ca.mcmaster.se2aa4.island.team103;


public enum Direction{
	NORTH,
	WEST,
	SOUTH,
	EAST;

	/*
	 * returns Direction to the right of d and throws exception if d is not a valid direction 
	 */
	public static Direction rightOf(Direction d) {
		switch(d) {
			case NORTH -> {
				return EAST;
			}
			case EAST -> {
				return SOUTH;
			}
			case SOUTH -> {
				return WEST;
			}
			case WEST -> {
				return NORTH;
			}
		}
		throw new IllegalStateException("Illegal direction: " + d);
	}
	
	/*
	 * returns Direction to the left of d and throws exception if d is not a valid direction 
	 */
	public static Direction leftOf(Direction d) {
		switch(d) {
			case NORTH -> {
				return WEST;
			}
			case EAST -> {
				return NORTH;
			}
			case SOUTH -> {
				return EAST;
			}
			case WEST -> {
				return SOUTH;
			}
		}
		throw new IllegalStateException("Illegal direction: " + d);
	}

	/*
	 * returns Direction d as a string or null if direction is invalid.
	 */
	public static String stringValue(Direction d) {
		String dirString = switch(d) {
			case NORTH -> {
				yield "N";
			}
			case EAST -> {
				yield "E";
			}
			case SOUTH -> {
				yield "S";
			}
			case WEST -> {
				yield "W";
			}
		};
		return dirString;
	}
} 

