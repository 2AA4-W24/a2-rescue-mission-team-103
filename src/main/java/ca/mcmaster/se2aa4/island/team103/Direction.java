package ca.mcmaster.se2aa4.island.team103;


public enum Direction{
	NORTH,
	WEST,
	SOUTH,
	EAST;

	public Direction turnRight() {
		switch(this) {
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
		throw new IllegalStateException("Illegal direction: " + this);
	}

	public Direction turnLeft() {
		switch(this) {
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
		throw new IllegalStateException("Illegal direction: " + this);
	}
} 

