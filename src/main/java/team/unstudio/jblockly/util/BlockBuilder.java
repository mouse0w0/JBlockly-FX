package team.unstudio.jblockly.util;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.Node;
import javafx.scene.control.Label;
import team.unstudio.jblockly.BlockSlot;
import team.unstudio.jblockly.ConnectionType;
import team.unstudio.jblockly.SlotType;

public final class BlockBuilder {
	
	private ConnectionType connectionType;
	public ConnectionType getConnectionType() {return connectionType;}
	public void setConnectionType(ConnectionType connectionType) {this.connectionType = connectionType;}
	
	private final List<NodeBuilder> builders;
	
	public BlockBuilder(ConnectionType connectionType){
		this.builders = new ArrayList<>();
		setConnectionType(connectionType);
	}
	
	public BlockBuilder() {
		this(ConnectionType.NONE);
	}
	
	public void addLabel(String text){
		builders.add(new NodeBuilder() {
			
			@Override
			public Node build() {
				return new Label(text);
			}
		});
	}
	
	public void addBlockSlot(String name,SlotType type){
		builders.add(new NodeBuilder() {
			
			@Override
			public Node build() {
				return new BlockSlot(type);
			}
		});
	}
	
	public void addTextField(String name,String defaultText){
		
	}
	
	public void addChoiceBox(String name){
		
	}
	
	public void addComboBox(String name){
		
	}
	
	public void addToggleButton(String name,boolean defaultValue){
		
	}

	public static interface NodeBuilder{
		Node build();
	}
}
