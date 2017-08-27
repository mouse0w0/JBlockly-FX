package team.unstudio.jblockly;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;

//TODO: registy blocks
public class BlockWorkspace extends Pane implements IBlockly{
	
	private ReadOnlyObjectWrapper<Block> movingBlock;
	final ReadOnlyObjectWrapper<Block> movingBlockPropertyImpl(){
		if(movingBlock==null)
			movingBlock = new ReadOnlyObjectWrapper<>(this, "movingBlock");
		return movingBlock;
	}
	final void setMovingBlock(Block block){movingBlockPropertyImpl().set(block);}
	public final ReadOnlyObjectProperty<Block> movingBlockProperty(){return movingBlockPropertyImpl().getReadOnlyProperty();}
	public final Block getMovingBlock(){return movingBlock==null?null:movingBlock.get();}
	
	private final ReadOnlyObjectWrapper<BlockWorkspace> workspace = new ReadOnlyObjectWrapper<BlockWorkspace>(this, "workspace", this);
	public BlockWorkspace getWorkspace() {return this;}
	public ReadOnlyObjectProperty<BlockWorkspace> workspaceProperty() {return workspace.getReadOnlyProperty();}
	
	private static final String DEFAULT_STYLE_CLASS = "block-workspace";
	public BlockWorkspace() {
		getStyleClass().addAll(DEFAULT_STYLE_CLASS);
	}

	public void addBlock(Block block) {
		if (block.getWorkspace().equals(this)) {
			block.addToWorkspace();
		} else {
			getChildren().add(block);
			block.setLayoutX(0);
			block.setLayoutY(0);
		}
	}
	
	public List<Block> getBlocks() {
		List<Block> blocks = new ArrayList<>();
		getManagedChildren().stream().filter(node->node instanceof Block).forEach(node->blocks.add((Block) node));
		return blocks;
	}
	
	public void tryConnectBlock(Block block,double x,double y){
		for(Block b:getBlocks())
			if(b.tryConnectBlock(block, x-b.getLayoutX(), y-b.getLayoutY()))
				return;
	}
	
	public void tryConnectBlock(Block block,Point2D point){
		tryConnectBlock(block, point.getX(), point.getY());
	}
}
