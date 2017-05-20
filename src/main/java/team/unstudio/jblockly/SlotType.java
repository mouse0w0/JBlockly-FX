package team.unstudio.jblockly;

public enum SlotType {
	NONE, 
	INSERT(ConnectionType.LEFT), 
	BRANCH(ConnectionType.TOP,ConnectionType.TOPANDBOTTOM), 
	NEXT(ConnectionType.TOP,ConnectionType.TOPANDBOTTOM);
	
	private final ConnectionType[] canConnectionType;
	
	private SlotType(ConnectionType ...canConnectionType) {
		this.canConnectionType = canConnectionType;
	}
	
	public boolean isCanBeConnection(ConnectionType connectionType){
		if(!connectionType.isCanConnection())
			return false;
		for(ConnectionType c:canConnectionType)
			if(connectionType == c)
				return true;
		return false;
	}
}
