package DoodleJump;

import javafx.scene.layout.*;
import javafx.animation.*;
import javafx.event.*;
import javafx.util.Duration;
import javafx.scene.input.*;
import java.util.ArrayList;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.Node;
import javafx.geometry.Bounds;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.*;

/**
DoodleGame is the engine of the whole application - among other corollary functions, it primarily establishes the
timelines controlling the vertical and horizontal motion of the game elements, generates platforms, rockets, monsters
and black holes in a semi-random manner, scrolls the screen and, critically, updates the doodle's position while
simultaneously checking for contact with all of the other elements added, and then responding accordingly.
*/
public class DoodleGame {
	private Pane _root;
	private PaneOrganizer _organizer;
	private Doodle _doodle;
	private Platform _topMostPlatform;
	private ArrayList<Platform> _myPlatforms;
	private Label _scoreCounter;
	private Timeline _verticalTimeline;
	private Timeline _lateralTimeline;
	private KeyFrame _moveLeft;
	private KeyFrame _moveRight;

	private ImageView _blackHoleViewer;
	private String _blackHoleStatus;
	private String _blackHoleContact;

	private ImageView _rocketViewer;
	private ImageView _thrustViewer;
	private String _rocketStatus;
	private FadeTransition _fadeThrust;

	private Timeline _monsterTimeline;
	private ImageView _monsterViewer;
	private String _monsterStatus;
	private Image _monsterType;

	private String _bounceType;
	private String _playStatus;
	private double _velocity;
	private double _yPositionFinal;
	private double _randomX;
	private int _counter;
	private int _currentScore;
	private LateralMoveInitiator _activateArrowKeys;
	private LateralMoveInhibitor _disconnectArrowKeys;

	/**
	DoodleGame's constructor establishes association with the game's doodle, the root pane, the scoreCounter label and
	the instance of the PaneOrganizer class. It also includes the initialization of some String instance variables which
	dictate game logic processes, particularly those that contain ___status. Such strings are often used in if statments
	throughout the program to determine interactions.
	*/
	public DoodleGame(Pane rootPane, Doodle doodle, Label scoreCounter,
			PaneOrganizer organizer) {
		_root = rootPane;
		_blackHoleContact = "FALSE";
		_organizer = organizer;
		_scoreCounter = scoreCounter;
		_currentScore = 0;
		_scoreCounter.setText(Integer.toString(_currentScore));
		_myPlatforms = new ArrayList<Platform>();
		_doodle = doodle;
		_doodle.getPane().setFocusTraversable(true);
		_activateArrowKeys = new LateralMoveInitiator();
		_disconnectArrowKeys = new LateralMoveInhibitor();
		_doodle.getPane().addEventHandler(KeyEvent.KEY_PRESSED,
				_activateArrowKeys);
		_doodle.getPane().addEventHandler(KeyEvent.KEY_RELEASED,
				_disconnectArrowKeys);
		_velocity = Constants.REBOUND_VELOCITY;
		_topMostPlatform = new Platform(_root, Constants.SCENE_WIDTH / 2,
				Constants.SCENE_HEIGHT / 2 - 100);
		_myPlatforms.add(_topMostPlatform);
		_counter = 0;
		_playStatus = "PLAYING";
		
		_thrustViewer = new ImageView(new Image("Images/thrust.png"));
		_thrustViewer.setOpacity(1.0);
		_thrustViewer.setFitWidth(50);
		_thrustViewer.setRotate(180);
		_thrustViewer.setPreserveRatio(true);
		_thrustViewer.setCache(true);
		_thrustViewer.setSmooth(true);
		
		_fadeThrust = new FadeTransition(Duration.seconds(9.5), _thrustViewer);
		_fadeThrust.setFromValue(1.0);
		_fadeThrust.setToValue(0.0);
		_fadeThrust.setOnFinished(new RemoveThrustHandler());
		
		this.generatePlatforms();
		this.setUpTimelines();
	}

	/**
	Generates timelines that control the vertical and horizontal movement of the doodle.
	*/
	private void setUpTimelines() {
		//ESTABLISHES VERTICAL TIMELINE
		_verticalTimeline = new Timeline();
		KeyFrame doodleUnderGravity = new KeyFrame(
				Duration.seconds(Constants.DURATION), new BounceHandler());
		_verticalTimeline.getKeyFrames().add(doodleUnderGravity);
		_verticalTimeline.setCycleCount(Animation.INDEFINITE);

		//ESTABLISHES HORIZONTAL TIMELINE
		_lateralTimeline = new Timeline();
		_moveLeft = new KeyFrame(Duration.seconds(Constants.DURATION),
				new LateralMoveHandler("LEFT"));
		_moveRight = new KeyFrame(Duration.seconds(Constants.DURATION),
				new LateralMoveHandler("RIGHT"));
		_lateralTimeline.setCycleCount(Animation.INDEFINITE);

		_monsterTimeline = new Timeline();
		_monsterTimeline.setCycleCount(Animation.INDEFINITE);
	}

