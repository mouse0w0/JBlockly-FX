package team.unstudio.jblockly;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;

public class BlockWorkspace extends Pane implements IBlockly{

	public void addBlock(Block block) {
		if (block.getParent()!=null&&block.getWorkspace().equals(this)) {
			block.addToWorkspace();
		} else {
			getChildren().add(block);
			block.setLayoutX(0);
			block.setLayoutY(0);
		}
	}
	
	public void removeBlock(Block block){
		Parent parent = block.getParent();
		if(parent instanceof BlockSlot)
			((BlockSlot) parent).removeBlock();
		else if(parent instanceof Pane)
			((Pane) parent).getChildren().remove(block);
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
	
	private final ReadOnlyObjectWrapper<BlockWorkspace> workspace = new ReadOnlyObjectWrapper<BlockWorkspace>(this, "workspace", this);
	public BlockWorkspace getWorkspace() {return this;}
	public ReadOnlyObjectProperty<BlockWorkspace> workspaceProperty() {return workspace.getReadOnlyProperty();}
}
