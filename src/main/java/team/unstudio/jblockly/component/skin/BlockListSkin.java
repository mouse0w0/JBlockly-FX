package team.unstudio.jblockly.component.skin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.beans.InvalidationListener;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.SkinBase;
import team.unstudio.jblockly.Block;
import team.unstudio.jblockly.component.BlockList;
import team.unstudio.jblockly.util.provider.IBlockProvider;

public class BlockListSkin extends SkinBase<BlockList>{
	
	private Map<IBlockProvider, Block> providerToBlock = new HashMap<>();

	public BlockListSkin(BlockList control) {
		super(control);
		control.providersProperty().addListener(providersChangeListener);
		updateBlock();
	}
	
    private InvalidationListener providersChangeListener = observable -> updateBlock();
	
	private void updateBlock(){
		getChildren().clear();
		
		List<IBlockProvider> providers = getSkinnable().providersProperty();
		for(IBlockProvider provider:providers){
			if(providerToBlock.containsKey(provider)){
				Block block = providerToBlock.get(provider);
				if(!getSkinnable().equals(block.getParent())){
					block = provider.build();
					providerToBlock.put(provider, block);
				}
				getChildren().add(block);
			}else{
				Block block = provider.build();
				providerToBlock.put(provider, block);
				getChildren().add(block);
			}
		}
		
		providerToBlock.keySet().stream().filter(provider->!providers.contains(provider)).forEach(provider->providerToBlock.remove(provider));
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
		
		for(Node node:providerToBlock.values()){
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
		
		for(Node node:providerToBlock.values()){
			double theight = node.prefHeight(-1);
			height+=theight+spacing;
		}
		
		return topInset + height + bottomInset;
	}
	
	@Override
	public void dispose() {
		providerToBlock = null;
	}
}
