package hudson.gwtmarketplace.client;

import hudson.gwtmarketplace.client.service.UserInfoService;
import hudson.gwtmarketplace.client.service.UserInfoServiceAsync;

import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.gwtpages.client.page.GWTPage;
import com.google.gwt.gwtpages.client.page.GWTPageAttributes;
import com.google.gwt.gwtpages.client.page.GWTPages;
import com.google.gwt.gwtpages.client.page.PageEventHandler;
import com.google.gwt.gwtpages.client.page.PageRequestSession;
import com.google.gwt.gwtpages.client.page.parameters.PageParameters;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class AuthenticationPageEventHandler implements PageEventHandler {

	private static final UserInfoServiceAsync svc = GWT
			.create(UserInfoService.class);

	@Override
	public void beforePageShow(GWTPage page, PageParameters parameters,
			PageRequestSession session) {
		if (null != page.getAttributes()
				&& null != page.getAttributes().get(AuthenticationData.class)) {
			// this is a secure page
			if (null == Session.get().getLoggedInUser()) {
				StringBuilder callbackUri = new StringBuilder();
				callbackUri.append(Window.Location.getProtocol()).append("//")
						.append(Window.Location.getHost())
						.append(Window.Location.getPath());
				Map<String, List<String>> parameterMap = Window.Location
						.getParameterMap();
				if (parameterMap.size() > 0) {
					boolean started = false;
					callbackUri.append("?");
					for (Map.Entry<String, List<String>> entry : parameterMap
							.entrySet()) {
						for (String s : entry.getValue()) {
							if (started)
								callbackUri.append("&");
							else
								started = true;
							callbackUri.append(entry.getKey()).append("=")
									.append(s);
						}
					}
				}
				callbackUri.append("#").append(History.getToken());
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
				GWTPages.get().stopRequest();
			}
		}
	}

	@Override
	public void afterPageShow(GWTPage pageLoadResult,
			PageParameters parameters, PageRequestSession session) {
	}

	@Override
	public void onPageShowSuccess(GWTPage pageLoadResult,
			PageParameters parameters, PageRequestSession session) {
	}

	@Override
	public void onPageShowFailure(GWTPage pageLoadResult,
			PageParameters parameters, PageRequestSession session) {
	}

	@Override
	public void onPageRequest(String pageToken, String historyToken,
			PageRequestSession session) {
	}

	@Override
	public void onPageLoaded(GWTPage result) {
	}

	@Override
	public void onPageNotFound(String historyToken) {
	}

	@Override
	public void onPageLoadFailure(String historyToken, Throwable cause) {
	}

	public static GWTPageAttributes metaData() {
		return new GWTPageAttributes().put(AuthenticationData.class,
				new AuthenticationData() {
				});
	}

	private interface AuthenticationData {
	}
}