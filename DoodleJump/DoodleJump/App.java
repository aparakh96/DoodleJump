package DoodleJump;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;

/**
The uppermost class in the application, the App class handles the creation of the scene/stage (as is typical) and
instantiates the program's top-level class PaneOrganizer. The only potentially unconventional configuration to note
is the passing of the App and stage instances to the instance of PaneOrganizer - this relationship exists to
enable a full game reset button in PaneOrganizer to call the App class' start(Stage stage) method on action.
 */
public class App extends Application {

	@Override
	public void start(Stage stage) {
		PaneOrganizer organizer = new PaneOrganizer(this, stage);
		Scene scene = new Scene(organizer.getRoot(), Constants.SCENE_WIDTH,
				Constants.SCENE_HEIGHT);
		stage.setScene(scene);
		stage.setTitle("Doodle Jump");
		stage.setResizable(false);
		stage.show();
	}

/**
The main line, this method enables the execution of the application's code.
*/
	public static void main(String[] argv) {
		launch(argv);
	}
}