	/**
	An accessor method that returns the vertical timeline (enabling other classes to stop/play it in their
	implementation).
	*/
	public Timeline getTimeline() {
		return _verticalTimeline;
	}

	/**
	Semi-randomly generates platforms to be added to the game - the randomness of platform type is dealt within the
	Platform class, but this method dictates the position of the new platform in relation to its predecessor in such a way
	that ensures its lateral and vertical placement will be within reach of the doodle on the platform below.
	*/
	public void generatePlatforms() {
		//REMOVES ALL THE LISTED ELEMENTS TO PREEMPT A DUPLICATE PANE ERROR UPON RE-ADDING THEM AT THE END OF THE METHOD
		_root.getChildren().remove(_doodle.getPane());
		_root.getChildren().remove(_organizer.getWhiteBackgroundPane());
		_root.getChildren().remove(_organizer.getQuitButton());
		_root.getChildren().remove(_organizer.getGameOverPane());
		//A WHILE LOOP THAT GENERATES A PLATFORM WWHILE THE TOPMOST PLATFORM MOVES DOWN TO BECOME VISIBLE ON SCREEN
		while (_topMostPlatform.getY() > 0) {
			double referenceX = _topMostPlatform.getX();
			double referenceY = _topMostPlatform.getY();
			double maxDisplacementY = referenceY - 300;
			double minDisplacementY = referenceY - 75;
			double maxDisplacementX = referenceX + 200;
			double minDisplacementX = referenceX - 200;
			_randomX = minDisplacementX
					+ (int) ((maxDisplacementX - minDisplacementX + 1) * Math
							.random());
			double randomY = minDisplacementY
					+ (int) ((maxDisplacementY - minDisplacementY + 1) * Math
							.random());
			double randomXInBounds = (int) (Math.random() * 100);
			if (_randomX <= 30) {
				_randomX = 30 + randomXInBounds;
			}
			if (_randomX >= Constants.SCENE_WIDTH - Constants.PLATFORM_WIDTH
					- 15) {
				_randomX = Constants.SCENE_WIDTH - Constants.PLATFORM_WIDTH
						- 30 - randomXInBounds;
			}
			_topMostPlatform = new Platform(_root, _randomX, randomY);
			_myPlatforms.add(_topMostPlatform);
		}
		//RE-ADDS THE ELEMENTS FROM THE BEGINNING OF THE METHOD TO ENSURE THEY GRAPHICALLY REMAIN IN FRONT OF THE PLATFORMS
		_root.getChildren().add(_doodle.getPane());
		_root.getChildren().add(_organizer.getWhiteBackgroundPane());
		_root.getChildren().add(_organizer.getQuitButton());
		_root.getChildren().add(_organizer.getGameOverPane());
	}

