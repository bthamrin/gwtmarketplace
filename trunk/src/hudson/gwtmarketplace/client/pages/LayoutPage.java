/*
 * GNU GENERAL PUBLIC LICENSE, Version 3, 29 June 2007
 */
package hudson.gwtmarketplace.client.pages;

import hudson.gwtmarketplace.client.pages.layout.Header;
import hudson.gwtmarketplace.client.pages.layout.LeftNav;
import hudson.gwtmarketplace.client.util.Message;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class LayoutPage extends Composite {

	interface Binder extends UiBinder<FlowPanel, LayoutPage> {
	}

	private static Binder uiBinder = GWT.create(Binder.class);
	
	@UiField
	SimplePanel headContainer;
	@UiField
	SimplePanel navContainer;
	@UiField
	SimplePanel bodyContainer;
	@UiField
	FlowPanel messages;
	
	public LayoutPage() {		
		initWidget(uiBinder.createAndBindUi(this));
		headContainer.add(new Header());
		navContainer.add(new LeftNav());
	}

	public Widget getCurrentPage() {
		return bodyContainer.getWidget();
	}

	public Widget setCurrentPage(Widget page) {
		Widget w = getCurrentPage();
		if (null != w)
			bodyContainer.remove(w);
		bodyContainer.add(page);
		return w;
	}

	public void clearMessages() {
		messages.clear();
		messages.setVisible(false);
	}

	public void addMessages(List<Message> messages) {
		if (null == messages || messages.size() == 0) clearMessages();
		else {
			this.messages.clear();
			this.messages.setVisible(true);
			for (Message m : messages) {
				this.messages.add(new MessageEntry(m));
			}
			this.messages.getElement().scrollIntoView();
		}
	}

	public class MessageEntry extends SimplePanel implements ClickHandler {
		private Focusable component;
		
		public MessageEntry (Message message) {
			if (null != message.getComponent()) {
				Anchor anchor = new Anchor(message.getMessage());
				this.component = message.getComponent();
				anchor.addClickHandler(this);
				add(anchor);
			}
			else {
				add(new Label(message.getMessage()));
			}
			if (message.getType() == Message.TYPE_ERROR) {
				addStyleName("error");
			}
			else if (message.getType() == Message.TYPE_SUCCESS) {
				addStyleName("success");
			}
		}
		
		@Override
		public void onClick(ClickEvent event) {
			component.setFocus(true);
		}
	}
}