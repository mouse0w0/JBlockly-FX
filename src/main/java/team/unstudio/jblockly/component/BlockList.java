package team.unstudio.jblockly.component;

import java.util.List;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ListPropertyBase;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import team.unstudio.jblockly.Block;
import team.unstudio.jblockly.BlockWorkspace;
import team.unstudio.jblockly.IBlockly;
import team.unstudio.jblockly.util.IBlockBuilder;

public class BlockList extends Pane implements IBlockly{
	
	private ListProperty<IBlockBuilder> builders;
	public final ListProperty<IBlockBuilder> buildersProperty(){
		if(builders==null)
			builders = new ListPropertyBase<IBlockBuilder>(FXCollections.observableArrayList()) {

				@Override
				public Object getBean() {
					return BlockList.this;
				}

				@Override
				public String getName() {
					return "builders";
				}
			};
		return builders;
	}
	
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
	
	private DoubleProperty spacing;
	public final DoubleProperty spacingProperty(){
		if(spacing == null){
			spacing = new DoublePropertyBase() {
				
				@Override
				public void invalidated() {
					requestLayout();
				}
				
				@Override
				public String getName() {
					return "spacing";
				}
				
				@Override
				public Object getBean() {
					return BlockList.this;
				}
			};
		}
		return spacing;
	}
	public final double getSpacing(){return spacing==null?20:spacing.get();}
	public final void setSpacing(double value){spacingProperty().set(value);}
	
	public BlockList() {
		buildersProperty().addListener((observable, oldValue, newValue)->requestLayout());
		
		parentProperty().addListener((observable, oldValue, newValue)->{
			if(newValue instanceof BlockWorkspace){
				setWorkspace(((IBlockly)newValue).getWorkspace());
			}else{
				setWorkspace(null);
			}
		});
		
		setPadding(new Insets(20));
		setPrefSize(USE_COMPUTED_SIZE, USE_COMPUTED_SIZE);
	}
	
	private boolean updatingBlock;
	private void updateBlock(){
		if(updatingBlock)
			return;
		updatingBlock = true;
		
		getChildren().clear();
		buildersProperty().get().forEach(builder->{
			Block block = builder.build();
			getChildren().add(block);
		});
		
		updatingBlock = false;
	}
	
	private boolean performingLayout;
	@Override
	public void requestLayout() {
		if(updatingBlock)
			return;
		if(performingLayout) 
			return;

		super.requestLayout();
	}

	@Override
	protected void layoutChildren() {
		if(updatingBlock)
			return;
		if(performingLayout) 
			return;
		performingLayout = true;
		
		updateBlock();
		
		double spacing = getSpacing();
		List<Node> managed = getManagedChildren();
		
		Insets insets = getInsets();
		double x = insets.getLeft();
		double y = insets.getTop();
		for(Node node:managed){
			double width = node.prefWidth(-1),height = node.prefHeight(-1);
			layoutInArea(node, x, y, width, height, 0, HPos.LEFT, VPos.TOP);
			y += height + spacing;
		}
		
		performingLayout = false;
	}
	
	@Override
	protected double computePrefWidth(double height) {
		List<Node> managed = getManagedChildren();
		
		double width = 0;
		for(Node node:managed){
			double twidth = node.prefWidth(-1);
			if(width<twidth)
				width=twidth;
		}
		
		Insets insets = getInsets();
		return insets.getLeft() + width + insets.getRight();
	}
	
	@Override
	protected double computePrefHeight(double width) {
		double spacing = getSpacing();
		List<Node> managed = getManagedChildren();
		
		double height = 0;
		for(Node node:managed){
			double theight = node.prefHeight(-1);
			height+=theight+spacing;
		}
		
		Insets insets = getInsets();
		return insets.getTop() + height - spacing + insets.getBottom();
	}
}
