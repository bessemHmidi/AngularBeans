package org.espritjug.angularBeans.log;

public class LogMessage {

	private String level;
	private String message;

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public LogMessage(String level, String message) {
		super();
		this.level = level;
		this.message = message;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "LEVEL: " + getLevel() + " MESSAGE: " + getMessage();
	}

}
