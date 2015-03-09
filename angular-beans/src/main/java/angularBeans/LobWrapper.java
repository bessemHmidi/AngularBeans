package angularBeans;


public class LobWrapper {

	private Object owner;
	private byte[] data;
	
	public LobWrapper(byte[] data,Object owner) {
		this.owner=owner;
		this.data=data;
	
		
	}
	public Object getOwner() {
		return owner;
	}
	
	public byte[] getData() {
		
		return data;
	}
	
	
}
