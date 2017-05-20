package team.unstudio.jblockly;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;

public class BlockWorkspace extends Pane {

	public void addBlock(Block block) {
		if (block.getParent()!=null&&block.getWorkspace().equals(this)) {
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
	
	public void tryLinkBlock(Block block,double sceneX,double sceneY){
		double x = sceneX-getLayoutX(),y = sceneY-getLayoutY();
		Parent parent = getParent();
		while(parent!=null){
			x-=parent.getLayoutX();
			y-=parent.getLayoutY();
			parent = parent.getParent();
		}
		
		for(Block b:getBlocks())
			if(b.tryLinkBlock(block, x-b.getLayoutX(), y-b.getLayoutY()))
				return;
	}
}
