package ca.mcmaster.se2aa4.island.team103.islandLocating;

// Counter object used for island locating phase.
public class Counter {
    private int count;

    public Counter() {
        this.count = 0;
    }

    public Counter(int start) {
        this.count = start;
    }

    public void next() {
        this.count++;
    }

    public void reset() {
        this.count = 0;
    }

    public int value() {
        return this.count;
    }
}
