package team.unstudio.jblockly.component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.geometry.Point2D;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.stage.Popup;
import team.unstudio.jblockly.BlockWorkspace;
import team.unstudio.jblockly.IBlockly;
import team.unstudio.jblockly.provider.BlockProviderRegisty;
import team.unstudio.jblockly.provider.IBlockProvider;
import team.unstudio.jblockly.util.ui.FXHelper;

public class BlockSearcher extends TextField implements IBlockly{
	
	private ReadOnlyObjectWrapper<BlockWorkspace> workspace;
	public final ReadOnlyObjectWrapper<BlockWorkspace> workspacePropertyImpl(){
		if(workspace==null){
			workspace = new ReadOnlyObjectWrapper<BlockWorkspace>(this, "workspace");
		}
		return workspace;
	}
	public final void setWorkspace(BlockWorkspace workspace){workspacePropertyImpl().set(workspace);}
	public final BlockWorkspace getWorkspace(){return workspace==null?null:workspace.get();}
	public final ReadOnlyObjectProperty<BlockWorkspace> workspaceProperty(){return workspacePropertyImpl().getReadOnlyProperty();}
	
	public BlockSearcher(){
		textProperty().addListener(observable->createSearch());
		focusedProperty().addListener((observable,oldValue,newValue)->{
			if(newValue)
				show();
			else
				hide();
		});
		
		initPopup();
	}
	
	//FIXME
	private Popup content;
	private BlockList list;
	private void initPopup(){
		content = new Popup();
		content.setAutoHide(true);
		
		list = new BlockList();
		list.workspacePropertyImpl().bind(workspacePropertyImpl());
		
		ScrollPane pane = new ScrollPane(list);
		content.getContent().add(pane);
		pane.setPrefWidth(150);
		pane.setPrefHeight(200);
		pane.setPrefViewportWidth(150);
		pane.setPrefViewportHeight(200);
	}
	
	public void show(){
		Point2D pos = FXHelper.getScreenPos(this);
		content.show(this, pos.getX(), pos.getY()+prefHeight(-1));
	}
	
	public void hide(){
		content.hide();
	}
	
	private Thread searchTask;
	private AtomicBoolean cancelled = new AtomicBoolean(false);
	private List<IBlockProvider> providers = new ArrayList<>();

	private void createSearch() {
		if (searchTask != null && searchTask.isAlive()) {
			cancelled.set(true);
			try {
				searchTask.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			cancelled.set(false);
		}

		final String searchKey = getText();
		searchTask = new Thread(() -> {
			providers.clear();
			
			if(!searchKey.isEmpty()){
				for (IBlockProvider provider : BlockProviderRegisty.INSTANCE.REGISTERED_BLOCK_PROVIDERS.values()) {
					if (cancelled.get())
						return;
	
					if (provider.getRegistyName().indexOf(searchKey) != -1)
						providers.add(provider);
				}
			}
			
			Platform.runLater(() -> {
				list.providersProperty().clear();
				list.providersProperty().addAll(providers);
			});
		});
		searchTask.start();
	}
}