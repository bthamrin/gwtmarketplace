package hudson.gwtmarketplace.client;

import gwtpages.client.page.Page;
import gwtpages.client.page.PageEventHandler;
import gwtpages.client.page.PageLoadResult;
import gwtpages.client.page.PageMetaData;
import gwtpages.client.page.parameters.PageParameters;
import hudson.gwtmarketplace.client.service.UserInfoService;
import hudson.gwtmarketplace.client.service.UserInfoServiceAsync;

import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class AuthenticationPageEventHandler implements PageEventHandler {

	private static final UserInfoServiceAsync svc = GWT.create(UserInfoService.class);
	
	@Override
	public void afterPageShow(PageLoadResult page, PageParameters parameters) {
	}

	@Override
	public boolean onPageRequest(String pageToken, String historyToken, PageMetaData metaData) {
		if (null != metaData && null != metaData.get(AuthenticationData.class)) {
			// this is a secure page
			if (null == Session.get().getLoggedInUser()) {
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
				callbackUri.append("#").append(historyToken);
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
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean beforePageShow(PageLoadResult page, PageParameters parameters) {
		return true;
	}

	@Override
	public void onPageLoaded(PageLoadResult result) {
	}

	@Override
	public Page onPageNotFound(String historyToken) {
		return null;
	}

	@Override
	public Page onPageLoadFailure(String originalToken, Throwable cause) {
		return null;
	}

	public static PageMetaData metaData() {
		return new PageMetaData().add(AuthenticationData.class, new AuthenticationData() {});
	}

	private interface AuthenticationData {
	}
}