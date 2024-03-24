package ca.mcmaster.se2aa4.island.team103.history;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ActionUsage {
	int echo_F = 0;
	int echo_R = 0;
	int echo_L = 0;
	int scan = 0;
	int turn_L = 0;
	int turn_R = 0;
	int fly = 0;
	String summary;
	private final Logger logger = LogManager.getLogger();

	public void log(Action action) {
		switch (action) {
			case Action.ECHO_FORWARD:
				this.echo_F++;
				break;
			case Action.ECHO_RIGHT:
				this.echo_R++;
				break;
			case Action.ECHO_LEFT:
				this.echo_L++;
				break;
			case Action.SCAN:
				this.scan++;
				break;
			case Action.TLEFT:
				this.turn_L++;
				break;
			case Action.TRIGHT:
				this.turn_R++;
				break;
			case Action.FORWARD:
				this.fly++;
				break;
			default:
				logger.warn("Attempting to log unsupported action");
		}
	}

	public String getSummary() {
		int echo_total = this.echo_F + this.echo_L + this.echo_R;
		int turn_total = this.turn_L + this.turn_R;
		this.summary = "Action Usage Summary:";
		this.summary = this.summary + "\n\tEcho Total: " + echo_total;
		this.summary = this.summary + "\n\tForward Total: " + fly;
		this.summary = this.summary + "\n\tTurn Total: " + turn_total;
		this.summary = this.summary + "\n\tScan Total: " + this.scan;
		this.summary = this.summary + "\n\tEcho Left: " + this.echo_L;
		this.summary = this.summary + "\n\tEcho Right: " + this.echo_R;
		this.summary = this.summary + "\n\tEcho Forward: " + this.echo_F;
		this.summary = this.summary + "\n\tTurn Left: " + this.turn_L;
		this.summary = this.summary + "\n\tTurn Right: " + this.turn_R;
		return summary;
	}

}
