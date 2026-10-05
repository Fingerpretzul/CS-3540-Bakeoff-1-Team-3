package cs3540;

import processing.core.PApplet;

/**
 * This is a simple drawing application made with processing. It contains the
 * essential processing methods and links to their documentation. See
 * https://processing.org/reference#structure For the structure of a processing
 * application.
 * 
 * @author Jens Phanich
 * 
 */
public class SimpleDraw extends PApplet {

	// The argument passed to main must match the class name
	public static void main(String[] args) {
		PApplet.main("cs3540.SimpleDraw");
	}

	// The color that the user can draw
	private int drawColor;
	// The thickness of the users pen
	private int drawThickness;

	// method used for setting the size of the window
	// https://processing.org/reference/settings_.html#:~:text=The%20settings()%20method%20runs,commands%20in%20the%20Processing%20API.
	public void settings() {
		// If using eclipse the size of our applet must be set her and NOT in setup.
		size(700, 700);
	}

	// https://processing.org/reference/setup_.html
	public void setup() {
		drawThickness = 20;
		drawColor = color(255, 0, 0);
		// function sets the color used for the background of the Processing window.
		background(255, 255, 255);
		// This sets up our stroke thickness and color.
		// Sets the color used to draw lines and borders around shapes
		stroke(255, 0, 0);
		// Sets the width of the stroke used for lines, points, and the border around
		// shapes. All widths are set in units of pixels.
		strokeWeight(10);
	}

	/**
	 * https://processing.org/reference/draw_.html function continuously executes
	 * the lines of code contained inside its block until the program is stopped
	 * ornoLoop() is called. draw() is called automatically and should never be
	 * called explicitly. All Processing programs update the screen at the end of
	 * draw(),never earlier.
	 */
	public void draw() {
		// Draw or drawing options
		// Set to white
		stroke(0, 0, 0);
		fill(255, 255, 255);
		rect(20, 20, 40, 40);
		// Set to red
		stroke(255, 0, 0);
		fill(255, 0, 0);
		rect(90, 20, 40, 40);
		// Set to green
		stroke(0, 255, 0);
		fill(0, 255, 0);
		rect(160, 20, 40, 40);
		// Set to blue
		stroke(0, 0, 255);
		fill(0, 0, 255);
		rect(230, 20, 40, 40);

		// Im using this to "clear" the text since draw draws on top of existing
		// elements.
		stroke(255, 255, 255);
		fill(255, 255, 255);
		rect(320, 20, 1000, 40);
		// Draw plus and minus for pen size
		textFont(createFont("Arial", 1, true), 64);
		fill(0);
		text("+", 320, 60);
		text("-", 380, 60);
		textFont(createFont("Arial", 1, true), 32);
		text("Pen Size: " + drawThickness, 450, 60);

	}
//https://processing.org/reference#input-mouse
// Below are some methods that I use for mouse input

	/**
	 * https://processing.org/reference/mouseClicked_.html I use this method to set
	 * the color and size of the pen
	 */
	public void mouseClicked() {

		System.out.println("Clicked Mouse mouseX is at " + mouseX + " and mouseY is at " + mouseY);
		// Color option detection
		if (mouseY >= 10 && mouseY <= 65) {
			if (mouseX >= 15 && mouseX <= 65) {
				// Set to white
				drawColor = color(255, 255, 255);
				System.out.println("White clicked");
			} else if (mouseX >= 85 && mouseX <= 135) {
				// Set to red
				drawColor = color(255, 0, 0);
				System.out.println("Red clicked");
			} else if (mouseX >= 155 && mouseX <= 205) {
				// Set to green
				drawColor = color(0, 255, 0);
				System.out.println("Green clicked");
			} else if (mouseX >= 225 && mouseX <= 277) {
				// Set to blue
				drawColor = color(0, 0, 255);
				System.out.println("Blue clicked");
			} else if (mouseX >= 321 && mouseX <= 356) {
				if (drawThickness <= 1000) {
					// increase drawThickness
					drawThickness += 2;
				}
				System.out.println("+ clicked");
			} else if (mouseX >= 375 && mouseX <= 405) {
				if (drawThickness > 2) {
					// decrease drawThickness
					drawThickness -= 2;
				}
				System.out.println("- clicked");
			}
		}
	}

	/**
	 * https://processing.org/reference/mouseDragged_.html I am using this method as
	 * a way for the user to draw
	 */
	public void mouseDragged() {
		// I don't want users to be able to draw where the color selection option are
		if (pmouseY > 90) {
			// we can draw
			stroke(drawColor);
			fill(drawColor);
			ellipse(mouseX, mouseY, drawThickness, drawThickness);
		}
	}
}