package team.unstudio.jblockly.component;

import java.util.function.Predicate;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import team.unstudio.jblockly.Block;
import team.unstudio.jblockly.BlockWorkspace;
import team.unstudio.jblockly.IBlockly;
import team.unstudio.jblockly.component.skin.BlockListSkin;
import team.unstudio.jblockly.provider.IBlockProvider;

public class BlockList extends Control implements IBlockly{
	
	private ListProperty<IBlockProvider> providers;
	public final ListProperty<IBlockProvider> providersProperty(){
		if(providers==null)
			providers = new SimpleListProperty<IBlockProvider>(this, "providers", FXCollections.observableArrayList());
		return providers;
	}
	
	private ReadOnlyObjectWrapper<BlockWorkspace> workspace;
	public final ReadOnlyObjectWrapper<BlockWorkspace> workspacePropertyImpl(){
		if(workspace==null)
			workspace = new ReadOnlyObjectWrapper<BlockWorkspace>(this, "workspace");
		return workspace;
	}
	public final void setWorkspace(BlockWorkspace workspace){workspacePropertyImpl().set(workspace);}
	public final BlockWorkspace getWorkspace(){return workspace==null?null:workspace.get();}
	public final ReadOnlyObjectProperty<BlockWorkspace> workspaceProperty(){return workspacePropertyImpl().getReadOnlyProperty();}
	
	private DoubleProperty spacing;
	public final DoubleProperty spacingProperty(){
		if(spacing == null)
			spacing = new SimpleDoubleProperty(this,"spacing") {
				
				@Override
				public void invalidated() {
					requestLayout();
				}
			};
		return spacing;
	}
	public final double getSpacing(){return spacing==null?20:spacing.get();}
	public final void setSpacing(double value){spacingProperty().set(value);}
	
	private ObjectProperty<Predicate<Block>> filter;
	public final ObjectProperty<Predicate<Block>> filterProperty(){
		if(filter == null)
			filter = new SimpleObjectProperty<Predicate<Block>>(this, "filter"){
				@Override
				public void invalidated() {
					requestLayout();
				}
			};
		return filter;
	}
	public final Predicate<Block> getFilter(){return filter==null?null:filter.get();}
	public final void setFilter(Predicate<Block> value){filterProperty().set(value);}
	
	private static final String DEFAULT_STYLE_CLASS = "block-list";
	public BlockList() {
		getStyleClass().setAll(DEFAULT_STYLE_CLASS);
		
		setPadding(new Insets(20));
	}
	
	@Override
	protected Skin<?> createDefaultSkin() {
		return new BlockListSkin(this);
	}
}
