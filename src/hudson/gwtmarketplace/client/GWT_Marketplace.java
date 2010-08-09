/*
 * GNU GENERAL PUBLIC LICENSE, Version 3, 29 June 2007
 */
package hudson.gwtmarketplace.client;

import hudson.gwtmarketplace.client.commands.LoginCommand;
import hudson.gwtmarketplace.client.model.UserInfo;
import hudson.gwtmarketplace.client.pages.LayoutPage;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.gwtpages.client.Pages;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class GWT_Marketplace implements EntryPoint {

	private LayoutPage layoutPage;

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		Pages
				.init(this.layoutPage = new LayoutPage(), new PageLoader(),
						new HandlerManager(null), true)
				.addDefaultEventHandlers()
				.add(new AuthenticationPageEventHandler());
		RootPanel.get("content").add(layoutPage);

		// load the current logged in user
		new LoginCommand() {
			@Override
			public void onSuccess(UserInfo result) {
				Pages.get().showStartPage(true);
			}

			public void onFailure(Throwable e) {
				Pages.get().showStartPage(false);
			};
		}.execute();
	}
}