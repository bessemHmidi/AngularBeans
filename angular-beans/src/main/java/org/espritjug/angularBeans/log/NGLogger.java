package org.espritjug.angularBeans.log;

import java.io.Serializable;
import java.util.LinkedList;

import org.espritjug.angularBeans.context.NGSessionScoped;

@NGSessionScoped
public class NGLogger implements Serializable {

	public enum Level {
		LOG("log"), INFO("info"), WARN("warn"), ERROR("error"), DEBUG("debug");

		private String level;

		private Level(String level) {
			this.level = level;
		}

		public String getLevel() {
			return level;
		}
	}

	private LinkedList<LogMessage> logPool = new LinkedList<LogMessage>();

	// private String UID;
	//
	// public void setUID(String uID) {
	// UID = uID;
	// }

	public void log(Level level, String message) {
		// LinkedList<LogMessage> logPool=locator.getLogPool(UID);
		logPool.addLast(new LogMessage(level.getLevel(), message));

	}

	public LogMessage poll(String UID) {
		// LinkedList<LogMessage> logPool=locator.getLogPool(UID);
		return logPool.pollFirst();
	}

	public LinkedList<LogMessage> getLogPool() {
		// LinkedList<LogMessage> logPool=locator.getLogPool(UID);
		return logPool;
	}

}
