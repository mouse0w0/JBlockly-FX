package team.unstudio.jblockly.demo;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import team.unstudio.jblockly.Block;
import team.unstudio.jblockly.BlockSlot;
import team.unstudio.jblockly.BlockSlot.SlotType;
import team.unstudio.jblockly.BlockWorkspace;
import team.unstudio.jblockly.Block.ConnectionType;

public class Main extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		Block block2 = new Block();
		block2.setConnectionType(ConnectionType.LEFT);
		block2.getChildren().addAll(
				new Label("233333333333333333333333333"),new BlockSlot(SlotType.INSERT),
				new Label("233333333333333333333333333"),new BlockSlot(SlotType.INSERT));
		
		Block block3 = new Block();
		block3.setConnectionType(ConnectionType.TOP);
		block3.getChildren().addAll(
				new Label("233333333333333333333333333"),new BlockSlot(SlotType.INSERT),
				new Label("233333333333333333333333333"),new BlockSlot(SlotType.INSERT));
		
		BlockSlot slot = new BlockSlot(SlotType.INSERT);
		slot.setBlock(block2);
		
		BlockSlot slot2 = new BlockSlot(SlotType.BRANCH);
		slot2.setBlock(block3);
		
		Block block = new Block();
		block.setConnectionType(ConnectionType.TOPANDBOTTOM);
		block.getChildren().addAll(
				new Label("233333333333333333333333333"),slot,
				new Label(""),slot2,
				new Label("23333333333333333333"),new BlockSlot(SlotType.INSERT),
				new Label("23333333333333333333333"),new BlockSlot(),
				new BlockSlot(SlotType.NEXT));

		BlockWorkspace workspace = new BlockWorkspace();
		workspace.addBlock(block);

		Scene scene = new Scene(workspace);

		stage.setWidth(900);
		stage.setHeight(600);
		stage.setScene(scene);
		stage.show();
	}
}
