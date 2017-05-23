package team.unstudio.jblockly;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Parent;
import javafx.scene.layout.Region;

public class BlockSlot extends Region implements BlockGlobal{

	private ObjectProperty<SlotType> slotType;
	public final ObjectProperty<SlotType> slotTypeProperty(){
		if(slotType==null){
			slotType = new ObjectPropertyBase<SlotType>() {

				@Override
				public Object getBean() {
					return BlockSlot.this;
				}

				@Override
				public String getName() {
					return "slot";
				}
			};
		}
		return slotType;
	}
	public SlotType getSlotType() {return slotType==null?SlotType.NONE:slotType.get();}
	public void setSlotType(SlotType value) {slotTypeProperty().set(value);}
	
	private ReadOnlyObjectWrapper<Block> block;
	private final ReadOnlyObjectWrapper<Block> blockPropertyImpl(){
		if(block == null){
			block = new ReadOnlyObjectWrapper<Block>(BlockSlot.this,"block"){
				@Override
				protected void invalidated() {
					if(isNotNull().get())
						get().parentProperty().addListener(new ChangeListener<Parent>() {
							@Override
							public void changed(ObservableValue<? extends Parent> observable, Parent oldValue,
									Parent newValue) {
								observable.removeListener(this);
								set(null);
							}
						});
				}
			};
		}
		return block;
	}
	public final ReadOnlyObjectProperty<Block> blockProperty(){return blockPropertyImpl().getReadOnlyProperty();}
	public final Block getBlock() {return block==null?null:block.get();}
	public final boolean hasBlock(){return blockPropertyImpl().isNotNull().get();}
	
	private ObjectProperty<Block> defaultBlock;
	private final ObjectProperty<Block> defaultBlockProperty(){
		if(defaultBlock == null){
			defaultBlock = new ObjectPropertyBase<Block>() {

				@Override
				public Object getBean() {
					return BlockSlot.this;
				}

				@Override
				public String getName() {
					return "defaultBlock";
				}
			};
		}
		return defaultBlock;
	}
	public final Block getDefaultBlock() {return defaultBlock==null?null:defaultBlock.get();}
	public final void setDefaultBlock(Block block) {defaultBlockProperty().set(block);}
	public final boolean hasDefaultBlock(){return defaultBlockProperty().isNotNull().get();}

	public BlockSlot() {
		this(SlotType.NONE);
	}

	public BlockSlot(SlotType slotType) {
		setSlotType(slotType);
	}
	
	public BlockSlot(SlotType slotType,Block block) {
		this(slotType);
		setBlock(block);
	}

	public BlockWorkspace getWorkspace() {
		Parent parent = getParent();

		if (parent instanceof Block)
			return ((Block) parent).getWorkspace();
		else
			return null;
	}

	public boolean setBlock(Block block) {
		if(!isCanLinkBlock(block))
			return false;
		
		ReadOnlyObjectWrapper<Block> blockWrapper = blockPropertyImpl();
		if (hasBlock())
			blockWrapper.get().addToWorkspace();
		if (block != null)
			getChildren().add(block);
		
		blockWrapper.set(block);
		return true;
	}
	
	public boolean tryLinkBlock(Block block,double x,double y){
		switch (getSlotType()) {
		case INSERT:
			if(INSERT_SLOT_BOUNDS.contains(x, y))
				return setBlock(block);
			break;
		case NEXT:
		case BRANCH:
			if(NEXT_SLOT_BOUNDS.contains(x, y))
				return setBlock(block);
			break;
		default:
			return false;
		}
		
		if(hasBlock())
			return getBlock().tryLinkBlock(block, x, y);
		
		return false;
	}
	
	public boolean isCanLinkBlock(Block block){
		return getSlotType().isCanBeConnection(block.getConnectionType());
	}

	@Override
	protected void layoutChildren() {
		if (hasBlock())
			layoutInArea(getChildren().get(0), 0, 0, prefWidth(-1), prefHeight(-1), 0, null, HPos.CENTER, VPos.CENTER);
	}

	@Override
	protected double computePrefWidth(double height) {
		if(hasBlock())
			return getBlock().prefWidth(height);
		
		switch (getSlotType()) {
		case INSERT:
			return INSERT_SLOT_BOUNDS.getMaxX()+INSERT_SLOT_BOUNDS.getWidth();
		case BRANCH:
		case NEXT:
			return NEXT_SLOT_BOUNDS.getMaxX()+NEXT_SLOT_BOUNDS.getWidth();
		default:
			return BLOCK_SLOT_MIN_WIDTH;
		}
	}

	@Override
	protected double computePrefHeight(double width) {
		if(hasBlock())
			return getBlock().prefHeight(width);
		
		switch (getSlotType()) {
		case INSERT:
			return INSERT_SLOT_BOUNDS.getMaxY()+INSERT_SLOT_BOUNDS.getHeight();
		case BRANCH:
		case NEXT:
			return NEXT_SLOT_BOUNDS.getMaxY()+NEXT_SLOT_BOUNDS.getHeight();
		default:
			return BLOCK_SLOT_MIN_HEIGHT;
		}
	}
	
	private double lineWidth = 0, lineHeight = 0;
	private int firstNode = 0, lastNode = 0;

	double getLineHeight() {
		return lineHeight;
	}

	void setLineHeight(double lineHeight) {
		this.lineHeight = lineHeight;
	}
	
	double getOriginalLineWidth(){
		return lineWidth;
	}
	
	double getLayoutLineWidth(){
		switch (getSlotType()) {
		case BRANCH:
			return lineWidth<BRANCH_MIN_WIDTH?BRANCH_MIN_WIDTH:lineWidth;
		default:
			return lineWidth;
		}
	}

	double getLineWidth() {
		switch (getSlotType()) {
		case BRANCH:
			return lineWidth<BRANCH_MIN_WIDTH?BRANCH_MIN_WIDTH:lineWidth;
		case INSERT:
			return lineWidth+INSERT_WIDTH;
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