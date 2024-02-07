package ca.mcmaster.se2aa4.island.team103.Navigation;
import org.json.JSONObject;

public class Flying {
	public JSONObject flyForward(){
		JSONObject fly = new JSONObject();
		fly.put("action", "fly");
		return fly;
	}
}
