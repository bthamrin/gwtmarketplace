package hudson.gwtmarketplace.client;

import hudson.gwtmarketplace.client.model.UserInfo;
import hudson.gwtmarketplace.client.pages.LayoutPage;
import hudson.gwtmarketplace.client.util.Message;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.Focusable;

public class Session {

	private static final Session instance = new Session();

	public static final Session get() {
		return instance;
	}

	private Session() {
	}

	private LayoutPage messageContainer;
	private UserInfo loggedInUser;
	private String loginUrl;
	private String logoutUrl;
	private HandlerManager handlerManager = new HandlerManager(null);

	public UserInfo getLoggedInUser() {
		return loggedInUser;
	}

	public void setLoggedInUser(UserInfo loggedInUser) {
		this.loggedInUser = loggedInUser;
	}

	public HandlerManager bus() {
		return handlerManager;
	}

	public void setMessageContainer(LayoutPage messageContainer) {
		this.messageContainer = messageContainer;
	}

	public void clearMessages() {
		messageContainer.clearMessages();
	}

	public void addMessages(List<Message> messages) {
		messageContainer.addMessages(messages);
	}

	public void success(String message) {
		messageContainer.addMessages(Arrays.asList(new Message[] { Message
				.success(message) }));
	}

	public void error(String message, Focusable component) {
		messageContainer.addMessages(Arrays.asList(new Message[] { Message
				.error(message, component) }));
	}

	public String getLoginUrl() {
		return loginUrl;
	}

	public void setLoginUrl(String loginUrl) {
		this.loginUrl = loginUrl;
	}

	public String getLogoutUrl() {
		return logoutUrl;
	}

	public void setLogoutUrl(String logoutUrl) {
		this.logoutUrl = logoutUrl;
	}
}