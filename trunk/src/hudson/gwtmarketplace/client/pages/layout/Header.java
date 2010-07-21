package hudson.gwtmarketplace.client.pages.layout;

import hudson.gwtmarketplace.client.Session;
import hudson.gwtmarketplace.client.commands.LoginCommand;
import hudson.gwtmarketplace.client.model.UserInfo;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;

public class Header extends Composite implements ClickHandler {

	interface MyUiBinder extends UiBinder<FlowPanel, Header> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	@UiField
	Anchor loginLogout;
	Boolean isLoggedIn;
	
	public Header() {
		initWidget(uiBinder.createAndBindUi(this));
		loginLogout.setVisible(false);
		loginLogout.addClickHandler(this);
		new LoginCommand() {
			
			@Override
			public void onSuccess(UserInfo result) {
				loginLogout.setVisible(true);
				isLoggedIn = result.isLoggedIn();
				if (result.isLoggedIn()) {
					loginLogout.setText("logout");
				}
				else {
					loginLogout.setText("login");
				}
			}
		}.execute();
	}

	private void onLoginLogout() {
		if (isLoggedIn) {
			Window.Location.assign(Session.get().getLogoutUrl());
		}
		else {
			Window.Location.assign(Session.get().getLoginUrl());
		}
	}

	@Override
	public void onClick(ClickEvent event) {
		if (event.getSource().equals(loginLogout)) {
			onLoginLogout();
		}
	}
}
