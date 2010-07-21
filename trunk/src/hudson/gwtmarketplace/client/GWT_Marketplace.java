package hudson.gwtmarketplace.client;

import hudson.gwtmarketplace.client.Pages.TokenAndParameters;
import hudson.gwtmarketplace.client.commands.LoginCommand;
import hudson.gwtmarketplace.client.event.PageChangeEvent;
import hudson.gwtmarketplace.client.event.PageChangeEvent.PageChangeHandler;
import hudson.gwtmarketplace.client.model.UserInfo;
import hudson.gwtmarketplace.client.pages.LayoutPage;
import hudson.gwtmarketplace.client.pages.PageStateAware;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class GWT_Marketplace implements EntryPoint, PageChangeHandler,
		ValueChangeHandler<String> {

	private static LayoutPage frontPage;

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		RootPanel.get("content").add(this.frontPage = new LayoutPage());
		Session.get().setMessageContainer(this.frontPage);
		Session.get().bus().addHandler(PageChangeEvent.TYPE, this);
		History.addValueChangeHandler(this);
		String token = History.getToken();
		if (null != token && token.length() > 0) {
			Pages.TokenAndParameters tap = Pages.parse(token);
			if (null == tap) {
				onPageChange(false, Pages.PAGE_DEFAULT, new String[0]);
			} else {
				onPageChange(false, tap.getToken(), tap.getParameters());
			}
		} else {
			onPageChange(false, Pages.PAGE_DEFAULT, new String[0]);
		}
	}

	@Override
	public void onPageChange(final boolean addHistoryToken, final String pageToken,
			final String... params) {
		final Widget page = Pages.getPage(pageToken);
		if (null == page) {
			Window.alert("Unknown page");
		} else {
			if (page instanceof PageStateAware
					&& ((PageStateAware) page).getPageType().isSecuire()
					&& null == Session.get().getLoggedInUser()) {
				if (null != Session.get().getLoginUrl()) {
					Window.Location.assign(Session.get().getLoginUrl());
				}
				else {
					// this must have been the first page hit
					new LoginCommand() {
						@Override
						public void onSuccess(UserInfo result) {
							if (result.isLoggedIn()) {
								if (addHistoryToken)
									History.newItem(Pages.tokenize(pageToken, params), false);
								Widget previousPage = frontPage.setCurrentPage(page);
								if (null != previousPage)
									Pages.onUnload(page);
								Pages.onLoad(page, params);
							}
							else {
								Window.Location.assign(result.getLoginUrl());
							}
						}
					}.execute();
				}
			}
			else {
				if (addHistoryToken)
					History.newItem(Pages.tokenize(pageToken, params), false);
				Widget previousPage = frontPage.setCurrentPage(page);
				if (null != previousPage)
					Pages.onUnload(page);
				Pages.onLoad(page, params);
			}
		}
	}

	@Override
	public void onValueChange(ValueChangeEvent<String> event) {
		TokenAndParameters tap = Pages.parse(event.getValue());
		onPageChange(false, tap.getToken(), tap.getParameters());
	}
}