	/**
	A method that interprets the Doodle's position above the midpoint as a distance by which to move the rest of the
	game's elements down, creating a scrolling illusion or effect.
	*/
	public void scroll() {
		//REMOVES ELEMENTS THAT HAVE MOVED OFFSCREEN
		for (int i = 0; i < _myPlatforms.size(); i++) {
			if (_myPlatforms.get(i).getY() > Constants.SCENE_HEIGHT) {
				_myPlatforms.remove(i);
				this.generatePlatforms();
			}
			if (_blackHoleStatus == "ADDED") {
				if (_blackHoleViewer.getLayoutY() > Constants.SCENE_HEIGHT) {
					_root.getChildren().remove(_blackHoleViewer);
					_blackHoleStatus = "REMOVED";
				}
			}
			if (_monsterStatus == "ADDED") {
				if (_monsterViewer.getLayoutY() > Constants.SCENE_HEIGHT) {
					_root.getChildren().remove(_monsterViewer);
					_monsterStatus = "REMOVED";
					_monsterTimeline.stop();
				}
			}
		}
		//SHIFTS ELEMENTS DOWN BY THE DIFFERENCE BETWEEN THE DOODLE AND THE VETICAL MIDPOINT
		if (_doodle.getCenterY() < Constants.SCENE_HEIGHT / 2 & _velocity < 0) {
			double difference = Constants.SCENE_HEIGHT / 2 - _doodle.getCenterY();
			for (Platform thisPlatform : _myPlatforms) {
				thisPlatform.setPosition(thisPlatform.getX(),
						thisPlatform.getY() + difference);
				if (_blackHoleStatus == "ADDED") {
					_blackHoleViewer.setLayoutY(_blackHoleViewer.getLayoutY()
							+ difference / 4);
				}
				if (_rocketStatus == "ADDED") {
					_rocketViewer.setLayoutY(_rocketViewer.getLayoutY()
							+ difference / 4);
				}
				if (_monsterStatus == "ADDED") {
					_monsterViewer.setLayoutY(_monsterViewer.getLayoutY()
							+ difference / 4);
				}
			}
			//PRESERVES SCORE COUNTER'S VISUAL FORMATTING AS ITS VALUE GROWS
			_currentScore = _currentScore + (int) difference;
			_scoreCounter.setText(Integer.toString(_currentScore));
			if (_currentScore >= 0 && _currentScore < 10) {
				_scoreCounter.setLayoutX(Constants.SCENE_WIDTH
						- Constants.TEXT_OFFSET);
			} else if (_currentScore >= 10 && _currentScore < 100) {
				_scoreCounter.setLayoutX(Constants.SCENE_WIDTH
						- Constants.TEXT_OFFSET - 20);
			} else if (_currentScore >= 100 && _currentScore < 1000) {
				_scoreCounter.setLayoutX(Constants.SCENE_WIDTH
						- Constants.TEXT_OFFSET - 40);
			} else if (_currentScore >= 1000 && _currentScore < 10000) {
				_scoreCounter.setLayoutX(Constants.SCENE_WIDTH
						- Constants.TEXT_OFFSET - 60);
			} else if (_currentScore >= 10000 && _currentScore < 100000) {
				_scoreCounter.setLayoutX(Constants.SCENE_WIDTH
						- Constants.TEXT_OFFSET - 80);
			} else if (_currentScore >= 100000 && _currentScore < 1000000) {
				_scoreCounter.setLayoutX(Constants.SCENE_WIDTH
						- Constants.TEXT_OFFSET - 100);
			} else if (_currentScore >= 1000000 && _currentScore < 10000000) {
				_scoreCounter.setLayoutX(Constants.SCENE_WIDTH
						- Constants.TEXT_OFFSET - 120);
			}
			_doodle.setPosition(_doodle.getCenterX(),	Constants.SCENE_HEIGHT / 2);
		}
	}

	/**
	The primary EventHandler class in the entire application, this handle method is invoked by the KeyFrame added to the
	vertical timeline and updates the doodle's position
	*/
	private class BounceHandler implements EventHandler<ActionEvent> {
		private String _fallStatus;
		private String _launchStatus;

		/**
		Initializes fall status as false to ensure that the method checks for contacts between the doodle and platforms
		as well as among all elements in the game. If fall status is true, for example, after the doodle contacts a
		monster, the doodle will no longer be able to collide with the platforms.
		*/
		private BounceHandler() {
			_fallStatus = "FALSE";
		}

		/**
		The handle method, effectively passes all the work off to a "helper" method immediately below.
		*/
		@Override
		public void handle(ActionEvent event) {
			this.updateDoodleVertically();
			event.consume();
		}

