package team.unstudio.jblockly.util;

import java.util.HashMap;
import java.util.Map;

public class BlockBuilderManager {
	public final static BlockBuilderManager INSTANCE = new BlockBuilderManager();
	
	private final Map<String,IBlockBuilder> REGISTERED_BLOCK_BUILDERS = new HashMap<>();
	
	public void registerBlockBuilder(IBlockBuilder builder){
		REGISTERED_BLOCK_BUILDERS.put(builder.getRegistyName(), builder);
	}
	
	public IBlockBuilder getBlockBuilder(String name){
		if(name == null||name.isEmpty())
			return null;
		return REGISTERED_BLOCK_BUILDERS.get(name);
	}
}
