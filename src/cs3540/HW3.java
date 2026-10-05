package cs3540;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PVector;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Your work in this class
 * do not change the name of the class
 * @author Emersen Ingebretsen
 */
public class HW3 extends PApplet {
    Circle[] circles;
    Circle[] textCircles;
    List<PVector> textPoints;
    boolean startSimulation;

    int screenW = 1000;
    int screenH = 750;
    PVector center;

    int yellow = color(255, 255, 0);
    int purple = color(154, 24, 222);

    PVector mousePosition;

    // PGraphics, used to process text shapes
    PGraphics pg;

	// Do not change
	public static void main(String[] args) {
		// Tell processing what class we want to run. 
		PApplet.main("cs3540.HW3");
	}

    // method for setting the size of the window
    public void settings(){
        size(screenW, screenH);
    }

    // https://processing.org/reference/setup_.html
    public void setup() {
        pg = createGraphics(screenW, screenH);

        startSimulation = true;

        center = new PVector((float) screenW / 2, (float) screenH / 2);

        int circleCount = 5000;
        circles = new Circle[circleCount];
        mousePosition = new PVector(mouseX, mouseY);

        // Generate text circles:
        textPoints = getTextPoints("CS 3540\n Rocks!");
        textCircles = new Circle[textPoints.size()];
        for (int i = 0; i < textCircles.length; i++) { // Some extra criclees are generated for the text. This forces the text to feel more filleed in and readable
            textCircles[i] = new Circle(textPoints.get(i), gradient(textPoints.get(i), yellow, purple), 15, 10);
        }

        // Generate circles
        for (int i = 0; i < circleCount; i++) {
            circles[i] = new Circle(15);
        }

        // Sets the width of the stroke used for lines, points, and the border around
        // shapes. All widths are set in units of pixels.
        strokeWeight(0);
    }

    public void draw() {
        drawBackground();

        // Draws the circles
        if (startSimulation) {
            for (Circle c : circles) {
                c.advance();
                c.draw();
            }
            for (Circle c : textCircles) {
                c.advance();
                c.draw();
            }
        }

        boolean mouseOffScreen = mouseX >= screenW - 5 || mouseX <= 0 || mouseY >= screenH - 5 || mouseY <= 0;
        if (mouseOffScreen) {
            mousePosition.set(-100, -100);
        } else {
            mousePosition.set(mouseX, mouseY);
        }
    }

    /**
     * Draws the background
     */
    public void drawBackground() {
        background(0);
        stroke(0);
        fill(0);
        rect(0, 0, screenW, screenH);
    }

    /**
     * Generates a list of points, all of which together create the provided text.
     * (Much of this code was made with great assistance from ChatGPT, as it helped me understand
     * much better the PGraphics and pixels[] elements of Processing. While very little of it was
     * directly made by ChatGPT, I would be lying if I said I made it entirely on my own).
     *
     * @param text the text to be generated
     * @return List of points that outline that text
     */
    private List<PVector> getTextPoints(String text) {
        int resolution = 15;

        pg.beginDraw();
        pg.background(255);
        pg.textSize(255);
        pg.textAlign(CENTER, CENTER);
        pg.fill(0, 0, 0);
        pg.text(text, (float)screenW / 2, (float)screenH / 2);
        pg.endDraw();

        LinkedList<PVector> tp = new LinkedList<>();
        pg.loadPixels();

        for (int x = 0; x < pg.width; x += resolution) {
            for (int y = 0; y < pg.height; y += resolution) {
                if (getPixelColorAt(x, y) < -1) {
                    tp.add(new PVector(x, y));
                }
            }
        }

        return tp;
    }

    /**
     * Gets the pixel color at a given (X, Y) from the pixels[] array
     * WARNING - pg.loadPixels() must be called at least once or this function will fail
     * https://processing.org/reference/pixels.html
     *
     * @param x Target x coordinate
     * @param y Target y coordinate
     * @return Pixel color at the desired coordinates
     */
    private int getPixelColorAt(int x, int y) {
        return pg.pixels[x + y * pg.width];
    }

    /**
     * Gets the calculated gradient for a given pixel.
     * Gradient starts in the center (c1) and radiates outwards (c2)
     *
     * @param point Desired (X, Y) point
     * @param c1 Center color
     * @param c2 Edge color
     * @return Color of the provided point
     */
    private int gradient(PVector point, int c1, int c2) {
        float distToCenter = PVector.dist(point, center);
        distToCenter = (distToCenter / ((float) max(screenW, screenH) / 2)); // Turn it into a ratio

        return lerpColor(c1, c2, distToCenter);
    }

    /**
     * Represents a circle in the 2D space. This circle will slowly wander around its origin point
     * and move away from the user's mouse
     */
    private class Circle {
        private final int color;
        private final float size;

        private final PVector origin; // Original position, circle will orbit this
        private PVector pos; // Current position
        private PVector halfway; // Halfway point to the target
        private PVector tar; // Target position
        private float wander; // Amount that the dot "Wanders" around it's origin

        // The distance between the starting position and the target
        // this is calculated when a new target is found
        private float targetDistance;
        private PVector velocity; // Current velocity

