package team.unstudio.jblockly.demo;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import team.unstudio.jblockly.SlotType;
import team.unstudio.jblockly.util.BlockBuilder;
import team.unstudio.jblockly.BlockWorkspace;
import team.unstudio.jblockly.ConnectionType;

public class Main extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		BlockBuilder link = new BlockBuilder().setConnectionType(ConnectionType.TOPANDBOTTOM).setRegistyName("link")
				.addLabel("皇天女装 ").addBlockSlot().addBlockSlot("next", SlotType.NEXT);
				
		BlockBuilder branch = new BlockBuilder().setConnectionType(ConnectionType.NONE).setRegistyName("branch")
				.addLabel("main         ").addBlockSlot().addBlockSlot("branch",SlotType.BRANCH).addLabel("                   ").addBlockSlot();
		
		BlockBuilder insert = new BlockBuilder().setConnectionType(ConnectionType.LEFT).setRegistyName("insert")
				.addLabel("皇天是dalao").addBlockSlot();
		
		BlockBuilder ifBlock = new BlockBuilder().setConnectionType(ConnectionType.TOPANDBOTTOM).setRegistyName("if")
				.addLabel("如果            ").addBlockSlot("if", SlotType.INSERT).addBlockSlot("branch", SlotType.BRANCH)
				.addLabel("              ").addBlockSlot().addBlockSlot("next",SlotType.NEXT);
		
		BlockBuilder end = new BlockBuilder().setConnectionType(ConnectionType.TOP).setRegistyName("end").addLabel("结束方法").addBlockSlot();

		BlockWorkspace workspace = new BlockWorkspace();
		workspace.addBlock(branch.build());
		workspace.addBlock(ifBlock.build());
		workspace.addBlock(insert.build());
		workspace.addBlock(link.build());
		workspace.addBlock(end.build());
		workspace.addBlock(end.build());

		Scene scene = new Scene(workspace);

		stage.setTitle("JBlockly");
		stage.setWidth(900);
		stage.setHeight(600);
		stage.setScene(scene);
		stage.show();
	}
}
