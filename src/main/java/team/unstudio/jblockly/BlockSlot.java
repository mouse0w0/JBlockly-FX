package team.unstudio.jblockly;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.StringProperty;
import javafx.beans.property.StringPropertyBase;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Parent;
import javafx.scene.layout.Region;
import team.unstudio.jblockly.util.IBlockBuilder;

public class BlockSlot extends Region implements BlockGlobal,IBlockly,IBlockInput<Block>{

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
//		Block defaultBlock = getDefaultBlock().build();
//		if(oldBlock!=null&&defaultBlock!=oldBlock)
		if(oldBlock!=null)
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
	
	//TODO:
	private Block cacheDefaultBlock;
	private ObjectProperty<IBlockBuilder> defaultBlock;
	private final ObjectProperty<IBlockBuilder> defaultBlockProperty(){
		if(defaultBlock == null){
			defaultBlock = new ObjectPropertyBase<IBlockBuilder>() {
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
	public final IBlockBuilder getDefaultBlock() {return defaultBlock==null?null:defaultBlock.get();}
	public final boolean setDefaultBlock(IBlockBuilder builder) {
		defaultBlockProperty().set(builder);
		return true;
	}
	public final boolean hasDefaultBlock(){return getDefaultBlock()!=null;}
	
	private ReadOnlyObjectWrapper<BlockWorkspace> workspace;
	private final ReadOnlyObjectWrapper<BlockWorkspace> workspacePropertyImpl(){
		if(workspace==null){
			workspace = new ReadOnlyObjectWrapper<BlockWorkspace>(this, "workspace");
		}
		return workspace;
	}
	private final void setWorkspace(BlockWorkspace workspace){workspacePropertyImpl().set(workspace);}
	public final BlockWorkspace getWorkspace(){return workspace==null?null:workspace.get();}
	public final ReadOnlyObjectProperty<BlockWorkspace> workspaceProperty(){return workspacePropertyImpl().getReadOnlyProperty();}
	private final ChangeListener<BlockWorkspace> workspaceListener = (observable, oldValue, newValue)->setWorkspace(newValue);
	
	public BlockSlot() {
		this(SlotType.NONE);
	}

	public BlockSlot(SlotType slotType) {
		setSlotType(slotType);
		
		setPickOnBounds(false);
		
		parentProperty().addListener((observable, oldValue, newValue)->{
			if(oldValue instanceof IBlockly){
				((IBlockly) oldValue).workspaceProperty().removeListener(workspaceListener);
			}
			if(newValue instanceof IBlockly){
				IBlockly blockly = (IBlockly) newValue;
				setWorkspace(blockly.getWorkspace());
				blockly.workspaceProperty().addListener(workspaceListener);
			}else{
				setWorkspace(null);
			}
		});
	}
	
	public BlockSlot(SlotType slotType,IBlockBuilder defaultBlock) {
		this(slotType);
		setDefaultBlock(defaultBlock);
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
	
	private StringProperty name;
	@Override
	public StringProperty name() {
		if(name==null){
			name = new StringPropertyBase() {
				
				@Override
				public String getName() {
					return "name";
				}
				
				@Override
				public Object getBean() {
					return BlockSlot.this;
				}
			};
		}
		return name;
	}
	@Override
	public String getName() {
		return null;
	}
	@Override
	public void setName(String name) {name().set(name);}
	@Override
	public Block getValue() {return getBlock();}
	@Override
	public void setValue(Block value) {setBlock(value);}
}