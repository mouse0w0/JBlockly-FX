package team.unstudio.jblockly.util.provider;

import team.unstudio.jblockly.Block;

public interface IBlockProvider {
	
	Block build();
	String getRegistyName();
}
