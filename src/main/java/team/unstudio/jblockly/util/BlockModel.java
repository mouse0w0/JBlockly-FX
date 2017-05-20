package team.unstudio.jblockly.util;

import java.util.HashMap;

import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import team.unstudio.jblockly.Block;
import team.unstudio.jblockly.BlockSlot;

public class BlockModel extends HashMap<String, Object>{
	
	public static BlockModel getBlockModel(Block block){
		BlockModel model = new BlockModel(Block.getNodeName(block));
		
		for(Entry<String, javafx.scene.Node> entry:block.getNameToNode().entrySet()){
			javafx.scene.Node node = entry.getValue();
			if(node instanceof BlockSlot)
				model.put(entry.getKey(), getBlockModel(((BlockSlot) node).getBlock()));
			else if(node instanceof TextField)
				model.put(entry.getKey(), ((TextField) node).getText());
			else if(node instanceof ChoiceBox)
				model.put(entry.getKey(), ((ChoiceBox<?>) node).getValue());
			else if(node instanceof ComboBox)
				model.put(entry.getKey(), ((ComboBox<?>) node).getValue());
			else if(node instanceof ToggleButton)
				model.put(entry.getKey(), ((ToggleButton) node).isSelected());
		}
		
		return model;
	}

	private String name;

	public BlockModel() {}
	
	public BlockModel(String name) {
		setName(name);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getString(String key){
		return get(key).toString();
	}
	
	public BlockModel getBlockModel(String key){
		return (BlockModel) get(key);
	}
	
	public boolean getBoolean(String key){
		return (boolean) get(key);
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("{\"name\":\"").append(name).append("\",\"data\":{");
		for(Entry<String, Object> key:entrySet()){
			builder.append("\"").append(key.getKey()).append("\":");
			if(key.getValue() instanceof BlockModel)
				builder.append(key.getKey().toString());
			else
				builder.append("\"").append(key.getValue()).append("\"");
		}
		return builder.append("}}").toString();
	}
}
