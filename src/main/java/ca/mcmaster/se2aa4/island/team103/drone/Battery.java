package ca.mcmaster.se2aa4.island.team103.drone;

public class Battery {
	// For tracking battery percentage and ensuring that power is not too low to continue.
	private int value;
	public Battery(int starting_level) {
		// Initializes battery with starting battery level (passed in)
		this.value = starting_level;
	}

	public void log(int cost) {
		value -= cost;
	}

	public boolean canContinue() {
		if (this.value > 50) {
			return true;
		} else {
			return false;
		}
	}

	public int getBattery(){
		return this.value;
	}
}
