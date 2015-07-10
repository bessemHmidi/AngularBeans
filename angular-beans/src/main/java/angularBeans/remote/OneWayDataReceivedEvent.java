package angularBeans.remote;

import java.io.Serializable;

import org.projectodd.sockjs.SockJsConnection;

import com.google.gson.JsonObject;

public class OneWayDataReceivedEvent implements DataReceived,Serializable {

	private JsonObject data;
	
	@Override
	public JsonObject getData() {
	
		return data;
	}
	
	
	public OneWayDataReceivedEvent(JsonObject data) {
		
		this.data = data;

	}
}
