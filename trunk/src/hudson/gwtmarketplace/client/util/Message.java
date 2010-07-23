/*
 * GNU GENERAL PUBLIC LICENSE, Version 3, 29 June 2007
 */
package hudson.gwtmarketplace.client.util;

import com.google.gwt.user.client.ui.Focusable;

public class Message {

	public static Message error(String text) {
		return new Message(text, null, TYPE_ERROR);
	}
	
	public static Message error(String text, Focusable widget) {
		return new Message(text, widget, TYPE_ERROR);
	}
	
	public static Message success(String text) {
		return new Message(text, null, TYPE_SUCCESS);
	}
	
	public static final int TYPE_ERROR = 1;
	public static final int TYPE_SUCCESS = 2;
	
	private String message;
	private Focusable component;
	private int type;

	private Message(String message, Focusable component, int type) {
		this.message = message;
		this.component = component;
		this.type = type;
	}

	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Focusable getComponent() {
		return component;
	}
	public void setComponent(Focusable component) {
		this.component = component;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
}
