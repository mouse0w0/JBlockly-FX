package team.unstudio.jblockly;

import javafx.beans.property.ReadOnlyObjectProperty;

public interface IBlockly {
	public BlockWorkspace getWorkspace();
	public ReadOnlyObjectProperty<BlockWorkspace> workspaceProperty();
}
