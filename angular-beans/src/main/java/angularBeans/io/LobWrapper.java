package angularBeans.io;

import java.util.Arrays;


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
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(data);
		result = prime * result + ((owner == null) ? 0 : owner.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LobWrapper other = (LobWrapper) obj;
		if (!Arrays.equals(data, other.data))
			return false;
		if (owner == null) {
			if (other.owner != null)
				return false;
		} else if (!owner.equals(other.owner))
			return false;
		return true;
	}
	
	
}
