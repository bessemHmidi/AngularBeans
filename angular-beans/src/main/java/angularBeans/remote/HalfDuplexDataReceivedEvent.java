package angularBeans.remote;

import java.io.Serializable;

import com.google.gson.JsonObject;

public class HalfDuplexDataReceivedEvent implements DataReceived, Serializable {

	private JsonObject data;

	@Override
	public JsonObject getData() {

		return data;
	}

	public HalfDuplexDataReceivedEvent(JsonObject data) {

		this.data = data;

	}
}
