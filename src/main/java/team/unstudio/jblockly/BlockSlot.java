package team.unstudio.jblockly;

import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.layout.Region;

public class BlockSlot extends Region{
	
	public enum SlotType{
		NONE,INSERT,BRANCH,NEXT
	}
	
	private SlotType slotType = SlotType.NONE;
	private Block block;
	
	public BlockSlot() {
		
	}

	public SlotType getSlotType() {
		return slotType;
	}

	public void setSlotType(SlotType slotType) {
		this.slotType = slotType;
	}

	public Block getBlock() {
		return block;
	}

	public void setBlock(Block block) {
		if(this.block!=null) getChildren().remove(this.block);
		if(block!=null) getChildren().add(block);
		this.block = block;
	}
	
	@Override
	protected void layoutChildren() {
		if(block==null) return;
		layoutInArea(block, 0, 0, block.getLayoutBounds().getWidth(), block.getLayoutBounds().getHeight(), 0, HPos.CENTER, VPos.CENTER);
	}
	
	@Override
	protected double computeMinWidth(double height) {
		return block==null?0:block.minWidth(height);
	}
	
	@Override
	protected double computeMinHeight(double width) {
		return block==null?0:block.minHeight(width);
	}
	
	@Override
	protected double computePrefWidth(double height) {
		return block==null?0:block.prefWidth(height);
	}

	@Override
	protected double computePrefHeight(double width) {
		return block==null?0:block.prefHeight(width);
	}
	
	@Override
	protected double computeMaxWidth(double height) {
		return block==null?0:block.maxWidth(height);
	}
	
	@Override
	protected double computeMaxHeight(double width) {
		return block==null?0:block.maxHeight(width);
	}
}
