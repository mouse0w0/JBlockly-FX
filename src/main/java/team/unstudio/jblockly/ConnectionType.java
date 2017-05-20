package team.unstudio.jblockly;

public enum ConnectionType {
	
	LEFT(true), 
	TOP(true), 
	BUTTOM(false), 
	TOPANDBOTTOM(true), 
	NONE(false);
	
	private final boolean canConnection;
	
	private ConnectionType(boolean canConnection){
		this.canConnection = canConnection;
	}
	
	public boolean isCanConnection(){
		return canConnection;
	}
}
