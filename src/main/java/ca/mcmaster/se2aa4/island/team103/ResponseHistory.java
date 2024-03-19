package ca.mcmaster.se2aa4.island.team103;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class ResponseHistory implements History<JSONObject> {
	private List<JSONObject> history = new ArrayList<>();

	public void addItem(JSONObject j){
		history.add(j);
	}

	public JSONObject getLast(){
		return history.get(history.size()-1);
	}

	public List<JSONObject> getItems(int offset) {
		/* positive offset gets all items from the offset to the end (inclusive) (offset = 3 -> items index 3 to end)
		 * negative offset gets last n items (inclusive) (offset = -3 -> last 3 items) */
		if (offset >= 0) {
			return history.subList(offset, history.size());
		} else {
			return history.subList(history.size() + offset, history.size());
		}
	}

	public List<JSONObject> getItems(int start, int end) {
		/* Gets items from start (inclusive) to end (exclusive)*/
		return history.subList(start, end);
	}

	public JSONObject getItem(int index){
		return history.get(index);
	}

	public int getSize(){
		return history.size();
	}
}
