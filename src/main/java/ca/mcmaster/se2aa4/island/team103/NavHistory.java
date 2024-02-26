package ca.mcmaster.se2aa4.island.team103;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

public class NavHistory {
	private List<Coordinate> navHistory = new ArrayList<Coordinate>();
	
	public void addItem(Coordinate c){
		navHistory.add(c);
	}

	public Coordinate getLast(){
		return navHistory.get(navHistory.size()-1);
	}

	public Coordinate getFirst(){
		return navHistory.get(0);
	}

	public List<Coordinate> getItems(int offset) {
		/* positive offset gets all items from the offset to the end (inclusive) (offset = 3 -> items index 3 to end)
		 * negative offset gets last n items (inclusive) (offset = -3 -> last 3 items) */
		if (offset >= 0) {
			return navHistory.subList(offset, navHistory.size());
		} else {
			return navHistory.subList(navHistory.size() + offset, navHistory.size());
		}
	}

	public List<Coordinate> getItems(int start, int end) {
		/* Gets items from start (inclusive) to end (exclusive)*/
		return navHistory.subList(start, end);
	}

	public int getSize(){
		return navHistory.size();
	}

	public Coordinate getIndex(int index){
		return navHistory.get(index);
	}

}
