package team.unstudio.jblockly.provider;

import team.unstudio.jblockly.Block;

public interface IBlockProvider {
	
	Block build();
	String getRegistyName();
}
