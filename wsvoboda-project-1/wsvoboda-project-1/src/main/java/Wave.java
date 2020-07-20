import bagel.util.Point;

import java.util.ArrayList;
import java.util.List;

/**
 * A Wave in the game.
 *
 * @author William Svoboda
 */
public class Wave {

    // The number of nanoseconds per one second
    private static final long NANOSECONDS_PER_SECOND = 1_000_000_000;

    // The spawn delay multiplier
    private static final long DELAY_MULTIPLIER = 2;

    // The default spawn delay between Slicers in seconds
    private static final long DEFAULT_DELAY = 5;

    // The minimum timescale multiplier of the Wave
    private static final int MINIMUM_TIMESCALE = 1;

    private List<Point> polyline; // The track the Slicers follows
    private ArrayList<Slicer> slicers; // Holds Slicers spawned during the Wave
    private int numberOfSlicersToSpawn; // The number of Slicers in the Wave
    private long spawnDelay = DEFAULT_DELAY * NANOSECONDS_PER_SECOND; // Delay between Slicers in nanoseconds
    private long timeSinceLastSpawn; // The time in nanoseconds when the last Slicer was spawned
    private int timescale = MINIMUM_TIMESCALE; // The timescale multiplier of the Wave
    private boolean hasStarted = false; // Has the Wave started?
    private boolean isFinished = false; // Has the Wave finished?

    /**
     * Create a new Wave.
     */
    public Wave(List<Point> polyline, int numberOfSlicers) {
        this.polyline = polyline;
        slicers = new ArrayList<Slicer>();
        this.numberOfSlicersToSpawn = numberOfSlicers;
    }

    /**
     * Update the Wave.
     */
    public void update () {
        // Calculate elapsed time.
        long elapsedTime = System.nanoTime() - timeSinceLastSpawn;

        // If no Slicers have been spawned yet start by spawning one.
        if (slicers.size() == 0 && numberOfSlicersToSpawn > 0) spawnSlicer();
        // Otherwise, spawn a Slicer if the spawn delay has been reached.
        else if (elapsedTime >= spawnDelay && numberOfSlicersToSpawn > 0) {
            spawnSlicer();
            timeSinceLastSpawn = System.nanoTime(); // Reset time since last spawn
        }
        // If the Wave is finished
        else if (slicers.size() == 0 && numberOfSlicersToSpawn == 0) {
            isFinished = true;
        }

        // Create a list of Slicers to remove after update to avoid concurrent modification.
        List<Slicer> slicersToRemove = new ArrayList<Slicer>();

        // Update each Slicer in the Wave
        for (Slicer slicer : slicers) {
            // If a slicer is finished add it to the removal list.
            if (slicer.isFinished()) slicersToRemove.add(slicer);
            else {
                // Move the Slicer proportional to the timescale multiplier.
                for (int i = 0; i < timescale; i++) {
                    slicer.move();
                    if (slicer.isFinished()) slicersToRemove.add(slicer);
                }
                if (!slicersToRemove.contains(slicer)) slicer.draw();
            }
        }
        slicers.removeAll(slicersToRemove); // Remove all finished Slicers.
    }

    /* Spawn a new Slicer in the Wave. */
    private void spawnSlicer() {
        slicers.add(new Slicer(polyline));
        numberOfSlicersToSpawn--;
    }

    /**
     * Speed up the Wave.
     */
    public void speedUp() {
        timescale++;
        spawnDelay /= DELAY_MULTIPLIER;
    }

    /**
     * If possible, slow down the Wave.
     */
    public void slowDown() {
        if (timescale > MINIMUM_TIMESCALE) {
            timescale--;
            spawnDelay *= DELAY_MULTIPLIER;
        }
    }

    /**
     * Start the Wave if it hasn't begun yet.
     */
    public void start() {
        hasStarted = true;
        timeSinceLastSpawn = System.nanoTime();
    }

    /**
     * Return if the Wave has started.
     */
    public boolean hasStarted() {
        return hasStarted;
    }

    /**
     * Return if the Wave has finished.
     */
    public boolean isFinished() {
        return isFinished;
    }
}
