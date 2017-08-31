package team.unstudio.jblockly.component;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ListPropertyBase;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import team.unstudio.jblockly.BlockWorkspace;
import team.unstudio.jblockly.IBlockly;
import team.unstudio.jblockly.component.skin.BlockListSkin;
import team.unstudio.jblockly.util.provider.IBlockProvider;

public class BlockList extends Control implements IBlockly{
	
	private ListProperty<IBlockProvider> providers;
	public final ListProperty<IBlockProvider> providersProperty(){
		if(providers==null)
			providers = new ListPropertyBase<IBlockProvider>(FXCollections.observableArrayList()) {

				@Override
				public Object getBean() {
					return BlockList.this;
				}

				@Override
				public String getName() {
					return "providers";
				}
			};
		return providers;
	}
	
	private ReadOnlyObjectWrapper<BlockWorkspace> workspace;
	private final ReadOnlyObjectWrapper<BlockWorkspace> workspacePropertyImpl(){
		if(workspace==null){
			workspace = new ReadOnlyObjectWrapper<BlockWorkspace>(this, "workspace");
		}
		return workspace;
	}
	public final void setWorkspace(BlockWorkspace workspace){workspacePropertyImpl().set(workspace);}
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
	
	private static final String DEFAULT_STYLE_CLASS = "block-list";
	public BlockList() {
		getStyleClass().setAll(DEFAULT_STYLE_CLASS);
//		parentProperty().addListener((observable, oldValue, newValue)->{
//			if(newValue instanceof IBlockly){
//				setWorkspace(((IBlockly)newValue).getWorkspace());
//			}else{
//				setWorkspace(null);
//			}
//		});
		
		setPadding(new Insets(20));
		setPrefSize(USE_COMPUTED_SIZE, USE_COMPUTED_SIZE);
	}
	
	@Override
	protected Skin<?> createDefaultSkin() {
		return new BlockListSkin(this);
	}
}