		/**
		This method it its essence simply updates the vertical position of the doodle based on the current velocity -
		the complications come from the generation of monsters, rockets and black holes, the checking of contact between
		the doodle and any of those elements, and then the manipulation of velocity and other paramaters in a manner
		specific to each element (both within different platform types and between platforms, black holes, rockets and
		monsters). After all of this has occured, the method determines the new position and trajectory of the doodle.
		*/
		public void updateDoodleVertically() {
			//A check to ensure that contact is processed only if the string is actively switched
			String contact = "FALSE";
			//Checks contact between all platforms in the platform ArrayList and the doodle, and then manipulates velocity
			//depending on the type of platform contacted.
			for (int i = 0; i < _myPlatforms.size(); i++) {
				ImageView thisPlatformNode = _myPlatforms.get(i)
						.getPlatformViewer();
				Node thisDoodleNode = _doodle.getDoodleViewer();
				//Means of putting local platform bounds in terms of local *doodle* bounds, to enable the local bounds based
				//intersects method called on doodle to check for contact with the platform ImageViews.
				Bounds platformToDoodleBounds = thisDoodleNode
						.sceneToLocal(thisPlatformNode
								.localToScene(thisPlatformNode
										.getBoundsInLocal()));
				//If fall status is true, i.e., after an intersection with a monster, the method will no longer check for
				//contact between the doodle and the platforms, ensuring the game ends.
				if (_fallStatus == "FALSE") {
					if (_velocity > 0
							&& thisDoodleNode
									.intersects(platformToDoodleBounds)) {
						_counter = _counter + 1;
						if (_myPlatforms.get(i).getType().equals("NORMAL")) {
							_velocity = Constants.REBOUND_VELOCITY;
							_bounceType = "NORMAL";
						} else if (_myPlatforms.get(i).getType()
								.equals("SPRING")) {
							_velocity = Constants.REBOUND_VELOCITY
									* Constants.SPRING_VELOCITY_FACTOR;
							_bounceType = "SPRING";
						} else if (_myPlatforms.get(i).getType()
								.equals("TRAMP")) {
							_velocity = Constants.REBOUND_VELOCITY
									* Constants.TRAMP_VELOCITY_FACTOR;
							_bounceType = "TRAMP";
						} else if (_myPlatforms.get(i).getType()
								.equals("SHAKY")) {
							_velocity = Constants.REBOUND_VELOCITY;
							_bounceType = "SHAKY";
							//Platform vanishes at impact
							_root.getChildren().remove(_myPlatforms.get(i).getPlatformViewer());
						} else if (_myPlatforms.get(i).getType()
								.equals("CRACKED")) {
							_velocity = Constants.REBOUND_VELOCITY
									* Constants.CRACKED_VELOCITY_FACTOR;
							_bounceType = "CRACKED";
						}
						//Uses semi-randomly defined static constants as increments that dictate how often elements are added.
						//Counter refers to the number of times the doodle has hit a platform.
						if (_counter % Constants.BLACK_HOLE_INC == 0) {
							this.addBlackHole();
						}
						if (_counter % Constants.ROCKET_INC == 0) {
							this.addRocket();
						}
						if (_counter % Constants.MONSTER_INC == 0) {
							this.addMonster();
						}
						contact = "TRUE";
					} else {
						contact = "FALSE";
					}
				}
				//Determines what to do if the doodle intersects with a black hole. The first 'if' line is simply intended to
				//avoid a null pointer exception if no black hole is currently added to the game, and ensure that a launched
				//doodle is immune to the black hole.
				if (_blackHoleStatus == "ADDED" && _launchStatus != "TRUE") {
					//Means of putting local black hole bounds in terms of local *doodle* bounds, to enable the local bounds based
					//intersects method called on doodle to check for contact with the black hole ImageView.
					Bounds blackHoleToDoodleBounds = thisDoodleNode
							.sceneToLocal(_blackHoleViewer
									.localToScene(_blackHoleViewer
											.getBoundsInLocal()));
					if (thisDoodleNode.intersects(blackHoleToDoodleBounds)) {
						//If contact actually occurs, stops all timelines, disconnects the keyboard keys from the game, causes the
						//doodle to shrink until it appears to have vanished, and shows the end "game over" screen.
						_blackHoleContact = "TRUE";
						_verticalTimeline.stop();
						_lateralTimeline.stop();
						double bHX = _blackHoleViewer.getLayoutX();
						double bHY = _blackHoleViewer.getLayoutY();
						_doodle.setPosition(bHX + 15, bHY + 45);
						_doodle.getPane().removeEventHandler(
								KeyEvent.KEY_PRESSED, _activateArrowKeys);
						_doodle.getPane().removeEventHandler(
								KeyEvent.KEY_RELEASED, _disconnectArrowKeys);
						ScaleTransition blackHoleShrink = new ScaleTransition(
								Duration.seconds(Constants.FADE_OUT),
								thisDoodleNode);
						blackHoleShrink.setFromX(1);
						blackHoleShrink.setFromY(1);
						blackHoleShrink.setToX(1 / 2);
						blackHoleShrink.setToY(1 / 2);
						blackHoleShrink.play();
						DoodleGame.this.showEndScreen();
					}
				}
				if (_monsterStatus == "ADDED" && _launchStatus != "TRUE") {
					//Same as immediately above, but simply for the monster ImageView.
					Bounds monsterToDoodleBounds = thisDoodleNode
							.sceneToLocal(_monsterViewer
									.localToScene(_monsterViewer
									.getBoundsInLocal()));
					if (thisDoodleNode.intersects(monsterToDoodleBounds)) {
						//An actively falling doodle
						_velocity = 100;
						//Prevents chance contact with platforms on the way down
						_fallStatus = "TRUE";
						_monsterTimeline.stop();
					}
				}
				if (_rocketStatus == "ADDED") {
					//Same bounds conversion as the above two counterparts
					Bounds rocketToDoodleBounds = thisDoodleNode
							.sceneToLocal(_rocketViewer
									.localToScene(_rocketViewer
											.getBoundsInLocal()));
					if (thisDoodleNode.intersects(rocketToDoodleBounds)) {
						_launchStatus = "TRUE";
						_velocity = Constants.REBOUND_VELOCITY * Constants.ROCKET_VELOCITY_FACTOR;
						_root.getChildren().remove(_rocketViewer);
						_thrustViewer.setLayoutY(Constants.SCENE_HEIGHT / 2 + 31);
						_thrustViewer.setLayoutX(_doodle.getDoodleViewer().getLayoutX());
						_root.getChildren().add(_thrustViewer);
						_rocketStatus = "REMOVED";
						_playStatus = "PAUSED";
						_fadeThrust.play();
						_scoreCounter.setTextFill(Color.WHITE);
						Image space = new Image("Images/space.jpg");
						_organizer.getBackgroundViewer().setImage(space);
						_organizer.getBackgroundViewer().setFitWidth(
								Constants.SCENE_WIDTH + 100);
						_organizer.getBackgroundViewer().setOpacity(0.6);
						_doodle.getDoodleViewer().setImage(
								_doodle.getDefaultDoodle());
						_doodle.getDoodleViewer().setRotate(0);
						_doodle.getDoodleViewer().setRotate(
								_doodle.getDoodleViewer().getRotate() + 90);
					}
				}
				if (_rocketStatus == "REMOVED" & _velocity > -600) {
					_playStatus = "PLAYING";
				}
				if (_rocketStatus == "REMOVED" & _velocity > 0) {
					_launchStatus = "FALSE";
					_organizer.getBackgroundViewer().setImage(
							_organizer.getDefaultBackground());
					_organizer.getBackgroundViewer().setFitWidth(
							Constants.SCENE_WIDTH + 52);
					_organizer.getBackgroundViewer().setOpacity(
							Constants.BACKGROUND_OPACITY);
					_scoreCounter.setTextFill(Color.BLACK);
					_doodle.getDoodleViewer().setRotate(0);
					_playStatus = "PLAYING";
				}
			}
			if (_bounceType == "TRAMP") {
				if (_velocity < 0
						&& _doodle.getDoodleViewer().getRotate() < 355) {
					_doodle.getDoodleViewer().setRotate(
							_doodle.getDoodleViewer().getRotate()
									+ Constants.DOODLE_ROTATE_FACTOR);
				}
				if (_doodle.getDoodleViewer().getRotate() == 357) {
					_doodle.getDoodleViewer().setRotate(
							_doodle.getDoodleViewer().getRotate() + 3);
				}
				if (_velocity > 0
						&& _doodle.getDoodleViewer().getRotate() == 360) {
					_doodle.getDoodleViewer().setRotate(0);
				}
			}
			//********** WATERSHED POINT - If no contact of any kind ocurred, here's what to do:
			if (contact == "FALSE") {
				_velocity = (_velocity + (Constants.GRAVITY * Constants.DURATION));
			}
			_yPositionFinal = _doodle.getCenterY()
					+ (_velocity * Constants.DURATION)
					+ (0.5 * Constants.GRAVITY * (Constants.DURATION * Constants.DURATION));
			_doodle.setPosition(_doodle.getCenterX(), _yPositionFinal);
			DoodleGame.this.scroll();
			//Checks to see if the doodle has fallen below the lower boundary (graphically) of the screen.
			//If so, ends the game.
			if (_doodle.getCenterY() > Constants.SCENE_HEIGHT) {
				DoodleGame.this.showEndScreen();
				_fallStatus = "TRUE";
			}
		}

