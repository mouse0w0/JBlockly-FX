package team.unstudio.jblockly.demo;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import team.unstudio.jblockly.component.BlockList;
import team.unstudio.jblockly.component.BlockSearcher;
import team.unstudio.jblockly.input.SlotType;
import team.unstudio.jblockly.provider.SimpleBlockProvider;
import team.unstudio.jblockly.Block;
import team.unstudio.jblockly.BlockWorkspace;
import team.unstudio.jblockly.ConnectionType;

public class Demo extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		SimpleBlockProvider main = new SimpleBlockProvider().setConnectionType(ConnectionType.NONE).setRegistyName("main")
				.addLabel("main").addBlockSlot().addBlockSlot("branch",SlotType.BRANCH).register();
		
		SimpleBlockProvider ifBlock = new SimpleBlockProvider().setConnectionType(ConnectionType.TOPANDBOTTOM).setRegistyName("if")
				.addLabel("如果").addBlockSlot("if", SlotType.INSERT).addBlockSlot("branch", SlotType.BRANCH)
				.addBlockSlot().addBlockSlot("next",SlotType.NEXT).setFill(Color.WHITE).setStroke(Color.RED).register();
		
		SimpleBlockProvider end = new SimpleBlockProvider().setConnectionType(ConnectionType.TOP).setRegistyName("end")
				.addLabel("返回").addBlockSlot().register();

		SimpleBlockProvider string = new SimpleBlockProvider().setConnectionType(ConnectionType.LEFT).setRegistyName("string")
				.addLabel("\"").addTextField("text").addLabel("\"").addBlockSlot().register();
		
		SimpleBlockProvider getDalao = new SimpleBlockProvider().setConnectionType(ConnectionType.LEFT).setRegistyName("getDalao")
				.addLabel("获取大佬").addBlockSlot(null, SlotType.INSERT).register();
		
		SimpleBlockProvider variable = new SimpleBlockProvider().setConnectionType(ConnectionType.TOPANDBOTTOM).setRegistyName("variable")
				.addLabel("变量").addTextField("name").addBlockSlot(null, SlotType.INSERT).addNextSlot().register();
		
		SimpleBlockProvider set = new SimpleBlockProvider().setConnectionType(ConnectionType.LEFT).setRegistyName("set")
				.addLabel("=").addBlockSlot(null, SlotType.INSERT).register();
		
		BlockList blockList = new BlockList();
		blockList.providersProperty().addAll(main,ifBlock,end,string,getDalao,variable,set);
		
		ScrollPane scrollPane2 = new ScrollPane(blockList);
		scrollPane2.setPrefWidth(200);
		
		Block block = ifBlock.build();
		
		Rectangle rectangle = new Rectangle(block.prefWidth(-1),block.prefHeight(-1));
		rectangle.setFill(Color.BLACK);
		rectangle.setLayoutX(0);
		rectangle.setLayoutY(0);
		
		BlockSearcher searcher = new BlockSearcher();
		searcher.setVisible(false);
		
		BlockWorkspace workspace = new BlockWorkspace();
		
		workspace.getChildren().add(scrollPane2);
		blockList.setWorkspace(workspace);
		
		workspace.getChildren().add(rectangle);
		workspace.addBlock(block);
		
		workspace.getChildren().add(searcher);
		searcher.setWorkspace(workspace);
		
		rectangle.widthProperty().bind(block.widthProperty());
		rectangle.heightProperty().bind(block.heightProperty());
		
		ScrollPane scrollPane = new ScrollPane(workspace);
		scrollPane.viewportBoundsProperty().addListener((observable, oldValue, newValue)->{
			System.out.println(newValue);
			scrollPane2.setLayoutY(Math.abs(newValue.getMinY()));
		});

		Scene scene = new Scene(scrollPane);

		stage.setTitle("JBlockly Demo");
		stage.setWidth(900);
		stage.setHeight(600);
		stage.setScene(scene);
		stage.show();
	}
}
