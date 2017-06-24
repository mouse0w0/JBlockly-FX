package team.unstudio.jblockly;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
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
	
	private BooleanProperty insertable;
	public final BooleanProperty insertableProperty(){
		if(insertable==null){
			insertable = new BooleanPropertyBase(true) {
				
				@Override
				public String getName() {
					return "insertable";
				}
				
				@Override
				public Object getBean() {
					return BlockSlot.this;
				}
			};
		}
		return insertable;
	}
	public boolean isInsertable(){return insertable==null?true:insertable.get();}
	public void setInsertable(boolean value){insertableProperty().set(value);}
	
	
	private ReadOnlyObjectWrapper<Block> block;
	private final ReadOnlyObjectWrapper<Block> blockPropertyImpl(){
		if(block == null){
			block = new ReadOnlyObjectWrapper<Block>(BlockSlot.this,"block");
		}
		return block;
	}
	public final ReadOnlyObjectProperty<Block> blockProperty(){return blockPropertyImpl().getReadOnlyProperty();}
	public final Block getBlock() {return block==null?null:block.get();}
	public final boolean hasBlock(){return getBlock()!=null;}
	public final boolean setBlock(Block block) {
		if(!isCanInsertBlock(block))
			return false;
		
		Block oldBlock = getBlock();
		Block defaultBlock = getDefaultBlock();
		if(oldBlock!=null&&defaultBlock!=oldBlock)
			oldBlock.addToWorkspace();
		
		if(block!=null){
			getChildren().add(block);
			block.parentProperty().addListener(new ChangeListener<Parent>() {
				@Override
				public void changed(ObservableValue<? extends Parent> observable, Parent oldValue,
						Parent newValue) {
					observable.removeListener(this);
					blockPropertyImpl().set(null);
				}
			});
		}
		
		blockPropertyImpl().set(block);
		return true;
	}
	public final void removeBlock(){
		if(!hasBlock())
			return;
		
		getChildren().remove(getBlock());
	}
	
	private ObjectProperty<Block> defaultBlock;
	private final ObjectProperty<Block> defaultBlockProperty(){
		if(defaultBlock == null){
			defaultBlock = new ObjectPropertyBase<Block>() {
				@Override
				public void set(Block newValue) {
					if(isNotNull().get()){
						Block oldValue = get();
						if(getChildren().contains(oldValue))
							getChildren().remove(oldValue);
					}
					
					if(newValue!=null){
						getChildren().add(newValue);
						newValue.setMovable(false);
						newValue.setVisible(!hasBlock());
						newValue.parentProperty().addListener(new ChangeListener<Parent>() {
							@Override
							public void changed(ObservableValue<? extends Parent> observable, Parent oldValue,
									Parent newValue) {
								observable.removeListener(this);
								set(null);
							}
						});
					}
					
					super.set(newValue);
				}

				@Override
				public Object getBean() {
					return BlockSlot.this;
				}

				@Override
				public String getName() {
					return "defaultBlock";
				}
				
			};
			
			blockProperty().addListener((observable,oldValue,newValue)->{
				Block block = getDefaultBlock();
				if(block!=null&&newValue!=block){
					if(newValue==null){
						blockPropertyImpl().set(block);
						block.setVisible(true);
					}else{
						block.setVisible(false);
					}
				}
			});
		}
		return defaultBlock;
	}
	public final Block getDefaultBlock() {return defaultBlock==null?null:defaultBlock.get();}
	public final boolean setDefaultBlock(Block block) {
		if(!isCanInsertBlock(block))
			return false;
		defaultBlockProperty().set(block);
		return true;
	}
	public final boolean hasDefaultBlock(){return getDefaultBlock()!=null;}

	public BlockSlot() {
		this(SlotType.NONE);
	}

	public BlockSlot(SlotType slotType) {
		setSlotType(slotType);
		setPickOnBounds(false);
	}
	
	public BlockSlot(SlotType slotType,Block defaultBlock) {
		this(slotType);
		setDefaultBlock(defaultBlock);
	}

	public final BlockWorkspace getWorkspace() {
		Parent parent = getParent();

		if (parent instanceof Block)
			return ((Block) parent).getWorkspace();
		else
			return null;
	}
	
	public boolean tryConnectBlock(Block block,double x,double y){
		switch (getSlotType()) {
		case INSERT:
			if(INSERT_SLOT_LINK_BOUNDS.contains(x, y))
				return setBlock(block);
			break;
		case NEXT:
		case BRANCH:
			if(NEXT_SLOT_LINK_BOUNDS.contains(x, y))
				return setBlock(block);
			break;
		default:
			return false;
		}
		
		if(hasBlock())
			return getBlock().tryConnectBlock(block, x, y);
		
		return false;
	}
	
	public boolean isCanInsertBlock(Block block){
		return block==null?true:getSlotType().isCanInsert(block.getConnectionType())&&isInsertable()&&block.isConnectable(this);
	}

	@Override
	protected void layoutChildren() {
		if (hasBlock())
			layoutInArea(getBlock(), 0, 0, prefWidth(-1), prefHeight(-1), 0, null, HPos.CENTER, VPos.CENTER);
	}

	@Override
	protected double computePrefWidth(double height) {
		if(hasBlock())
			return getBlock().prefWidth(-1);
		
		switch (getSlotType()) {
		case INSERT:
			return INSERT_SLOT_WIDTH;
		case BRANCH:
		case NEXT:
			return NEXT_SLOT_WIDTH;
		default:
			return BLOCK_SLOT_WIDTH;
		}
	}

	@Override
	protected double computePrefHeight(double width) {
		if(hasBlock())
			return getBlock().prefHeight(-1);
		
		switch (getSlotType()) {
		case INSERT:
			return INSERT_SLOT_HEIGHT;
		case BRANCH:
		case NEXT:
			return NEXT_SLOT_HEIGHT;
		default:
			return BLOCK_SLOT_HEIGHT;
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

	double getLineWidth() {
		switch (getSlotType()) {
		case BRANCH:
			return lineWidth<BRANCH_MIN_WIDTH?BRANCH_MIN_WIDTH:lineWidth;
		case NEXT:
			return 0;
		default:
			return lineWidth<BLOCK_SLOT_MIN_LINE_WIDTH?BLOCK_SLOT_MIN_LINE_WIDTH:lineWidth;
		}
	}

	void setLineWidth(double lineWidth) {
		this.lineWidth = lineWidth;
	}

	int getFirstNode() {
		return firstNode;
	}
	
	int getLastNode() {
		return lastNode;
	}

	void setNodeRange(int firstNode,int lastNode) {
		this.firstNode = firstNode;
		this.lastNode = lastNode;
	}
}