		/**
		A helper method that loads, renders and formats the black hole image. X layout is based loosely on
		platform position to ensure the two don't overlap.
		*/
		private void addBlackHole() {
			Image blackHole = new Image("Images/blackhole.png");
			_blackHoleViewer = new ImageView(blackHole);
			_blackHoleViewer.setFitWidth(Constants.BLACK_HOLE_WIDTH);
			_blackHoleViewer.setPreserveRatio(true);
			_blackHoleViewer.setSmooth(true);
			_blackHoleViewer.setCache(true);
			if (_randomX > Constants.SCENE_WIDTH / 2) {
				_blackHoleViewer.setLayoutX(_randomX - 80);
			} else if (_randomX < Constants.SCENE_WIDTH / 2) {
				_blackHoleViewer.setLayoutX(_randomX + 80);
			}
			_blackHoleViewer.setLayoutY(-100);
			_blackHoleStatus = "ADDED";
			_root.getChildren().add(_blackHoleViewer);
		}

		/**
		A helper method that loads, renders and formats the rocket image. X layout is based loosely on
		platform position to ensure the two don't overlap.
		*/
		private void addRocket() {
			Image rocket = new Image("Images/rocket.png");
			_rocketViewer = new ImageView(rocket);
			_rocketViewer.setFitWidth(Constants.ROCKET_WIDTH);
			_rocketViewer.setPreserveRatio(true);
			_rocketViewer.setSmooth(true);
			_rocketViewer.setCache(true);
			_rocketViewer.setRotate(30);
			if (_randomX > Constants.SCENE_WIDTH / 2) {
				_rocketViewer.setLayoutX(_randomX - 120);
			} else if (_randomX < Constants.SCENE_WIDTH / 2) {
				_rocketViewer.setLayoutX(_randomX + 120);
			}
			_rocketViewer.setLayoutY(-100);
			_rocketStatus = "ADDED";
			_root.getChildren().add(_rocketViewer);
		}

