package team.unstudio.jblockly.provider;

import java.util.HashMap;
import java.util.Map;

public enum BlockProviderRegisty {
	INSTANCE;
	
	public final Map<String,IBlockProvider> REGISTERED_BLOCK_PROVIDERS = new HashMap<>();
	
	public void registerBlockProvider(IBlockProvider value){
		REGISTERED_BLOCK_PROVIDERS.put(value.getRegistyName(), value);
	}
	
	public IBlockProvider getBlockProvider(String name){
		if(name == null||name.isEmpty())
			return null;
		return REGISTERED_BLOCK_PROVIDERS.get(name);
	}
}
