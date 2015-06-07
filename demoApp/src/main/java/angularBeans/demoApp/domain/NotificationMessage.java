package angularBeans.demoApp.domain;

public class NotificationMessage {

	private String title;
	private String body;
	private String type;
	private String image;
	private boolean broadcast;

	public NotificationMessage(String type, String title, String body, boolean broadcast) {
		super();
		this.title = title;
		this.body = body;
		this.type = type;
		this.broadcast = broadcast;
	}

	public NotificationMessage() {
	}

	public String getTitle() {
		return title;
	}

	public String getBody() {
		return body;
	}

	public String getType() {
		return type;
	}

	public boolean isBroadcast() {
		return broadcast;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}
}
