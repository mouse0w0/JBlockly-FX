package team.unstudio.jblockly;

import javafx.scene.Parent;
import javafx.scene.layout.Pane;

public class BlockWorkspace extends Pane {

	public void addBlock(Block block) {
		Parent parent = block.getParent();
		if(parent==null) {
			getChildren().add(block);
			block.setLayoutX(0);
			block.setLayoutY(0);
		}else if (parent instanceof BlockWorkspace) {
			if (!parent.equals(this)) {
				getChildren().add(block);
				block.setLayoutX(0);
				block.setLayoutY(0);
			}
		} else {
			if (block.getWorkspace().equals(this)) {
				double x = block.getLayoutX(), y = block.getLayoutY();
				while (!(parent instanceof BlockWorkspace)) {
					x += parent.getLayoutX();
					y += parent.getLayoutY();
					parent = parent.getParent();
				}

				((BlockWorkspace) parent).getChildren().add(block);
				block.setLayoutX(x);
				block.setLayoutY(y);
			} else {
				getChildren().add(block);
				block.setLayoutX(0);
				block.setLayoutY(0);
			}
		}
	}

	public static void addBlockToWorkspace(Block block) {
		Parent parent = block.getParent();
		if (parent == null)
			return;
		if (parent instanceof BlockWorkspace)
			return;

		double x = block.getLayoutX(), y = block.getLayoutY();
		while (!(parent instanceof BlockWorkspace)) {
			x += parent.getLayoutX();
			y += parent.getLayoutY();
			parent = parent.getParent();
		}

		((BlockWorkspace) parent).getChildren().add(block);
		block.setLayoutX(x);
		block.setLayoutY(y);
	}
	
}
