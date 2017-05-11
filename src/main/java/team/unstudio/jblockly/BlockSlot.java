package team.unstudio.jblockly;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Parent;
import javafx.scene.layout.Region;

public class BlockSlot extends Region {

	public static final Bounds INSERT_SLOT_BOUNDS = new BoundingBox(0, Block.INSERT_OFFSET_Y, Block.INSERT_WIDTH, Block.INSERT_HEIGHT);
	public static final Bounds NEXT_SLOT_BOUNDS = new BoundingBox(Block.NEXT_OFFSET_X,0,Block.NEXT_WIDTH,Block.NEXT_HEIGHT);
	public static final double BLOCK_SLOT_MIN_WIDTH=0;
	public static final double BLOCK_SLOT_MIN_HEIGHT=30;
	public static final double BRANCH_MIN_WIDTH = 20;
	
	public enum SlotType {
		NONE, INSERT, BRANCH, NEXT
	}

	private SlotType slotType;
	private Block block;
	private double lineWidth, lineHeight;
	private int firstNode, lastNode;

	public BlockSlot() {
		this(SlotType.NONE);
	}

	public BlockSlot(SlotType slotType) {
		this.slotType = slotType;
	}

	public BlockWorkspace getWorkspace() {
		Parent parent = getParent();

		if (parent instanceof Block)
			return ((Block) parent).getWorkspace();
		else
			return null;
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
		if (getSlotType() == SlotType.NONE)
			return;
		
		if (this.block != null)
			this.block.addToWorkspace();
		if (block != null)
			getChildren().add(block);
		this.block = block;
	}

	public void validateBlock() {
		if (block != null && block.getParent() != this)
			block = null;
	}
	
	public void tryAddBlock(Block block,double x,double y){
		
	}

	@Override
	protected void layoutChildren() {
		if (block == null)
			return;
		layoutInArea(block, 0, 0, prefWidth(-1), prefHeight(-1), 0, null, HPos.CENTER, VPos.CENTER);
	}
	

	@Override
	protected double computePrefWidth(double height) {
		if(block!=null)
			return block.prefWidth(height);
		
		switch (getSlotType()) {
		case INSERT:
			return INSERT_SLOT_BOUNDS.getMaxX()+INSERT_SLOT_BOUNDS.getWidth();
		case BRANCH:
		case NEXT:
			return NEXT_SLOT_BOUNDS.getMaxX()+NEXT_SLOT_BOUNDS.getWidth();
		case NONE:
		default:
			return BLOCK_SLOT_MIN_WIDTH;
		}
	}

	@Override
	protected double computePrefHeight(double width) {
		if(block!=null)
			return block.prefHeight(width);
		
		switch (getSlotType()) {
		case INSERT:
			return INSERT_SLOT_BOUNDS.getMaxY()+INSERT_SLOT_BOUNDS.getHeight();
		case BRANCH:
		case NEXT:
			return NEXT_SLOT_BOUNDS.getMaxY()+NEXT_SLOT_BOUNDS.getHeight();
		case NONE:
		default:
			return BLOCK_SLOT_MIN_HEIGHT;
		}
	}

	double getLineHeight() {
		return lineHeight;
	}

	void setLineHeight(double lineHeight) {
		this.lineHeight = lineHeight;
	}

	double getLineWidth() {
		switch (slotType) {
		case BRANCH:
			return lineWidth<BRANCH_MIN_WIDTH?BRANCH_MIN_WIDTH:lineWidth;
		case INSERT:
			return lineWidth+Block.INSERT_WIDTH;
		default:
			return lineWidth;
		}
	}

	void setLineWidth(double lineWidth) {
		this.lineWidth = lineWidth;
	}

	int getFirstNode() {
		return firstNode;
	}

	void setFirstNode(int firstNode) {
		this.firstNode = firstNode;
	}

	int getLastNode() {
		return lastNode;
	}

	void setLastNode(int lastNode) {
		this.lastNode = lastNode;
	}
}