package team.unstudio.jblockly.demo;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import team.unstudio.jblockly.SlotType;
import team.unstudio.jblockly.util.SimpleBlockBuilder;
import team.unstudio.jblockly.BlockList;
import team.unstudio.jblockly.BlockWorkspace;
import team.unstudio.jblockly.ConnectionType;

public class Main extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		SimpleBlockBuilder main = new SimpleBlockBuilder().setConnectionType(ConnectionType.NONE).setRegistyName("main")
				.addLabel("main").addBlockSlot().addBlockSlot("branch",SlotType.BRANCH).addLabel("").addBlockSlot();
		
		SimpleBlockBuilder ifBlock = new SimpleBlockBuilder().setConnectionType(ConnectionType.TOPANDBOTTOM).setRegistyName("if")
				.addLabel("如果").addBlockSlot("if", SlotType.INSERT).addBlockSlot("branch", SlotType.BRANCH)
				.addBlockSlot().addBlockSlot("next",SlotType.NEXT);
		
		SimpleBlockBuilder end = new SimpleBlockBuilder().setConnectionType(ConnectionType.TOP).setRegistyName("end").addLabel("返回").addBlockSlot();

		SimpleBlockBuilder hookan = new SimpleBlockBuilder().setConnectionType(ConnectionType.LEFT).setRegistyName("hookan")
				.addLabel("皇天").addBlockSlot();
		
		SimpleBlockBuilder getDalao = new SimpleBlockBuilder().setConnectionType(ConnectionType.LEFT).setRegistyName("getDalao")
				.addLabel("获取大佬").addBlockSlot(null, SlotType.INSERT);
		
//		SimpleBlockBuilder nvZhuang = new SimpleBlockBuilder().setConnectionType(ConnectionType.LEFT).setRegistyName("nvZhuang")
//				.addLabel("女装").addBlockSlot();
		
		SimpleBlockBuilder dalao = new SimpleBlockBuilder().setConnectionType(ConnectionType.TOPANDBOTTOM).setRegistyName("dalao")
				.addLabel("变量 大佬").addBlockSlot(null, SlotType.INSERT).addNextSlot();
		
		SimpleBlockBuilder set = new SimpleBlockBuilder().setConnectionType(ConnectionType.LEFT).setRegistyName("set")
				.addLabel("=").addBlockSlot(null, SlotType.INSERT);
		
		BlockList blockList = new BlockList();
		blockList.buildersProperty().addAll(main,ifBlock,end,hookan,getDalao,dalao,set);
		
		BlockWorkspace workspace = new BlockWorkspace();
		workspace.getChildren().add(blockList);
		
		workspace.addBlock(main.build());
		workspace.addBlock(ifBlock.build());
		workspace.addBlock(hookan.build());
		workspace.addBlock(getDalao.build());
//		workspace.addBlock(nvZhuang.build());
		workspace.addBlock(dalao.build());
		workspace.addBlock(dalao.build());
		workspace.addBlock(set.build());
		workspace.addBlock(end.build());

		Scene scene = new Scene(workspace);

		stage.setTitle("JBlockly");
		stage.setWidth(900);
		stage.setHeight(600);
		stage.setScene(scene);
		stage.show();
	}
}
