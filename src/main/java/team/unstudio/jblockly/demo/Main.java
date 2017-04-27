package team.unstudio.jblockly.demo;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import team.unstudio.jblockly.Block;
import team.unstudio.jblockly.BlockSlot;
import team.unstudio.jblockly.BlockSlot.SlotType;
import team.unstudio.jblockly.BlockWorkspace;

public class Main extends Application{
	
	public static void main(String[] args){
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		Block block = new Block();
		Label label = new Label("23333");
		TextField field = new TextField();
		BlockSlot slot = new BlockSlot();
		slot.setSlotType(SlotType.INSERT);
		slot.setBlock(new Block());
		block.addNode("label1", new Label("23333"));
		block.addNode("field", field);
		block.addNode("insert", slot);
		
		BlockWorkspace workspace = new BlockWorkspace();
		workspace.getChildren().add(block);
		
		Scene scene = new Scene(workspace);
		
		stage.setWidth(900);
		stage.setHeight(600);
		stage.setScene(scene);
		stage.show();
	}
}
