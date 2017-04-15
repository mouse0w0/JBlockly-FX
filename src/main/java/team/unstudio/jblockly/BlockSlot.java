package team.unstudio.jblockly;

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
	protected double computeMinWidth(double height) {
		return block.minWidth(height);
	}
	
	@Override
	protected double computeMinHeight(double width) {
		return block.minHeight(width);
	}
	
	@Override
	protected double computePrefWidth(double height) {
		return block.prefWidth(height);
	}

	@Override
	protected double computePrefHeight(double width) {
		return block.prefHeight(width);
	}
}
