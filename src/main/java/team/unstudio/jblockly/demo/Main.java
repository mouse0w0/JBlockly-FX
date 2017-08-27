package team.unstudio.jblockly.demo;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import team.unstudio.jblockly.component.BlockList;
import team.unstudio.jblockly.input.SlotType;
import team.unstudio.jblockly.util.SimpleBlockProvider;
import team.unstudio.jblockly.BlockWorkspace;
import team.unstudio.jblockly.ConnectionType;

public class Main extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		SimpleBlockProvider main = new SimpleBlockProvider().setConnectionType(ConnectionType.NONE).setRegistyName("main")
				.addLabel("main").addBlockSlot().addBlockSlot("branch",SlotType.BRANCH).addLabel("").addBlockSlot();
		
		SimpleBlockProvider ifBlock = new SimpleBlockProvider().setConnectionType(ConnectionType.TOPANDBOTTOM).setRegistyName("if")
				.addLabel("如果").addBlockSlot("if", SlotType.INSERT).addBlockSlot("branch", SlotType.BRANCH)
				.addBlockSlot().addBlockSlot("next",SlotType.NEXT);
		
		SimpleBlockProvider end = new SimpleBlockProvider().setConnectionType(ConnectionType.TOP).setRegistyName("end").addLabel("返回").addBlockSlot();

		SimpleBlockProvider string = new SimpleBlockProvider().setConnectionType(ConnectionType.LEFT).setRegistyName("string")
				.addLabel("\"").addTextField("text").addLabel("\"").addBlockSlot();
		
		SimpleBlockProvider getDalao = new SimpleBlockProvider().setConnectionType(ConnectionType.LEFT).setRegistyName("getDalao")
				.addLabel("获取大佬").addBlockSlot(null, SlotType.INSERT);
		
		SimpleBlockProvider nvZhuang = new SimpleBlockProvider().setConnectionType(ConnectionType.LEFT).setRegistyName("nvZhuang")
				.addLabel("女装").addBlockSlot();
		
		SimpleBlockProvider dalao = new SimpleBlockProvider().setConnectionType(ConnectionType.TOPANDBOTTOM).setRegistyName("dalao")
				.addLabel("变量").addTextField("variaty").addBlockSlot(null, SlotType.INSERT).addNextSlot();
		
		SimpleBlockProvider set = new SimpleBlockProvider().setConnectionType(ConnectionType.LEFT).setRegistyName("set")
				.addLabel("=").addBlockSlot(null, SlotType.INSERT);
		
		BlockList blockList = new BlockList();
		blockList.buildersProperty().addAll(main,ifBlock,end,string,getDalao,dalao,set);
		
		BlockWorkspace workspace = new BlockWorkspace();
		workspace.getChildren().add(blockList);
		
//		Block bmain = main.build();
//		workspace.addBlock(main.build());
		
//		workspace.addBlock(ifBlock.build());
//		workspace.addBlock(hookan.build());
//		workspace.addBlock(getDalao.build());
////		workspace.addBlock(nvZhuang.build());
//		workspace.addBlock(dalao.build());
//		workspace.addBlock(dalao.build());
//		workspace.addBlock(set.build());
//		workspace.addBlock(end.build());

		Scene scene = new Scene(workspace);

		stage.setTitle("JBlockly");
		stage.setWidth(900);
		stage.setHeight(600);
		stage.setScene(scene);
		stage.show();
	}
}
