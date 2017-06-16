package team.unstudio.jblockly.demo;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import team.unstudio.jblockly.Block;
import team.unstudio.jblockly.BlockSlot;
import team.unstudio.jblockly.SlotType;
import team.unstudio.jblockly.BlockWorkspace;
import team.unstudio.jblockly.ConnectionType;
import team.unstudio.jblockly.util.BlockBuilder;

public class Main extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		Block interseting = new BlockBuilder().setConnectionType(ConnectionType.LEFT).addText("LG delaying").addSlot(SlotType.NONE).build();
		Block block2 = new BlockBuilder().setConnectionType(ConnectionType.LEFT).addText("2333").addSlot(SlotType.INSERT, interseting).addText("66666").addSlot(SlotType.INSERT).build();
		/*block2.setConnectionType(ConnectionType.LEFT);
		block2.getChildren().addAll(
				new Label("233333333333333333333333333"),new BlockSlot(SlotType.INSERT),
				new Label("233333333333333333333333333"),new BlockSlot(SlotType.INSERT));*/
		
		Block block3 = new BlockBuilder().setConnectionType(ConnectionType.TOP).addText("2333333333").addSlot(SlotType.INSERT).addText("666").addSlot(SlotType.INSERT).build();
		/*block3.setConnectionType(ConnectionType.TOP);
		block3.getChildren().addAll(
				new Label("233333333333333333333333333"),new BlockSlot(SlotType.INSERT),
				new Label("233333333333333333333333333"),new BlockSlot(SlotType.INSERT));*/
		
		/*BlockSlot slot = new BlockSlot(SlotType.INSERT);
		slot.setBlock(block2);
		
		BlockSlot slot2 = new BlockSlot(SlotType.BRANCH);
		slot2.setBlock(block3);*/
		
		Block block = new BlockBuilder().setFill(Color.GREEN).setConnectionType(ConnectionType.TOPANDBOTTOM).addText("23333333333333333333").addSlot(SlotType.INSERT,block2).addText("").addSlot(SlotType.BRANCH, block3).addText("233333333333333").addSlot(SlotType.INSERT).addText("10086").addSlot(SlotType.NONE).addSlot(SlotType.NEXT).build();
		/*block.setConnectionType(ConnectionType.TOPANDBOTTOM);
		block.getChildren().addAll(
				new Label("233333333333333333333333333"),slot,
				new Label(""),slot2,
				new Label("23333333333333333333"),new BlockSlot(SlotType.INSERT),
				new Label("23333333333333333333333"),new BlockSlot(),
				new BlockSlot(SlotType.NEXT));*/

		BlockWorkspace workspace = new BlockWorkspace();
		workspace.addBlock(block);
		//workspace.addBlock(interseting);

		Scene scene = new Scene(workspace);

		stage.setWidth(900);
		stage.setHeight(600);
		stage.setScene(scene);
		stage.show();
	}
}
