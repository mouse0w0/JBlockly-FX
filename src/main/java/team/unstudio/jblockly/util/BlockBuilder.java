package team.unstudio.jblockly.util;

import java.util.ArrayList;
import java.util.List;

import team.unstudio.jblockly.ConnectionType;
import team.unstudio.jblockly.SlotType;

public final class BlockBuilder {
	
	public class LabelBuilder implements NodeBuilder{

		@Override
		public String name() {
			return "label";
		}
		
	}
	
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
		
	}
	
	public void addBlockSlot(String name,SlotType type){
		
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
		public String name();
	}
}
