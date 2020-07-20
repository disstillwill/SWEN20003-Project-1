import bagel.AbstractGame;
import bagel.Input;
import bagel.Keys;
import bagel.Window;
import bagel.map.TiledMap;
import bagel.util.Point;

import java.util.List;

/**
 * The Shadow Defend game.
 *
 * @author William Svoboda
 */
public class ShadowDefend extends AbstractGame {

    // The game's window width and height
    private static final int WINDOW_WIDTH = 1024;
    private static final int WINDOW_HEIGHT = 768;

    // The game's title
    private static final String TITLE = "Shadow Defend";

    // The number of Slicers in the game's Wave
    private static final int NUMBER_OF_SLICERS = 5;

    private TiledMap map; // The map
    private List<Point> polyline; // The map's polyline
    private Wave wave1; // The game's Wave

    public ShadowDefend() {
        super(WINDOW_WIDTH, WINDOW_HEIGHT, TITLE);
        map = new TiledMap("res/levels/1.tmx");
        polyline = map.getAllPolylines().get(0);
        wave1 = new Wave(polyline, NUMBER_OF_SLICERS);
        // Window.removeFrameThrottle(); ***Can uncomment to unlock framerate***
    }

    /**
     * The entry point for the program.
     */
    public static void main(String[] args) {
        ShadowDefend game = new ShadowDefend();
        game.run();
    }

    /**
     * Perform a state update.
     */
    @Override
    public void update(Input input) {
        // Draw the map on screen.
        map.draw(0, 0,
                0, 0,
                map.getWidth(), map.getHeight());

        // Start the Wave if necessary.
        if (input.wasPressed(Keys.S) && !wave1.hasStarted()) wave1.start();

        // Update the Wave if it has started.
        if (wave1.hasStarted()) {
            // Change the Wave's timescale if necessary.
            if (input.wasPressed(Keys.L)) wave1.speedUp();
            else if (input.wasPressed(Keys.K)) wave1.slowDown();

            wave1.update();

            // If the Wave is finished end the game.
            if (wave1.isFinished()) {
                Window.close();
            }
        }
    }
}