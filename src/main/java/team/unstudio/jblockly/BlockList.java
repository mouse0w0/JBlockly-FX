package team.unstudio.jblockly;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.layout.Pane;
import team.unstudio.jblockly.util.SimpleBlockBuilder;

public class BlockList extends Pane implements IBlockly{
	
	private final ObjectProperty<SimpleBlockBuilder> builders = new ObjectPropertyBase<SimpleBlockBuilder>(){

		@Override
		public Object getBean() {
			return this;
		}

		@Override
		public String getName() {
			return "builders";
		}
		
	};
	public final ObjectProperty<SimpleBlockBuilder> buildersProperty(){
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
	
	public BlockList() {
		builders.addListener((observable, oldValue, newValue)->updateBlock());
		
		parentProperty().addListener((observable, oldValue, newValue)->{
			setWorkspace(null);
			if(newValue instanceof BlockWorkspace)
				setWorkspace(((IBlockly)newValue).getWorkspace());
			else 
				setWorkspace(null);
		});
	}
	
	private void updateBlock(){
		
	}

	@Override
	protected void layoutChildren() {
		
	}
}
