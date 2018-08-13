package DoodleJump;

/**
Constants used throughout the program, primarily used to define default
layouts, manipulate the rebound velocity, and dictate frequency with which non-platform elements should be
added to the game.
*/
public class Constants {

	public static final double SCENE_WIDTH = 400; // width of scene in pixels
	public static final double SCENE_HEIGHT = 800; // height of scene in pixels
	public static final double BACKGROUND_OPACITY = 0.6; // sets default opacity of background image (graph paper)

	public static final int GRAVITY = 1000; // acceleration constant (UNITS:
											// pixels/s^2)
	public static final int REBOUND_VELOCITY = -900; // initial jump velocity
														// (UNITS: pixels/s)
	public static final double DURATION = 0.016; // KeyFrame duration (UNITS: s)
	public static final int LAT_PXL_INCR = 5; // Dictates the number of pixels per movement left/right for doodle

	public static final int PLATFORM_WIDTH = 40; // (UNITS: pixels)
	public static final int PLATFORM_HEIGHT = 10; // (UNITS: pixels)
	public static final double DOODLE_WIDTH = 10; // (UNITS: pixels)
	public static final double DOODLE_HEIGHT = 20; // (UNITS: pixels)
	public static final double CLASSIC_DOODLE_DIM = 45; // default fit width of
														// doodle imageview
	public static final double TRAMP_VELOCITY_FACTOR = 1.75; // fraction of
																// rebound
																// velocity
																// after
																// trampoline
																// bounce
	public static final double SPRING_VELOCITY_FACTOR = 2.2; // fraction of
																// rebound
																// velocity
																// after spring
																// bounce
	public static final double CRACKED_VELOCITY_FACTOR = 0.8; // fraction of
																// rebound
																// velocity
																// after cracked
																// bounce
	public static final double ROCKET_VELOCITY_FACTOR = 10; // factor of
															// rebound
															// velocity
															// after contact
															// with rocket
	public static final double DOODLE_ROTATE_FACTOR = 7; // how many degrees per
															// call of handle
															// method the doodle
															// turns after
															// trampoline jump
	public static final double TEXT_OFFSET = 33; // used in determining x layout
													// of score counter label

	public static final double BLACK_HOLE_WIDTH = 75; // default width of black
														// hole ImageView
	public static final double BLACK_HOLE_INC = 18 + (int) (Math.random() * 4); // semi-random
																				// increment
																				// dictates
																				// how
																				// often
																				// black
																				// holes
																				// are
																				// added
	public static final double ROCKET_WIDTH = 50; // default width of rocket
													// ImageView
	public static final double ROCKET_INC = 8 + (int) (Math.random() * 3); // semi-random
																			// increment
																			// dictates
																			// how
																			// often
																			// rockets
																			// are
																			// added
	public static final double MONSTER_WIDTH = 77; // default width of monster
													// ImageView
	public static final double MONSTER_INC = 11 + (int) (Math.random() * 7); // semi-random
																				// increment
																				// dictates
																				// how
																				// often
																				// monsters
																				// are
																				// added
	public static final double FADE_OUT = 1.5; // duration, seconds, of
												// FadeTransition fade out
}
