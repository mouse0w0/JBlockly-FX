package team.unstudio.jblockly.component.skin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.javafx.scene.control.skin.BehaviorSkinBase;

import javafx.beans.InvalidationListener;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import team.unstudio.jblockly.Block;
import team.unstudio.jblockly.component.BlockList;
import team.unstudio.jblockly.component.behavior.BlockListBehavior;
import team.unstudio.jblockly.util.IBlockBuilder;

public class BlockListSkin extends BehaviorSkinBase<BlockList,BlockListBehavior>{
	
	private Map<IBlockBuilder, Block> builderToBlock = new HashMap<>();

	public BlockListSkin(BlockList control) {
		super(control, new BlockListBehavior(control));
		control.buildersProperty().addListener(buildersChangeListener);
		updateBlock();
	}
	
    private InvalidationListener buildersChangeListener = observable -> updateBlock();
	
	private void updateBlock(){
		getChildren().clear();
		
		List<IBlockBuilder> builders = getSkinnable().buildersProperty();
		for(IBlockBuilder builder:builders){
			if(builderToBlock.containsKey(builder)){
				Block block = builderToBlock.get(builder);
				if(block.getParent()==null||!block.getParent().equals(getSkinnable())){
					block = builder.build();
					builderToBlock.put(builder, block);
				}
				getChildren().add(block);
			}else{
				Block block = builder.build();
				builderToBlock.put(builder, block);
				getChildren().add(block);
			}
		}
		
		builderToBlock.keySet().stream().filter(builder->!builders.contains(builder)).forEach(builder->builderToBlock.remove(builder));
	}
	
	private boolean layouting;
	@Override
	protected void layoutChildren(double contentX, double contentY, double contentWidth, double contentHeight) {
		if(layouting)
			return;
		layouting = true;
		
		updateBlock();
		
		double spacing = getSkinnable().getSpacing();
		double x = contentX,y = contentY;
		
		for(Node node:getChildren()){
			double width = node.prefWidth(-1),height = node.prefHeight(-1);
			layoutInArea(node, x, y, width, height, 0, HPos.LEFT, VPos.TOP);
			y += height + spacing;
		}
		
		layouting = false;
	}
	
	@Override
	protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset,
			double leftInset) {
		double width = 0;
		
		for(Node node:getChildren()){
			double twidth = node.prefWidth(-1);
			if(width<twidth)
				width=twidth;
		}
		
		return leftInset + width + rightInset;
	}
	
	@Override
	protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset,
			double leftInset) {
		double spacing = getSkinnable().getSpacing();
		double height = 0;
		
		for(Node node:getChildren()){
			double theight = node.prefHeight(-1);
			height+=theight+spacing;
		}
		
		return topInset + height - spacing + bottomInset;
	}
}
