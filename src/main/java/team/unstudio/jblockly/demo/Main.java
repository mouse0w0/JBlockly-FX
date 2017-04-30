package team.unstudio.jblockly.demo;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import team.unstudio.jblockly.Block;
import team.unstudio.jblockly.BlockSlot;
import team.unstudio.jblockly.BlockSlot.SlotType;
import team.unstudio.jblockly.BlockWorkspace;

public class Main extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		Block block = new Block();
		// TextField field = new TextField();
		Label label = new Label("233333333333333333333333333");
		label.setFont(new Font(18));
		BlockSlot slot = new BlockSlot();
		slot.setSlotType(SlotType.INSERT);
		block.addNode(label);
		// block.addNode("field", field);
		block.addNode("insert", slot);
		block.addNode(new Label(""));
		block.addNode(new BlockSlot(SlotType.BRANCH));
		block.addNode(new Label("23333333333333333333"));
		block.addNode(new BlockSlot());
		block.addNode(new Label("23333333333333333333333"));
		block.addNode(new BlockSlot());

		BlockWorkspace workspace = new BlockWorkspace();
		workspace.getChildren().add(block);

		Scene scene = new Scene(workspace);

		stage.setWidth(900);
		stage.setHeight(600);
		stage.setScene(scene);
		stage.show();
	}
}
