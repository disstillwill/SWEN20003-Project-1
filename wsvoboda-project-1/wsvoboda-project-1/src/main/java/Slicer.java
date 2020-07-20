import bagel.DrawOptions;
import bagel.Image;
import bagel.util.Point;
import bagel.util.Vector2;

import java.util.List;

/**
 * The Slicer enemy.
 *
 * @author William Svoboda
 */
public class Slicer {

    // Default move speed of the Slicer in pixels per frame
    private static final double DEFAULT_SPEED = 1.0;

    private Image sprite; // The Slicer's sprite
    private List<Point> polyline; // The track the Slicer follows
    private Point position; // The Slicer's current position
    private double rotation = 0; // The Slicer's current position
    private DrawOptions drawOptions; // The Slicer's sprite draw options
    private int currentPointIndex; // Index of current target point in polyline
    private int endPointIndex; // Index of last target point in polyline
    private boolean finished = false; // Has the Slicer reached the end of the polyline?

    /**
     * Spawn a new Slicer.
     */
    public Slicer(List<Point> polyline) {
        sprite = new Image("res/images/slicer.png");
        this.polyline = polyline;
        position = polyline.get(0); // Start the Splicer at the polyline's start
        drawOptions = new DrawOptions();
        currentPointIndex = 0;
        endPointIndex = polyline.size() - 1;
    }

    /**
     * Draw the Slicer.
     */
    public void draw() {
        drawOptions.setRotation(rotation);
        sprite.draw(position.x, position.y, drawOptions);
    }

    /**
     * Move the Slicer along the polyline.
     */
    public void move() {
        Point nextPoint;
        // If the indices of the current point and the end point are equal do not update the next point.
        if (currentPointIndex != endPointIndex) {
            // Get the next point in the polyline.
            nextPoint = polyline.get(currentPointIndex + 1);

            // If the Slicer's current position is close enough to the next point update the next point index.
            if (approximatelyEquals(position, nextPoint)) currentPointIndex++;
            updatePosition(nextPoint);
        }
        else {
            nextPoint = polyline.get(endPointIndex);
            if (!approximatelyEquals(position, nextPoint)) updatePosition(nextPoint);
            else finished = true;
        }
    }

    /* Determine if two doubles are approximately equal. */
    private static boolean approximatelyEquals(Point a, Point b) {
        final double DIFFERENCE_THRESHOLD = 0.7;

        return a.distanceTo(b) < DIFFERENCE_THRESHOLD;
    }

    /* Update the Slicer's position by moving it toward the next point in the polyline. */
    private void updatePosition(Point nextPoint) {
        // Calculate the direction vector towards the next point from the Slicer's current position.
        Vector2 direction = nextPoint.asVector().sub(position.asVector());

        // Calculate the magnitude of the direction vector.
        double magnitude = direction.length();

        // Calculate the Slicer's velocity in pixels per frame towards the next point.
        Vector2 velocity = direction.mul(DEFAULT_SPEED / magnitude);

        // Calculate the Slicer's new position via vector addition of the position and velocity.
        position = position.asVector().add(velocity).asPoint();

        // Update the Slicer's rotation.
        rotation = Math.atan2(direction.y, direction.x);
    }

    /**
     * Return if the Slicer is finished traversing the polyline.
     */
    public boolean isFinished() {
        return finished;
    }
}
