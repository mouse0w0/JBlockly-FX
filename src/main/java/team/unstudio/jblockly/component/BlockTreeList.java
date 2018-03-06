package team.unstudio.jblockly.component;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import team.unstudio.jblockly.BlockWorkspace;
import team.unstudio.jblockly.IBlockly;
import team.unstudio.jblockly.component.skin.BlockTreeListSkin;

public class BlockTreeList extends Control implements IBlockly{
	private ReadOnlyObjectWrapper<BlockWorkspace> workspace;
	public final ReadOnlyObjectWrapper<BlockWorkspace> workspacePropertyImpl(){
		if(workspace==null)
			workspace = new ReadOnlyObjectWrapper<BlockWorkspace>(this, "workspace");
		return workspace;
	}
	public final void setWorkspace(BlockWorkspace workspace){workspacePropertyImpl().set(workspace);}
	public final BlockWorkspace getWorkspace(){return workspace==null?null:workspace.get();}
	public final ReadOnlyObjectProperty<BlockWorkspace> workspaceProperty(){return workspacePropertyImpl().getReadOnlyProperty();}
	
	public BlockTreeList() {
		// TODO 自动生成的构造函数存根
	}
	
	@Override
	protected Skin<?> createDefaultSkin() {
		return new BlockTreeListSkin(this);
	}
}
