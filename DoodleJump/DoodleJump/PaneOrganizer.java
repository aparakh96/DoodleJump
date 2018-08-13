package DoodleJump;

import javafx.scene.layout.*;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.event.*;
import javafx.scene.input.*;
import javafx.scene.text.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.animation.*;
import javafx.util.Duration;

/**
PaneOrganizer contains much of the app's broad graphical setup as well as a few essential EventHandlers that manage the
quit/rest functionality and the startup sequence that triggers the game after the key icons have faded.
*/
public class PaneOrganizer {
	private Pane _root;
	private Pane _doodlePane;
	private Button _quitButton;
	private Button _tryAgain;
	private Label _scoreCounter;
	private Image _background;
	private ImageView _backgroundViewer;
	private ImageView _keys;
	private Pane _gameOverPane;
	private Pane _whiteBackgroundPane;
	private DoodleGame _newGame;
	private App _appClass;
	private Stage _stage;

	/**
	PaneOrganizer's constructor, this method establishes an association with the App class and its stage (enabling the
	reset game functionality) and invokes the umbrella helper method buildNewGame(). This method generates graphics,
	the dynamic score counter label, a doodle, and an instance of the game logic class DoodleGame.
	*/
	public PaneOrganizer(App appClass, Stage stage) {
		_appClass = appClass;
		_stage = stage;
		this.buildNewGame();
	}

	/**
	The umbrella helper method, buildNewGame() generates the root pane for the entire application, and sets up the
	graphics/adds the elements referenced by the individual helper method names (in a specific order, ensuring that
	elements are visually arranged properly).
	*/
	private void buildNewGame() {
		_root = new Pane();
		this.setUpBackgroundImage();
		this.setUpQuitButton();
		this.setUpScore();
		this.setUpGameOver();
		_doodlePane = new Pane();
		Doodle doodle = new Doodle(_root, _doodlePane);
		doodle.getPane().addEventHandler(KeyEvent.KEY_PRESSED, new KeyQuitHandler());
		_newGame = new DoodleGame(_root, doodle, _scoreCounter, this);
		this.setUpKeys();
	}

	/**
	A simple accessor method that returns the root pane. Invoked by the App class and passed to the scene.
	*/
	public Pane getRoot() {
		return _root;
	}

//PRIVATE HELPER METHODS

	/**
	Helper method that generates a new quit button, links it to an EventHandler that calls System.exit(0) on action,
	and establishes a new ImageView to render the red x icon visible on said quit button.
	*/
	private void setUpQuitButton() {
		//RENDERS THE RED X TO BE ADDED TO QUIT BUTTON
		Image quitX = new Image("redx.png");
		ImageView quitViewer = new ImageView(quitX);
		quitViewer.setFitWidth(20);
		quitViewer.setPreserveRatio(true);
		quitViewer.setSmooth(true);
		quitViewer.setCache(true);

		//QUIT BUTTON
		_quitButton = new Button();
		_quitButton.setPrefSize(20, 20);
		_quitButton.setLayoutX(10);
		_quitButton.setLayoutY(10);
		_quitButton.setGraphic(quitViewer);
		_quitButton.setOnAction(new ButtonQuitHandler());

		_root.getChildren().add(_quitButton);
	}

	/**
	A helper method that generates, formats, and adds a new score counter label to be later dynamically updated in
	DoodleGame.
	*/
	private void setUpScore() {
		_scoreCounter = new Label();
		_scoreCounter.setFont(Font.font("Courier New", 35));
		_scoreCounter.setLayoutY(5);
		_scoreCounter.setTextFill(Color.BLACK);
		_scoreCounter.setLayoutX(Constants.SCENE_WIDTH - Constants.TEXT_OFFSET);

		_root.getChildren().add(_scoreCounter);
	}

	/**
	Yet another helper method, setUpGameOver() generates/formats the "GAME OVER!" label, the "Another Round?" reset Button
	(which it then links to the instance of the App class/stage through an ActionEvent EventHandler), and the white
	background pane that appears each time the game ends. It then adds these elements to the root, either directly, or
	through a sub pane. Sub panes are generated to ensure that the visibility of the elements can be easily manipulated
	in other classes.
	*/
	private void setUpGameOver() {
		//RESET BUTTON
		_tryAgain = new Button("ANOTHER ROUND?");
		_tryAgain.setFont(Font.font("Courier New", 15));
		_tryAgain.setPrefSize(150, 18);
		_tryAgain.setLayoutX(Constants.SCENE_WIDTH / 2 - 115);
		_tryAgain.setLayoutY(Constants.SCENE_HEIGHT / 2 + 8);
		_tryAgain.setOnAction(new ResetHandler());

		//"GAME OVER!" LABEL
		Label gameOver = new Label();
		gameOver.setFont(Font.font("Courier New", 35));
		gameOver.setLayoutX(Constants.SCENE_WIDTH / 2 - 143);
		gameOver.setLayoutY(Constants.SCENE_HEIGHT / 2 - 40);
		gameOver.setText("GAME OVER!");
		gameOver.setTextFill(Color.BLACK);

		//PANE TO CONTAIN THE PREVIOUS TWO ELEMENTS
		_gameOverPane = new Pane();
		_gameOverPane.setVisible(false);
		_gameOverPane.setLayoutX(40);
		_gameOverPane.setLayoutY(40);
		_gameOverPane.getChildren().addAll(gameOver, _tryAgain);

		//WHITE BACKGROUND PANE TO PROVIDE CONTRAST/DEFINITION FOR TEXT AT GAME END
		_whiteBackgroundPane = new Pane();
		_whiteBackgroundPane.setPrefSize(Constants.SCENE_WIDTH,
				Constants.SCENE_HEIGHT);
		_whiteBackgroundPane.setStyle("-fx-background-color: white;");
		_whiteBackgroundPane.setVisible(false);

		_root.getChildren().addAll(_whiteBackgroundPane, _gameOverPane);
	}

