package team.unstudio.jblockly.util;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import team.unstudio.jblockly.Block;
import team.unstudio.jblockly.ConnectionType;
import team.unstudio.jblockly.input.BlockSlot;
import team.unstudio.jblockly.input.BlockTextField;
import team.unstudio.jblockly.input.SlotType;

public class SimpleBlockBuilder implements IBlockBuilder{
	
	private static final Map<String,Class<? extends NodeBuilder>> REGISTERED_NODE_BUILDERS = new HashMap<>();
	static{
		REGISTERED_NODE_BUILDERS.put("label", LabelBuilder.class);
		REGISTERED_NODE_BUILDERS.put("blockslot", BlockSlotBuilder.class);
		REGISTERED_NODE_BUILDERS.put("textfield",TextFieldBuilder.class);
	}
	
	public static void registerNodeBuilder(String name,Class<? extends NodeBuilder> clazz){
		REGISTERED_NODE_BUILDERS.put(name, clazz);
	}
	
	public static NodeBuilder newNodeBuilder(JsonObject json){
		if(json==null)
			return null;
		String type = json.get("type").getAsString();
		if(!REGISTERED_NODE_BUILDERS.containsKey(type))
			return null;
		try {
			return REGISTERED_NODE_BUILDERS.get(type).newInstance().fromJson(json);
		} catch (InstantiationException|IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private ConnectionType connectionType = ConnectionType.NONE;
	public ConnectionType getConnectionType() {return connectionType;}
	public SimpleBlockBuilder setConnectionType(ConnectionType connectionType) {
		this.connectionType = connectionType;
		return this;
	}
	
	private String registyName;
	public String getRegistyName(){return registyName;}
	public SimpleBlockBuilder setRegistyName(String registyName){
		this.registyName = registyName;
		return this;
	}
	
	private Color fill;
	public Color getFill(){return fill;}
	public SimpleBlockBuilder setFill(Color fill){
		this.fill = fill;
		return this;
	}
	
	private Color stroke;
	public Color getStroke(){return stroke;}
	public SimpleBlockBuilder setStroke(Color stroke){
		this.stroke = stroke;
		return this;
	}
	
	private final List<NodeBuilder> builders = new LinkedList<>();
	
	public SimpleBlockBuilder() {}
	
	public SimpleBlockBuilder addNodeBuilder(NodeBuilder builder){
		if(builder!=null)
			builders.add(builder);
		return this;
	}
	
	public SimpleBlockBuilder addLabel(String text){
		addNodeBuilder(new LabelBuilder(text));
		return this;
	}
	
	public SimpleBlockBuilder addNextSlot(){
		addBlockSlot("next", SlotType.NEXT);
		return this;
	}
	
	public SimpleBlockBuilder addBlockSlot(){
		addBlockSlot(null,SlotType.NONE);
		return this;
	}
	
	public SimpleBlockBuilder addBlockSlot(String name,SlotType type){
		addBlockSlot(name, type, null);
		return this;
	}
	
	public SimpleBlockBuilder addBlockSlot(String name,SlotType type,SimpleBlockBuilder defaultBlock){
		addNodeBuilder(new BlockSlotBuilder(name, type, defaultBlock));
		return this;
	}
	
	public SimpleBlockBuilder addTextField(String name){
		addTextField(name, null,null);
		return this;
	}
	
	public SimpleBlockBuilder addTextField(String name,String text,String defaultText){
		addNodeBuilder(new TextFieldBuilder(name, text, defaultText));
		return this;
	}
	
	public SimpleBlockBuilder addChoiceBox(String name,String[] objs){
		return this;
	}
	
	public SimpleBlockBuilder addComboBox(String name,String[] objs){
		return this;
	}
	
	public SimpleBlockBuilder addToggleButton(String name){
		addToggleButton(name, false);
		return this;
	}
	
	public SimpleBlockBuilder addToggleButton(String name,boolean defaultValue){
		return this;
	}
	
	public Block build(){
		Block block = new Block();
		block.setConnectionType(connectionType);
		block.setFill(fill==null?Color.GRAY:fill);
		block.setStroke(stroke==null?Color.BLACK:stroke);
		for(NodeBuilder nb:builders)
			block.getChildren().add(nb.build());
		return block;
	}
	
	public String toJson(){
		StringBuilder sb = new StringBuilder("{\"name\":\"").append(getRegistyName())
				.append("\",\"connectionType\":\"").append(connectionType.name())
				.append("\",\"fill\":\"").append(fill==null?Color.GRAY:fill)
				.append("\",\"stroke\":\"").append(stroke==null?Color.BLACK:stroke)
				.append("\",\"children\":[");
		builders.stream().forEach(builder->sb.append(builder.toJson()).append(","));
		return sb.substring(0, sb.length()-1)+"]}";
	}
	
	public SimpleBlockBuilder fromJson(String json){
		builders.clear();
		
		JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject();
		setRegistyName(jsonObject.get("name").getAsString());
		setConnectionType(ConnectionType.valueOf(jsonObject.get("connectionType").getAsString()));
		setFill(Color.valueOf(jsonObject.get("fill").getAsString()));
		setStroke(Color.valueOf(jsonObject.get("stroke").getAsString()));
		jsonObject.get("children").getAsJsonArray().forEach(element->{
			if(element.isJsonObject())
				addNodeBuilder(newNodeBuilder(element.getAsJsonObject()));
		});
		
		return this;
	}

	public static final String NODE_TYPE = "NODE_TYPE";
	
	public static interface NodeBuilder{
		public Node build();
		public String getRegistyName();
		public String toJson();
		public NodeBuilder fromJson(JsonObject jsonObject);
	}
	
	public static final class LabelBuilder implements NodeBuilder{
		
		private String text;
		
		public LabelBuilder() {
			this("");
		}
		
		public LabelBuilder(String text) {
			this.text = text;
		}

		@Override
		public String getRegistyName() {
			return "label";
		}

		@Override
		public Node build() {
			return new Label(text);
		}

		@Override
		public String toJson() {
			JsonObject object = new JsonObject();
			object.addProperty(NODE_TYPE, getRegistyName());
			object.addProperty("text", text);
			return object.toString();
		}

		@Override
		public NodeBuilder fromJson(JsonObject jsonObject) {
			text = jsonObject.get("text").getAsString();
			return this;
		}
		
	}
	
	public static final class BlockSlotBuilder implements NodeBuilder{
		
		private String name;
		private SlotType slotType;
		private IBlockBuilder defaultBlock;
		
		public BlockSlotBuilder(String name,SlotType slotType,SimpleBlockBuilder defaultBlock) {
			this.name = name==null?"":name;
			this.slotType = slotType==null?SlotType.NONE:slotType;
			this.defaultBlock = defaultBlock;
		}
		
		@Override
		public Node build() {
			BlockSlot slot = new BlockSlot(slotType);
			if(defaultBlock!=null)
				slot.setDefaultBlock(defaultBlock);
			if(name!=null&&!name.isEmpty())
				slot.setName(name);
			return slot;
		}

		@Override
		public String getRegistyName() {
			return "blockslot";
		}

		@Override
		public String toJson() {
			JsonObject object = new JsonObject();
			object.addProperty(NODE_TYPE, getRegistyName());
			object.addProperty("type", slotType.name());
			object.addProperty("defaultBlock", defaultBlock==null?"":defaultBlock.getRegistyName());
			object.addProperty("name", name);
			return object.toString();
		}

		@Override
		public NodeBuilder fromJson(JsonObject jsonObject) {
			name = jsonObject.get("name").getAsString();
			slotType = jsonObject.has("slotType")?SlotType.valueOf(jsonObject.get("slotType").getAsString()):SlotType.NONE;
			defaultBlock = BlockBuilderManager.INSTANCE.getBlockBuilder(jsonObject.get("defaultBlock").getAsString());
			return this;
		}
		
	}
	
	public static final class TextFieldBuilder implements NodeBuilder{
		
		private String name;
		private String text;
		private String defaultText;

		public TextFieldBuilder(String name,String text,String defaultText) {
			this.name = name;
			this.text = text;
			this.defaultText = defaultText;
		}

		@Override
		public Node build() {
			BlockTextField node = new BlockTextField();
			node.setName(name);
			node.setText(text);
			node.setPromptText(defaultText);
			return node;
		}

		@Override
		public String getRegistyName() {
			return "textfield";
		}

		@Override
		public String toJson() {
			JsonObject object = new JsonObject();
			object.addProperty(NODE_TYPE, getRegistyName());
			object.addProperty("text", text);
			object.addProperty("defaultText", defaultText);
			object.addProperty("name", name);
			return null;
		}

		@Override
		public NodeBuilder fromJson(JsonObject jsonObject) {
			text = jsonObject.get("text").getAsString();
			defaultText = jsonObject.get("defaultText").getAsString();
			name = jsonObject.get("name").getAsString();
			return null;
		}
		
	}
}
