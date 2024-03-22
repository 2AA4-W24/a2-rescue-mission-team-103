package ca.mcmaster.se2aa4.island.team103.history;

public class Coordinate {
	private int x;
	private int y;

	public Coordinate(int x_in,int y_in) {
		x = x_in;
		y = y_in;
	}

	public int x() {
		return x;
	}

	public int y() {
		return y;
	}

	public boolean equalTo(Coordinate c){
		return (this.x == c.x && this.y == c.y);
	}

	public String toString(){
		return "(" + this.x() + ", " + this.y() + ")";

	}
}
