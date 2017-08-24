package team.unstudio.jblockly;

import java.util.List;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ListPropertyBase;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
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
					return "vSpacing";
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
		buildersProperty().addListener((observable, oldValue, newValue)->updateBlock());
		
		parentProperty().addListener((observable, oldValue, newValue)->{
			if(newValue instanceof BlockWorkspace)
				setWorkspace(((IBlockly)newValue).getWorkspace());
			else 
				setWorkspace(null);
		});
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
		if(performingLayout) 
			return;

		super.requestLayout();
	}

	@Override
	protected void layoutChildren() {
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
}