		/**
		A helper method that loads, renders and formats the monster images. Unlike previous two, however, also randomly
		selects a monster image to portray. X layout is based loosely on platform position to ensure the two don't overlap.
		*/
		private void addMonster() {
			_monsterViewer = new ImageView();
			_monsterViewer.setFitWidth(Constants.MONSTER_WIDTH);
			_monsterViewer.setPreserveRatio(true);
			_monsterViewer.setSmooth(true);
			_monsterViewer.setCache(true);
			if (_randomX > Constants.SCENE_WIDTH / 2) {
				_monsterViewer.setLayoutX(_randomX - 40);
			} else if (_randomX < Constants.SCENE_WIDTH / 2) {
				_monsterViewer.setLayoutX(_randomX + 40);
			}
			_monsterViewer.setLayoutY(-700);
			_monsterStatus = "ADDED";
			_root.getChildren().add(_monsterViewer);
			//Six monsters in total, each with a "left" and a "right" image (flipped horizontally).
			//Array lets the semi-randomly generated direction int and the randomly generated monster type int
			//combine to select the proper image.
			Image[][] monsterImages = new Image[2][6];
			//"Left" images
			monsterImages[0][0] = new Image("Images/fangsL.png");
			monsterImages[0][1] = new Image("Images/grouperL.png");
			monsterImages[0][2] = new Image("Images/ninjaL.png");
			monsterImages[0][3] = new Image("Images/santaL.png");
			monsterImages[0][4] = new Image("Images/squidL.png");
			monsterImages[0][5] = new Image("Images/zombieL.png");
			//"Right" images
			monsterImages[1][0] = new Image("Images/fangsR.png");
			monsterImages[1][1] = new Image("Images/grouperR.png");
			monsterImages[1][2] = new Image("Images/ninjaR.png");
			monsterImages[1][3] = new Image("Images/santaR.png");
			monsterImages[1][4] = new Image("Images/squidR.png");
			monsterImages[1][5] = new Image("Images/zombieR.png");
			String movingDirection = null;
			int directionInt = 0;
			//Ensures initial movement covers larger half of the screen (if monster's on the right, move left, converse true)
			if (_monsterViewer.getLayoutX() > Constants.SCENE_WIDTH
					- Constants.MONSTER_WIDTH / 2) {
				movingDirection = "LEFT";
				directionInt = 0;
			} else if (_monsterViewer.getLayoutX() <= Constants.SCENE_WIDTH
					- Constants.MONSTER_WIDTH / 2) {
				movingDirection = "RIGHT";
				directionInt = 1;
			}
			//Integer determines which "species" of monster chosen
			int monsterInt = (int) (Math.random() * 6);
			//Specific monster image (1 of 12) is selected, ensuring that the monster type picked is facing
			//in the direction assigned.
			_monsterType = monsterImages[directionInt][monsterInt];
			//Selected image is added to the monster ImageView
			_monsterViewer.setImage(_monsterType);
			_monsterTimeline.getKeyFrames().clear();
			//Semi-randomly determines the monster's velocity
			int speedFactor = 1 + (int) (Math.random() * 6);
			KeyFrame pacing = new KeyFrame(Duration.seconds(Constants.DURATION
					* speedFactor / 2),
					new MonsterPacerHandler(movingDirection));
			_monsterTimeline.getKeyFrames().add(pacing);
			//Determines whether or not the monster will be static or mobile (favoring mobile)
			int movementInt = (int) (Math.random() * 3);
			switch (movementInt) {
			case 0:
				_monsterTimeline.stop();
				break;
			case 1:
			case 2:
				_monsterTimeline.play();
				break;
			}
		}
	}

