package angularBeans.io;

import java.lang.reflect.Method;

public class LobSource {
	
	
	private Object container;
	private Method getter;
	
	
	public LobSource(Object container, Method getter) {
		super();
		this.container = container;
		this.getter = getter;
	
	}	
	
	
	public Method getGetter() {
		return getter;
	}
	
	public Object getContainer() {
		return container;
	}
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((container == null) ? 0 : container.hashCode());
		result = prime * result + ((getter == null) ? 0 : getter.hashCode());
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
		LobSource other = (LobSource) obj;
		if (container == null) {
			if (other.container != null)
				return false;
		} else if (!(container==other.container))
			return false;
		if (getter == null) {
			if (other.getter != null)
				return false;
		} else if (!(getter==other.getter))
			return false;
		return true;
	}
	
	

}