	/**
	A helper method used to load and render the graph paper background image scene in the game.
	*/
	private void setUpBackgroundImage() {
		_background = new Image("graphpaper.jpeg");
		_backgroundViewer = new ImageView(_background);
		_backgroundViewer.setFitWidth(Constants.SCENE_WIDTH + 52);
		_backgroundViewer.setPreserveRatio(true);
		_backgroundViewer.setSmooth(true);
		_backgroundViewer.setCache(true);
		_backgroundViewer.setOpacity(Constants.BACKGROUND_OPACITY);
		_backgroundViewer.setLayoutX(-10);
		_backgroundViewer.setLayoutY(0);
		_root.getChildren().add(_backgroundViewer);
	}

	private void setUpKeys() {
		_keys = new ImageView(new Image("kb.png"));
		_keys.setFitWidth(200);
		_keys.setLayoutX(Constants.SCENE_WIDTH / 2 - 100);
		_keys.setLayoutY(Constants.SCENE_HEIGHT / 2 + 120);
		_keys.setPreserveRatio(true);
		_keys.setSmooth(true);
		_keys.setCache(true);
		_root.getChildren().add(_keys);
		FadeTransition fadeKeys = new FadeTransition(Duration.seconds(5), _keys);
		fadeKeys.setFromValue(1.0);
		fadeKeys.setToValue(0.0);
		fadeKeys.play();
		fadeKeys.setOnFinished(new StartHandler());
	}

//GETTERS

	/**
	An accessor method that returns the "getGameOverPane" - useful in altering the pane's visibility in the DoodleGame
	class to ensure it functions in tandem with other elements in the DoodleGame game logic class;
	*/
	public Pane getGameOverPane() {
		return _gameOverPane;
	}

	/**
	An accessor method that returns the "whiteBackgroundPane" - useful in altering the pane's visibility in the DoodleGame
	class to ensure it functions in tandem with other elements in the DoodleGame game logic class;
	*/
	public Pane getWhiteBackgroundPane() {
		return _whiteBackgroundPane;
	}

	/**
	An accessor method that returns _quitButton - useful in adding/removing the button in the DoodleGame class to ensure
	it remains (accessible) above other elements added after the button's initialization.
	*/
	public Button getQuitButton() {
		return _quitButton;
	}

	/**
	An accessor method returning the ImageView rendering the arrow keys.
	*/
	public ImageView getKeys() {
		return _keys;
	}

	/**
	An accessor method returning the ImageView rendering the background - called when the background is reset to the
	image of space on rocket contact.
	*/
	public ImageView getBackgroundViewer() {
		return _backgroundViewer;
	}

	/**
	An accessor method used to return the background image to its inital graph paper appearance, set after the
	doodle begins to fall after a rocket launch.
	*/
	public Image getDefaultBackground() {
		return _background;
	}

//EVENTHANDLERS

	/**
	Handles user input (the Q key) to quit the program
	*/
	private class KeyQuitHandler implements EventHandler<KeyEvent> {

		/**
		On handle, kills the program by calling System.exit(0);
		*/
		@Override
		public void handle(KeyEvent event) {
			KeyCode keyPressed = event.getCode();
			if (keyPressed == KeyCode.Q) {
				System.exit(0);
				event.consume();
			}
		}

	}

	/**
	Handles user input (the quit button) to quit the program
	*/
	private class ButtonQuitHandler implements EventHandler<ActionEvent> {

		/**
		On handle, kills the program by calling System.exit(0);
		*/
		@Override
		public void handle(ActionEvent event) {
			System.exit(0);
			event.consume();
		}
	}

	/**
	Begins timeline when called by the setOnFinished method of the keys icon's FadeTransition.
	*/
	private class StartHandler implements EventHandler<ActionEvent> {

		/**
		Simply plays the vertical timeline (or timeline controlling the vertical motion of elements in the game), thus
		beginning the game.
		*/
		@Override
		public void handle(ActionEvent event) {
			_newGame.getTimeline().play();
			event.consume();
		}
	}

	/**
	Relies on instance variables "passed" into it, linked to an association with the App class, to effectively
	run a new instance of the entire app when the "Another round?" button is clicked.
	*/
	private class ResetHandler implements EventHandler<ActionEvent> {

		/**
		On handle, resets game by calling the app method to re-initiate the program.
		*/
		@Override
		public void handle(ActionEvent event) {
			_appClass.start(_stage);
			event.consume();
		}
	}

}