        private boolean scared;

        /**
         * Circle constructor where all values are specified
         *
         * @param origin Starting position of the middle of the circle
         * @param color Color of the circle
         * @param size Size (Width & Height) of the circle
         */
        public Circle(PVector origin, int color, float size, float wander) {
            this.origin = origin.copy();
            this.pos = origin.copy();
            this.tar = origin.copy();
            this.color = color;
            this.size = size;
            this.wander = max(wander, 0);

            this.velocity = new PVector(0, 0);
            scared = false;
            getNewTarget();
        }

        /**
         * Circle constructor where all values are specified (Uses two floats as opposed to a PVector for creating the circle)
         *
         * @param x Starting x position of the middle of the circle
         * @param y Starting y position of the middle of the circle
         * @param color Color of the circle
         * @param size Size (Width & Height) of the circle
         */
        public Circle(float x, float y, int color, float size, float wander) {
            this(new PVector(x, y), color, size, wander);
        }

        /**
         * Constructor for creating a random circle. All values besides the size
         * are generated randomly
         * @param size Desired size of the circle
         */
        public Circle(float size) {
            PVector p = new PVector(random(screenW), random(screenH));
            this(p, gradient(p, yellow, purple), size, 50);
        }

        /**
         * Draws the circle, including overrides for circles over text points
         */
        public void draw() {
            int textColor = color(41, 143, 235);
            stroke(0, 0, 0);
            strokeWeight(1);

            // Draw circles close to text as baby blue
            float closestDist = -1;
            for (PVector p : textPoints) {
                float pDist = PVector.dist(p, pos);
                if (pDist < closestDist || closestDist == -1) {
                    closestDist = pDist;
                }
            }

            if (closestDist < 20) {
                fill(lerpColor(textColor, color, max((float)(closestDist / 20 - 0.25), 0)));
            } else {
                fill(color);
            }

            circle(pos.x, pos.y, size);
        }

        /**
         * Updates the circles position according to a set of rules.
         * - If the mouse is too close it will run away from it
         * - If not, it will move towards its current target according to getSpeeed()
         * - If it's reached it's target, it will select a new target using getNewTarget()
         */
        public void advance() {
            float distance = pos.dist(tar);
            float mouseDistance = PVector.dist(pos, mousePosition);
            float scaryDistance = 100; // how close the mouse can get before the dot gets "Scared"

            // If the mouse is too close, run away!
            if (mouseDistance < scaryDistance) {
                scared = true;
                velocity = PVector.sub(mousePosition, pos).mult(-1);

                // The following line was generated by ChatGPT
                float fleeSpeed = map(
                        mouseDistance,
                        0,
                        scaryDistance,
                        7f,
                        .25f
                );

                velocity.setMag(fleeSpeed);
            } else {
                float speed = getSpeed();

                if (distance < speed || scared) {
                    getNewTarget();
                    speed = getSpeed();

                    if (scared)
                        scared = false;
                }

                if (distance < 0.1) {
                    return;
                }

                velocity = PVector.sub(tar, pos);
                velocity.setMag(speed);
            }


            pos.add(velocity);
        }

        /**
         * Acquires a new target for the circle to go towards,
         * target will always orbit within [wander] units of where the
         * sphere was originally spawned
         */
        private void getNewTarget() {
            if (wander == 0) { // If the circle doesn't wander, don't find a new destination.
                tar = origin.copy();
            } else {
                PVector rv = PVector.random2D().mult(wander);
                tar = PVector.add(origin, rv);
            }

            tar.x = constrain(tar.x, 0, screenW);
            tar.y = constrain(tar.y, 0, screenH);

            targetDistance = PVector.dist(pos, tar);
        }

        public void overrideWander(float n) {
            wander = abs(n);
        }

        /**
         * Calculates the current speed based on the following formula:
         * y = (4(maxS - minS)/td^2 * x(td - x)) + minS
         * Where X is the distance traveled towards the current target. This creates
         * a parabolic speed, making the circle appear to speed up then slow down naturally.
         *
         * @param minS Minimum speed
         * @param maxS Maximum speed
         * @param td Distance between the start and the destination
         * @param t Target point
         * @return the speed the circle should be traveling
         */
        private float getSpeed(float minS, float maxS, float td, PVector t) {
            float x = td - PVector.dist(pos, t);

            float a = (4 * (maxS - minS)) / sq(td);
            float b = td - x;
            float s = a * x * b;

            return s + minS;
        }

        /**
         * Calculates the current speed based on the following formula:
         * y = 4maxSpeed/targetDistance^2 * x(targetDistance - x)
         * Where X is the distance traveled towards the current target
         *
         * Uses default values for speed restrctions:
         * minSpeed = 0.2
         * maxSpeed = 1
         *
         * Routes from where the circle started, to where it's target (tar) is
         *
         * @return the speed the circle should be traveling
         */
        private float getSpeed() {
            float minSpeed = .2f;
            float maxSpeed = 1f;

            return getSpeed(minSpeed, maxSpeed, targetDistance, tar);
        }
    }
}