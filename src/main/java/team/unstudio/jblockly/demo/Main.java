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
		block2.addNode(new Label("233333333333333333333333333"));
		block2.addNode(new BlockSlot());
		
		Block block3 = new Block();
		block3.setConnectionType(ConnectionType.NONE);
		block3.addNode(new Label("233333333333333333333333333"));
		block3.addNode(new BlockSlot());
		
		BlockSlot slot = new BlockSlot();
		slot.setSlotType(SlotType.INSERT);
		slot.setBlock(block2);
		
		Block block = new Block();
		block.setConnectionType(ConnectionType.TOPANDBUTTOM);
		block.addNode(new Label("233333333333333333333333333"));
		block.addNode("insert", slot);
		block.addNode(new Label(""));
		block.addNode(new BlockSlot(SlotType.BRANCH));
		block.addNode(new Label("23333333333333333333"));
		block.addNode(new BlockSlot());
		block.addNode(new Label("23333333333333333333333"));
		block.addNode(new BlockSlot());

		BlockWorkspace workspace = new BlockWorkspace();
		workspace.addBlock(block);
		workspace.addBlock(block3);

		Scene scene = new Scene(workspace);

		stage.setWidth(900);
		stage.setHeight(600);
		stage.setScene(scene);
		stage.show();
	}
}