	/**
	This method really pertains to formatting of graphical elements to ensure that the end of game screen has
	a visible score label and clickable buttons. Also, adds a white background pane for visibility and stops the
	vertical timeline to ensure the game ends properly.
	*/
	public void showEndScreen() {
		_verticalTimeline.stop();
		_organizer.getWhiteBackgroundPane().setVisible(true);
		if (_blackHoleStatus == "ADDED") {
			_root.getChildren().remove(_blackHoleViewer);
		}
		_root.getChildren().removeAll(_doodle.getPane(),
				_organizer.getWhiteBackgroundPane(),
				_organizer.getGameOverPane(), _organizer.getQuitButton(),
				_scoreCounter);
		_root.getChildren().add(_organizer.getWhiteBackgroundPane());
		_scoreCounter.setTextFill(Color.RED);
		_scoreCounter.setFont(Font.font("Courier New", FontWeight.BOLD, 35));
		_scoreCounter.setLayoutX(_scoreCounter.getLayoutX() - 27);
		_root.getChildren().add(_scoreCounter);
		if (_blackHoleStatus == "ADDED" && _blackHoleContact == "TRUE") {
			_root.getChildren().add(_blackHoleViewer);
		}
		_root.getChildren().add(_doodle.getPane());
		_organizer.getGameOverPane().setLayoutY(-20);
		//Preserves formatting of score label
		if (_currentScore >= 0 && _currentScore < 10) {
			_scoreCounter.setLayoutX(Constants.SCENE_WIDTH
					- Constants.TEXT_OFFSET);
		} else if (_currentScore >= 10 && _currentScore < 100) {
			_scoreCounter.setLayoutX(Constants.SCENE_WIDTH
					- Constants.TEXT_OFFSET - 20);
		} else if (_currentScore >= 100 && _currentScore < 1000) {
			_scoreCounter.setLayoutX(Constants.SCENE_WIDTH
					- Constants.TEXT_OFFSET - 40);
		} else if (_currentScore >= 1000 && _currentScore < 10000) {
			_scoreCounter.setLayoutX(Constants.SCENE_WIDTH
					- Constants.TEXT_OFFSET - 60);
		} else if (_currentScore >= 10000 && _currentScore < 100000) {
			_scoreCounter.setLayoutX(Constants.SCENE_WIDTH
					- Constants.TEXT_OFFSET - 80);
		} else if (_currentScore >= 100000 && _currentScore < 1000000) {
			_scoreCounter.setLayoutX(Constants.SCENE_WIDTH
					- Constants.TEXT_OFFSET - 100);
		} else if (_currentScore >= 1000000 && _currentScore < 10000000) {
			_scoreCounter.setLayoutX(Constants.SCENE_WIDTH
					- Constants.TEXT_OFFSET - 120);
		}
		_root.getChildren().addAll(_organizer.getGameOverPane(),
				_organizer.getQuitButton());
		_organizer.getGameOverPane().setVisible(true);
	}

	/**
	Connected to the monster timeline, this private EventHandler processes the movement of the monster
	and receives a string to dictate the direction.
	*/
	private class MonsterPacerHandler implements EventHandler<ActionEvent> {
		private String _direction;

		/**
		Constructor establishes association
		*/
		public MonsterPacerHandler(String direction) {
			_direction = direction;
		}

		/**
		The handle method uses if statements to determine direction if incremented layout shifting, based on the association
		with the string initially passed to it.
		*/
		@Override
		public void handle(ActionEvent event) {
			if (_direction == "RIGHT") {
				_monsterViewer.setLayoutX(_monsterViewer.getLayoutX() + 2);
				if (_monsterViewer.getLayoutX() >= Constants.SCENE_WIDTH) {
					_monsterViewer.setLayoutX(0 - Constants.MONSTER_WIDTH);
				}
			} else if (_direction == "LEFT") {
				_monsterViewer.setLayoutX(_monsterViewer.getLayoutX() - 2);
				if (_monsterViewer.getLayoutX() <= 0 - Constants.MONSTER_WIDTH) {
					_monsterViewer.setLayoutX(Constants.SCENE_WIDTH);
				}
			}
			event.consume();
		}
	}

	/**
	This private EventHandler handles user keyboard input that dictates both lateral doodle movement and pause & play
	*/
	private class LateralMoveInitiator implements EventHandler<KeyEvent> {
		private Pane _pausePane;
		private Pane _playPane;
		private FadeTransition _fadePlay;
		private FadeTransition _fadePause;

		/**
		*/
		@Override
		public void handle(KeyEvent event) {
			KeyCode keyPressed = event.getCode();
			this.setUpPauseAndPlay();
			//Move left
			if (keyPressed == KeyCode.LEFT) {
				if (_playStatus == "PLAYING") {
					Image doodleL = new Image("Images/doodleL.png");
					_doodle.getDoodleViewer().setImage(doodleL);
					_lateralTimeline.stop();
					_lateralTimeline.getKeyFrames().clear();
					_lateralTimeline.getKeyFrames().add(_moveLeft);
					_lateralTimeline.play();
				}
			//Move right
			} else if (keyPressed == KeyCode.RIGHT) {
				if (_playStatus == "PLAYING") {
					Image doodleR = new Image("Images/doodleR.png");
					_doodle.getDoodleViewer().setImage(doodleR);
					_lateralTimeline.stop();
					_lateralTimeline.getKeyFrames().clear();
					_lateralTimeline.getKeyFrames().add(_moveRight);
					_lateralTimeline.play();
				}
			//Pause
			} else if (keyPressed == KeyCode.DOWN) {
				_root.getChildren().remove(_organizer.getQuitButton());
				_verticalTimeline.stop();
				_playStatus = "PAUSED";
				_pausePane.setVisible(true);
				_pausePane.setOpacity(1.0);
				_fadePause.play();
				_root.getChildren().add(_organizer.getQuitButton());
				event.consume();
			//Play
			} else if (keyPressed == KeyCode.UP) {
				_root.getChildren().remove(_organizer.getKeys());
				_root.getChildren().remove(_organizer.getQuitButton());
				_verticalTimeline.play();
				_playStatus = "PLAYING";
				_pausePane.setVisible(false);
				_playPane.setVisible(true);
				_fadePlay.play();
				_root.getChildren().add(_organizer.getQuitButton());
				event.consume();
			}
		}

