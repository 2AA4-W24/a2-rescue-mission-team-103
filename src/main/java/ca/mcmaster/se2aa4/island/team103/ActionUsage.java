package ca.mcmaster.se2aa4.island.team103;

public class ActionUsage {
	int echo_F = 0;
	int echo_R = 0;
	int echo_L = 0;
	int scan = 0;
	int turn_L = 0;
	int turn_R = 0;
	int fly = 0;
	String summary;

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
		}
	}

	public String getSummary() {
		int echo_total = this.echo_F + this.echo_L + this.echo_R;
		int turn_total = this.turn_L + this.turn_R;
		this.summary = "\nAction Usage Summary";
		this.summary = this.summary + "\nEcho Total: " + echo_total;
		this.summary = this.summary + "\nForward Total: " + fly;
		this.summary = this.summary + "\nTurn Total: " + turn_total;
		this.summary = this.summary + "\nScan Total: " + this.scan;
		this.summary = this.summary + "\nEcho Left: " + this.echo_L;
		this.summary = this.summary + "\nEcho Right: " + this.echo_R;
		this.summary = this.summary + "\nEcho Forward: " + this.echo_F;
		this.summary = this.summary + "\nTurn Left: " + this.turn_L;
		this.summary = this.summary + "\nTurn Right: " + this.turn_R;
		return summary;
	}

}
