package ca.mcmaster.se2aa4.island.team103;

import java.util.ArrayList;
import java.util.List;

public class Map {
	// Storing info about map.
	List<Tile> tiles = new ArrayList<Tile>();

	private void addTile(Tile t) {
		tiles.add(t);
	}

	public void populateMap(){
		// Methods for flying to fill in map
	}

	public void findIsland(){
		// Method for finding island (close to centre usually, so maybe head there first)
	}

	public void findEmSite(){
		// Get initial orientation and coordinates.
		// Travel towards center of map 
		// Locate emergency site
	}

	public void closestCreek(){
		// Closest creek (use find inlet methods), (could further delegate this task if needed)
	}

}
