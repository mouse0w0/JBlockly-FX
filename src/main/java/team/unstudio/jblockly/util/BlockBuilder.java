package team.unstudio.jblockly.util;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

import javafx.scene.Node;
import javafx.scene.control.Label;
import team.unstudio.jblockly.Block;
import team.unstudio.jblockly.BlockSlot;
import team.unstudio.jblockly.ConnectionType;
import team.unstudio.jblockly.SlotType;

public final class BlockBuilder {
	
	private static final Map<String,BlockBuilder> REGISTERED_BLOCK_BUILDERS = new HashMap<>();
	private static final Map<String,Class<? extends NodeBuilder>> REGISTERED_NODE_BUILDERS = new HashMap<>();
	static{
		REGISTERED_NODE_BUILDERS.put("label", LabelBuilder.class);
		REGISTERED_NODE_BUILDERS.put("blockslot", BlockSlotBuilder.class);
	}
	
	public static void registerBlockBuilder(BlockBuilder builder){
		REGISTERED_BLOCK_BUILDERS.put(builder.getRegistyName(), builder);
	}
	
	public static BlockBuilder getBlockBuilder(String name){
		if(name == null||name.isEmpty())
			return null;
		return REGISTERED_BLOCK_BUILDERS.get(name);
	}
	
	public static void registerNodeBuilder(String name,Class<? extends NodeBuilder> clazz){
		REGISTERED_NODE_BUILDERS.put(name, clazz);
	}
	
	public static NodeBuilder newNodeBuilder(JSONObject json){
		if(json==null)
			return null;
		String type = json.getString("type");
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
	public BlockBuilder setConnectionType(ConnectionType connectionType) {
		this.connectionType = connectionType;
		return this;
	}
	
	private String registyName;
	public String getRegistyName(){return registyName;}
	public BlockBuilder setRegistyName(String registyName){
		this.registyName = registyName;
		return this;
	}
	
	private final List<NodeBuilder> builders = new LinkedList<>();
	
	public BlockBuilder() {}
	
	public BlockBuilder addNodeBuilder(NodeBuilder builder){
		if(builder!=null)
			builders.add(builder);
		return this;
	}
	
	public BlockBuilder addLabel(String text){
		addNodeBuilder(new LabelBuilder(text));
		return this;
	}
	
	public BlockBuilder addNextSlot(){
		addBlockSlot("next", SlotType.NEXT);
		return this;
	}
	
	public BlockBuilder addBlockSlot(){
		addBlockSlot(null,SlotType.NONE);
		return this;
	}
	
	public BlockBuilder addBlockSlot(String name,SlotType type){
		addBlockSlot(name, type, null);
		return this;
	}
	
	public BlockBuilder addBlockSlot(String name,SlotType type,BlockBuilder defaultBlock){
		addNodeBuilder(new BlockSlotBuilder(name, type, defaultBlock));
		return this;
	}
	
	public BlockBuilder addTextField(String name){
		addTextField(name, null);
		return this;
	}
	
	public BlockBuilder addTextField(String name,String defaultText){
		return this;
	}
	
	public BlockBuilder addChoiceBox(String name,String[] objs){
		return this;
	}
	
	public BlockBuilder addComboBox(String name,String[] objs){
		return this;
	}
	
	public BlockBuilder addToggleButton(String name){
		addToggleButton(name, false);
		return this;
	}
	
	public BlockBuilder addToggleButton(String name,boolean defaultValue){
		return this;
	}
	
	public Block build(){
		Block block = new Block();
		block.setConnectionType(connectionType);
		for(NodeBuilder nb:builders)
			block.getChildren().add(nb.build());
		return block;
	}
	
	public String toJson(){
		StringBuilder sb = new StringBuilder("{\"name\":\"").append(getRegistyName())
				.append("\",\"connectionType\":\"").append(connectionType.name()).append("\",\"children\":[");
		builders.stream().forEach(builder->sb.append(builder.toJson()).append(","));
		return sb.substring(0, sb.length()-1)+"]}";
	}
	
	public BlockBuilder fromJson(String json){
		builders.clear();
		
		JSONObject jsonObject = JSONObject.parseObject(json);
		setRegistyName(jsonObject.getString("name"));
		setConnectionType(ConnectionType.valueOf(jsonObject.getString("connectionType")));
		for(Object obj:jsonObject.getJSONArray("children")){
			if(!(obj instanceof JSONObject))
				continue;
			
			addNodeBuilder(newNodeBuilder((JSONObject) obj));
		}
		
		return this;
	}

	public static interface NodeBuilder{
		public Node build();
		public String getRegistyName();
		public String toJson();
		public NodeBuilder fromJson(JSONObject jsonObject);
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
			return String.format("{\"text\":\"%s\",\"type\":\"%s\"}", text, getRegistyName());
		}

		@Override
		public NodeBuilder fromJson(JSONObject jsonObject) {
			text = jsonObject.getString("text");
			return this;
		}
		
	}
	
	public static final class BlockSlotBuilder implements NodeBuilder{
		
		private String name;
		private SlotType slotType;
		private BlockBuilder defaultBlock;
		
		public BlockSlotBuilder() {
			this(null);
		}
		
		public BlockSlotBuilder(String name,SlotType slotType,BlockBuilder defaultBlock) {
			this.name = name==null?"":name;
			this.slotType = slotType;
			this.defaultBlock = defaultBlock;
		}
		
		public BlockSlotBuilder(String name,SlotType slotType) {
			this(name,slotType,null);
		}
		
		public BlockSlotBuilder(String name) {
			this(name,SlotType.NONE);
		}
		
		@Override
		public Node build() {
			BlockSlot slot = new BlockSlot(slotType);
			if(defaultBlock!=null)
				slot.setDefaultBlock(defaultBlock.build());
			if(name!=null&&!name.isEmpty())
				Block.setNodeName(slot, name);
			return slot;
		}

		@Override
		public String getRegistyName() {
			return "blockslot";
		}

		@Override
		public String toJson() {
			return String.format("{\"name\":\"%s\",\"slotType\":\"%s\",\"defaultBlock\":\"%s\",\"type\":\"%s\"}", name,slotType.name(),defaultBlock==null?"":defaultBlock.getRegistyName(),getRegistyName());
		}

		@Override
		public NodeBuilder fromJson(JSONObject jsonObject) {
			name = jsonObject.getString("name");
			slotType = jsonObject.containsKey("slotType")?SlotType.valueOf(jsonObject.getString("slotType")):SlotType.NONE;
			defaultBlock = getBlockBuilder(jsonObject.getString("defaultBlock"));
			return this;
		}
		
	}
}
