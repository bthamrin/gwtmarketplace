/*
 * GNU GENERAL PUBLIC LICENSE, Version 3, 29 June 2007
 */
package hudson.gwtmarketplace.client;

import gwtpages.client.Settings;
import gwtpages.client.page.Page;
import gwtpages.client.page.PageMetaData;
import gwtpages.client.page.PagePresenter;
import gwtpages.client.page.Pages;
import gwtpages.client.page.parameters.PageParameters;
import hudson.gwtmarketplace.client.commands.LoginCommand;
import hudson.gwtmarketplace.client.model.UserInfo;
import hudson.gwtmarketplace.client.pages.LayoutPage;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class GWT_Marketplace implements EntryPoint, PagePresenter {

	private LayoutPage layoutPage;
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() { 
		Settings.init(this, new PageLoader(), new HandlerManager(null))
			.addPageEventHandler(new AuthenticationPageEventHandler());
		RootPanel.get("content").add(this.layoutPage = new LayoutPage());

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

	@Override
	public void showPage(Page page, PageMetaData metaData, PageParameters parameters) {
		layoutPage.showPage(page, metaData, parameters);
	}
}