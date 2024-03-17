package ca.mcmaster.se2aa4.island.team103;
import org.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.ArrayList;
import java.util.List;

public class ResponseHistory implements History<JSONObject> {
	private List<JSONObject> responseHistory = new ArrayList<JSONObject>();
	private final Logger logger = LogManager.getLogger();

	public void addItem(JSONObject j){
		responseHistory.add(j);
	}

	public JSONObject getLast(){
		return responseHistory.get(responseHistory.size()-1);
	}

	public List<JSONObject> getItems(int offset) {
		/* positive offset gets all items from the offset to the end (inclusive) (offset = 3 -> items index 3 to end)
		 * negative offset gets last n items (inclusive) (offset = -3 -> last 3 items) */
		if (offset >= 0) {
			return responseHistory.subList(offset, responseHistory.size());
		} else {
			return responseHistory.subList(responseHistory.size() + offset, responseHistory.size());
		}
	}

	public List<JSONObject> getItems(int start, int end) {
		/* Gets items from start (inclusive) to end (exclusive)*/
		return responseHistory.subList(start, end);
	}

	public JSONObject getItem(int index){
		return responseHistory.get(index);
	}

	public int getSize(){
		return responseHistory.size();
	}
}
