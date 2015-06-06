package angularBeans.realtime;


/**
 * 
 * @author bessem
 *
 *an NGEvent will be inserted to a response and 
 *converted to an AngularJS event
 */
public class NGEvent {
	
	
	
	private String name;
	private Object data;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	
	

}
