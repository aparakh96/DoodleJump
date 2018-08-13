package DoodleJump;

import javafx.scene.layout.*;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;

/**
The doodle class models a doodle using an ImageView to render a graphic. Adds doodle to a pane and provides
a series of accessors and mutators to be used in the DoodleGame class when determining doodle's position.
*/
public class Doodle {
	private Pane _doodlePane;
	private Pane _root;
	private ImageView _doodleIconViewer;
	private Image _doodleIcon;

	/**
	A constructor that establishes an association with the root, and passes in a doodle pane to which the doodle
	will be added. Then invokes the helper method setUpDoodle() to graphically render the doodle.
	*/
	public Doodle(Pane rootPane, Pane doodlePane) {
		_root = rootPane;
		_doodlePane = doodlePane;
		this.setUpDoodle();
	}

	/**
	Generates an imageview and assigns it the doodle image. While it will be moved almost instantaneously
	in the doodle class, provides initialization layout and formatting.
	*/
	private void setUpDoodle() {
		_doodleIcon = new Image("doodleL.png");
		_doodleIconViewer = new ImageView(_doodleIcon);
		_doodleIconViewer.setFitWidth(Constants.CLASSIC_DOODLE_DIM);
		_doodleIconViewer.setPreserveRatio(true);
		_doodleIconViewer.setCache(true);
		_doodleIconViewer.setLayoutX(Constants.SCENE_WIDTH / 2
				- Constants.CLASSIC_DOODLE_DIM / 2);
		_doodleIconViewer.setLayoutY(Constants.SCENE_HEIGHT / 2);
		_doodlePane.getChildren().add(_doodleIconViewer);
		_root.getChildren().add(_doodlePane);
	}

	/**
	Accessor method that returns the doodle's "core" node, its ImageView.
	*/
	public ImageView getDoodleViewer() {
		return _doodleIconViewer;
	}

	/**
	Accessor method that returns the doodle's default image.
	*/
	public Image getDefaultDoodle() {
		return _doodleIcon;
	}

	/**
	Accessor method that returns the doodle's pane.
	*/
	public Pane getPane() {
		return _doodlePane;
	}

	/**
	Accessor method that returns the current x layout of the doodle.
	*/
	public double getCenterX() {
		return _doodleIconViewer.getLayoutX();
	}

	/**
	Accessor method that returns the current y layout of the doodle.
	*/
	public double getCenterY() {
		return _doodleIconViewer.getLayoutY();
	}

	/**
	Mutator method that sets the position of the doodle to the x and y doubles passed in.
	*/
	public void setPosition(double x, double y) {
		_doodleIconViewer.setLayoutX(x);
		_doodleIconViewer.setLayoutY(y);
	}
}