		/**
		Helper method within the private class that generates the graphical pause and play buttons that appear onscreen
		as well as the fade transitions that run each time the up or down keys are pressed.
		*/
		private void setUpPauseAndPlay() {
			Image paused = new Image("Images/paused.png");
			ImageView pauseViewer = new ImageView(paused);
			pauseViewer.setSmooth(true);
			pauseViewer.setCache(true);
			pauseViewer.setPreserveRatio(true);
			pauseViewer.setFitWidth(80);
			pauseViewer.setLayoutX(Constants.SCENE_WIDTH / 2 - 40);
			pauseViewer.setLayoutY(Constants.SCENE_HEIGHT / 2 - 40);
			pauseViewer.setOpacity(0.0);
			_pausePane = new Pane();
			_pausePane.getChildren().add(pauseViewer);
			_pausePane.setVisible(false);
			_root.getChildren().add(_pausePane);
			Image play = new Image("Images/play.png");

			ImageView playViewer = new ImageView(play);
			playViewer.setSmooth(true);
			playViewer.setCache(true);
			playViewer.setPreserveRatio(true);
			playViewer.setFitWidth(80);
			playViewer.setLayoutX(Constants.SCENE_WIDTH / 2 - 40);
			playViewer.setLayoutY(Constants.SCENE_HEIGHT / 2 - 40);
			_playPane = new Pane();
			_playPane.getChildren().add(playViewer);
			_playPane.setVisible(false);
			_root.getChildren().add(_playPane);

			_fadePlay = new FadeTransition(Duration.seconds(0.8), playViewer);
			_fadePlay.setFromValue(1.0);
			_fadePlay.setToValue(0.0);

			_fadePause = new FadeTransition(Duration.seconds(0.8), pauseViewer);
			_fadePause.setFromValue(1.0);
			_fadePause.setToValue(0.0);
		}

	}

	/**
	Ensures user control over lateral movement of doodle by stopping lateral timeline on key release
	*/
	private class LateralMoveInhibitor implements EventHandler<KeyEvent> {

		/**
		When either the left or right key is released, the lateral timeline is stopped, until it is
		reinitiated with a fresh keypress.
		*/
		@Override
		public void handle(KeyEvent event) {
			KeyCode keyReleased = event.getCode();
			if (keyReleased == KeyCode.LEFT | keyReleased == KeyCode.RIGHT) {
				_lateralTimeline.stop();
			}
			event.consume();
		}
	}
	
	private class RemoveThrustHandler implements EventHandler<ActionEvent> {
		
		@Override
		public void handle(ActionEvent event) {
			_root.getChildren().remove(_thrustViewer);
		}
	
	}

	/**
	Handles the technical lateral movement of the doodle.
	*/
	private class LateralMoveHandler implements EventHandler<ActionEvent> {
		private String _direction;

		/**
		Constructor recieves a string as a direction assignment, and creates an association
		*/
		public LateralMoveHandler(String direction) {
			_direction = direction;
		}

		/**
		Depending on the direction input received, calls on the appropriate helper method.
		*/
		@Override
		public void handle(ActionEvent event) {
			if (_direction == "LEFT") {
				this.moveLeft();
			} else if (_direction == "RIGHT") {
				this.moveRight();
			}
			event.consume();
		}

		/**
		Directly moves the doodle imageview  left by a constant-defined increment each time handle is called
		*/
		public void moveLeft() {
			if (_doodle.getCenterX() <= 0) {
				_doodle.setPosition(Constants.SCENE_WIDTH, _doodle.getCenterY());
			}
			_doodle.setPosition(_doodle.getCenterX() - Constants.LAT_PXL_INCR,
					_doodle.getCenterY());
		}

		/**
		Directly moves the doodle imageview right by a constant-defined increment each time handle is called
		*/
		public void moveRight() {
			if (_doodle.getCenterX() >= Constants.SCENE_WIDTH) {
				_doodle.setPosition(0, _doodle.getCenterY());
			}
			_doodle.setPosition(_doodle.getCenterX() + Constants.LAT_PXL_INCR,
					_doodle.getCenterY());
		}
	}

}
