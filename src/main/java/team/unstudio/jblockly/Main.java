package team.unstudio.jblockly;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application{
	
	public static void main(String[] args){
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		BlockWorkspace workspace = new BlockWorkspace();
		
		workspace.getChildren().add(new Block());
		
		Scene scene = new Scene(workspace);
		
		stage.setWidth(900);
		stage.setHeight(600);
		stage.setScene(scene);
		stage.show();
	}
}
