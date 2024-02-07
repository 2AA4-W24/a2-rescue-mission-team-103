package ca.mcmaster.se2aa4.island.team103.Recon;

import java.util.ArrayList;

enum Tile{
	None,
	Ocean,
	Land,
}
// Expand later based on what is available (may need to expand to full Tile class later on
// if more info needed)

public class Map {
	// Storing info about map.
	ArrayList<Tile> tiles = new ArrayList<Tile>();

	public void populateMap(){
		// Methods for flying to fill in map
	}

	public void findIsland(){
		// Method for finding island (close to centre usually, so maybe head there first)
	}

	public void findEmSite(){
		// to find emergency site
	}

	public void closestCreek(){
		// Closest creek (use find inlet methods), (could further delegate this task if needed)
	}

}
