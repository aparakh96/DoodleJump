package DoodleJump;

import javafx.scene.layout.*;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;

/**
Like the doodle clas, this platform class models a game element with an ImageView, and shares essentially
all aspects except for the images used and the fact that they are randomly selected.
*/
public class Platform {
	private Pane _root;
	private double _xLoc;
	private double _yLoc;
	private ImageView _platformIconViewer;
	private Image _platformType;
	private String _platformTypeText;

	/**
	Constructor that establishes an association with the root pane and the position coordinates passed to each new
	platform in DoodleGame's generatePlatforms method. Then, calls private helper method used to select and
	grapically render the platform.
	*/
	public Platform(Pane rootPane, double x, double y) {
		_root = rootPane;
		_xLoc = x;
		_yLoc = y;
		this.setUpPlatforms();
	}

	/**
	Loads a series of images that correspond to different platform types. Then, using a weighted switch statement,
	selects an image to be added to the platform ImageView. The platform type text is used as an identifier in the
	DoodleGame class to determine how the doodle should interact with the platform in question if the two intersect.
	*/
	private void setUpPlatforms() {
		int platformInt = (int) (Math.random() * 20);
		_platformType = null;
		Image normalPlatform = new Image("normalplatform.png");
		Image springPlatform = new Image("springplatform.png");
		Image trampPlatform = new Image("trampplatform.png");
		Image shakyPlatform = new Image("shakyplatform.png");
		Image crackedPlatform = new Image("crackedplatform.png");
		switch (platformInt) {
		case 0:
		case 1:
		case 2:
		case 3:
		case 4:
		case 5:
		case 6:
		case 7:
		case 8:
		case 9:
		case 19:
			_platformType = normalPlatform;
			_platformTypeText = "NORMAL";
			break;
		case 10:
		case 11:
			_platformType = springPlatform;
			_platformTypeText = "SPRING";
			break;
		case 12:
		case 13:
		case 14:
			_platformType = trampPlatform;
			_platformTypeText = "TRAMP";
			break;
		case 15:
		case 16:
		case 17:
			_platformType = shakyPlatform;
			_platformTypeText = "SHAKY";
			break;
		case 18:
			_platformType = crackedPlatform;
			_platformTypeText = "CRACKED";
			break;
		default:
			_platformType = normalPlatform;
			_platformTypeText = "NORMAL";
			break;
		}
		_platformIconViewer = new ImageView(_platformType);
		_platformIconViewer.setFitWidth(Constants.PLATFORM_WIDTH + 15);
		_platformIconViewer.setPreserveRatio(true);
		_platformIconViewer.setCache(true);
		_platformIconViewer.setLayoutX(_xLoc - Constants.PLATFORM_WIDTH / 2);
		_platformIconViewer.setLayoutY(_yLoc);
		_root.getChildren().add(_platformIconViewer);
	}

	/**
	Accessor method that returns the platform's "core" node, its ImageView.
	*/
	public ImageView getPlatformViewer() {
		return _platformIconViewer;
	}

	/**
	Mutator method that sets the position of the platform to the x and y doubles passed in.
	*/
	public void setPosition(double x, double y) {
		_platformIconViewer.setLayoutX(x);
		_platformIconViewer.setLayoutY(y);
	}

	/**
	Accessor method that returns the platform's current x location
	*/
	public double getX() {
		return _platformIconViewer.getLayoutX();
	}

	/**
	Accessor method that returns the platform's current y location
	*/
	public double getY() {
		return _platformIconViewer.getLayoutY();
	}

	/**
	Accessor method that allows the DoodleGame class to identify the platform type and react accordingly
	*/
	public String getType() {
		return _platformTypeText;
	}

}
