/*
 * GNU GENERAL PUBLIC LICENSE, Version 3, 29 June 2007
 */
package hudson.gwtmarketplace.client;

import hudson.gwtmarketplace.client.Pages.TokenAndParameters;
import hudson.gwtmarketplace.client.commands.LoginCommand;
import hudson.gwtmarketplace.client.event.PageChangeEvent;
import hudson.gwtmarketplace.client.event.PageChangeEvent.PageChangeHandler;
import hudson.gwtmarketplace.client.model.UserInfo;
import hudson.gwtmarketplace.client.pages.LayoutPage;
import hudson.gwtmarketplace.client.pages.PageStateAware;
import hudson.gwtmarketplace.client.service.UserInfoService;
import hudson.gwtmarketplace.client.service.UserInfoServiceAsync;

import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class GWT_Marketplace implements EntryPoint, PageChangeHandler,
		ValueChangeHandler<String> {

	private static UserInfoServiceAsync svc = GWT.create(UserInfoService.class);
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
			if (token.equals("clearCache")) {
				svc.clearCache(new AsyncCallback<Void>() {
					@Override
					public void onSuccess(Void result) {
						Session.get().success("the cache was cleared!");
					}

					@Override
					public void onFailure(Throwable caught) {
						Session.get().error(
								"the cache could not be cleared: "
										+ caught.getMessage(), null);
					}
				});
				token = null;
			}
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
	public void onPageChange(final boolean addHistoryToken,
			final String pageToken, final String... params) {
		final Widget page = Pages.getPage(pageToken);
		if (null == page) {
			Window.alert("Unknown page");
		} else {
			Session.get().clearMessages();
			if (page instanceof PageStateAware
					&& ((PageStateAware) page).getPageType().isSecuire()
					&& null == Session.get().getLoggedInUser()) {
				if (null != Session.get().getLoginUrl()) {
					// this means that we are not on the first page but we still want to create a custom
					// url that will go back to the desired page
					StringBuilder callbackUri = new StringBuilder();
					callbackUri.append(Window.Location.getProtocol()).append("//").append(Window.Location.getHost()).append(Window.Location.getPath());
					Map<String, List<String>> parameterMap = Window.Location.getParameterMap();
					if (parameterMap.size() > 0) {
						boolean started = false;
						callbackUri.append("?");
						for (Map.Entry<String, List<String>> entry : parameterMap.entrySet()) {
							for (String s : entry.getValue()) {
								if (started) callbackUri.append("&");
								else started = true;
								callbackUri.append(entry.getKey()).append("=").append(s);
							}
						}
					}
					callbackUri.append("#").append(Pages.tokenize(pageToken, params));
					svc.getLoginUrl(callbackUri.toString(),
							new AsyncCallback<String>() {

								@Override
								public void onSuccess(String result) {
									Window.Location.assign(result);
								}

								@Override
								public void onFailure(Throwable caught) {
									Window.Location.assign(Session.get()
											.getLoginUrl());
								}
							});
				} else {
					// this must have been the first page hit
					new LoginCommand() {
						@Override
						public void onSuccess(UserInfo result) {
							if (result.isLoggedIn()) {
								if (addHistoryToken)
									History.newItem(
											Pages.tokenize(pageToken, params),
											false);
								Widget previousPage = frontPage
										.setCurrentPage(page);
								if (null != previousPage)
									Pages.onUnload(page);
								Pages.onLoad(page, params);
							} else {
								Window.Location.assign(result.getLoginUrl());
							}
						}
					}.execute();
				}
			} else {
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