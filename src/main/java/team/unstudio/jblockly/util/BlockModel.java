package team.unstudio.jblockly.util;

import java.util.HashMap;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import team.unstudio.jblockly.Block;
import team.unstudio.jblockly.input.IBlockInput;

//TODO:
public final class BlockModel extends HashMap<String, Object>{
	
	public static BlockModel getBlockModel(Block block){
		BlockModel model = new BlockModel(block.getName());
		
		for(javafx.scene.Node node:block.getChildren()){
			if(!(node instanceof IBlockInput))
				continue;
				
			IBlockInput<?> input = (IBlockInput<?>) node;
			if(input instanceof Block)
				model.put(input.getName(), getBlockModel((Block) input));
			else
				model.put(input.getName(), input.getValue());
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
	
	public BlockModel getBlockModel(String key){
		return get(key);
	}
	
	public <T> T get(String key){
		return get(key);
	}
	
	public String toJson() {
		JsonObject object = new JsonObject();
		object.addProperty("name", getName());
		JsonObject data = new JsonObject();
		entrySet().forEach(entry->data.addProperty(entry.getKey(), entry.getValue().toString()));
		object.add("data", data);
		return object.toString();
	}
	
	public void fromJson(String json){
		JsonObject object = new JsonParser().parse(json).getAsJsonObject();
		setName(object.get("name").getAsString());
		object.get("data").getAsJsonObject().entrySet().forEach(entry->put(entry.getKey(), entry.getValue()));
	}
	
	@Override
	public String toString() {
		return toJson();
	}
}
