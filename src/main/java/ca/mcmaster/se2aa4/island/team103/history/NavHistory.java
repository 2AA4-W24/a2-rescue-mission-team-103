package ca.mcmaster.se2aa4.island.team103.history;

import java.util.ArrayList;
import java.util.List;

public class NavHistory implements History<Coordinate> {
	private List<Coordinate> history = new ArrayList<>();
	
	public void addItem(Coordinate c){
		history.add(c);
	}

	public Coordinate getLast(){
		return history.get(history.size()-1);
	}

	public Coordinate getFirst(){
		return history.get(0);
	}

	public List<Coordinate> getItems(int offset) {
		/* positive offset gets all items from the offset to the end (inclusive) (offset = 3 -> items index 3 to end)
		 * negative offset gets last n items (inclusive) (offset = -3 -> last 3 items) */
		if (offset >= 0) {
			return history.subList(offset, history.size());
		} else {
			return history.subList(history.size() + offset, history.size());
		}
	}

	public List<Coordinate> getItems(int start, int end) {
		/* Gets items from start (inclusive) to end (exclusive)*/
		return history.subList(start, end);
	}

	public int getSize(){
		return history.size();
	}

	public Coordinate getItem(int index){
		return history.get(index);
	}

}
