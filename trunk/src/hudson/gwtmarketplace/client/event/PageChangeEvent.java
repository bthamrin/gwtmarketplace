/*
 * GNU GENERAL PUBLIC LICENSE, Version 3, 29 June 2007
 */
package hudson.gwtmarketplace.client.event;

import hudson.gwtmarketplace.client.util.Message;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class PageChangeEvent extends
		GwtEvent<PageChangeEvent.PageChangeHandler> {
	
	public interface PageChangeHandler extends EventHandler {
		void onPageChange(boolean addToken, String pageToken, String... parameters);
	}

	String pageToken;
	String[] parameters;
	boolean addToken;
	Message message;

	public PageChangeEvent(String pageToken, String... parameters) {
		this.pageToken = pageToken;
		this.parameters = parameters;
		this.addToken = true;
	}

	public PageChangeEvent(String pageToken, Message message, String... parameters) {
		this.pageToken = pageToken;
		this.parameters = parameters;
		this.addToken = true;
		this.message = message;
	}

	public static final GwtEvent.Type<PageChangeEvent.PageChangeHandler> TYPE = new GwtEvent.Type<PageChangeHandler>();

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<PageChangeHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(PageChangeEvent.PageChangeHandler handler) {
		handler.onPageChange(addToken, pageToken, parameters);
	}

	public Message getMessage() {
		return message;
	}

}
