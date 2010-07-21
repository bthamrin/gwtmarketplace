package hudson.gwtmarketplace.client;

import hudson.gwtmarketplace.client.event.PageChangeEvent;
import hudson.gwtmarketplace.client.pages.MainPage;
import hudson.gwtmarketplace.client.pages.PageStateAware;
import hudson.gwtmarketplace.client.pages.product.EditProductPage;
import hudson.gwtmarketplace.client.pages.product.NewProductPage;
import hudson.gwtmarketplace.client.pages.product.ProductSearchPage;
import hudson.gwtmarketplace.client.pages.product.ViewProductPage;
import hudson.gwtmarketplace.client.util.Message;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.user.client.ui.Widget;

public class Pages {

	public static final String PAGE_DEFAULT = "";
	public static final String PAGE_VIEW_PRODUCT = "_view";
	public static final String PAGE_SEARCH = "_search";
	public static final String PAGE_NEW_PRODUCT = "_new";
	public static final String PAGE_EDIT_PRODUCT = "_edit";
	
	public static final String SEARCH_CATEGORY = "cat";
	
	private static final Map<String, Boolean> pMap = new HashMap<String, Boolean>();
	private static final Map<String, Widget> pinstMap = new HashMap<String, Widget>();

	static {
		pMap.put(PAGE_DEFAULT, Boolean.TRUE);
		pMap.put(PAGE_NEW_PRODUCT, Boolean.TRUE);
		pMap.put(PAGE_VIEW_PRODUCT, Boolean.TRUE);
		pMap.put(PAGE_EDIT_PRODUCT, Boolean.TRUE);
		pMap.put(PAGE_SEARCH, Boolean.TRUE);
	}

	public static void gotoPage(String token, String... params) {
		Session.get().bus().fireEvent(new PageChangeEvent(token, params));
	}

	public static void gotoPage(String token, Message message, String... params) {
		Session.get().bus().fireEvent(new PageChangeEvent(token, message, params));
	}

	public static String tokenize(String token, String... params) {
		if (token.equals(PAGE_VIEW_PRODUCT) && null != params && params.length == 1) {
			return params[0];
		} else {
			StringBuilder sb = new StringBuilder();
			sb.append(token);
			if (null != params) {
				for (String param : params) {
					sb.append('/');
					sb.append(param);
				}
			}
			return sb.toString();
		}
	}

	public static boolean isValidToken(String s) {
		if (null == s)
			return false;
		else if (s.startsWith("_"))
			return (null != pMap.get(s));
		else
			return true;
	}

	public static TokenAndParameters parse(String token) {
		if (null == token || token.length() == 0)
			return new TokenAndParameters(PAGE_DEFAULT);
		String[] segments = token.split("/");
		if (segments.length == 1 && !segments[0].startsWith("_")) {
			return new TokenAndParameters(PAGE_VIEW_PRODUCT, segments);
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < segments.length; i++) {
			if (sb.length() > 0)
				sb.append("/");
			sb.append(segments[i]);
			if (isValidToken(sb.toString())) {
				String[] params = new String[segments.length - i - 1];
				System.arraycopy(segments, i + 1, params, 0, params.length);
				return new TokenAndParameters(sb.toString(), params);
			}
		}
		return null;
	}

	public static Widget getPage(String token) {
		if (null == token)
			return null;
		Widget page = pinstMap.get(token);
		if (null != page)
			return page;
		else {
			if (token.equals(PAGE_DEFAULT))
				page = new MainPage();
			else if (token.equals(PAGE_NEW_PRODUCT))
				page = new NewProductPage();
			else if (token.equals(PAGE_VIEW_PRODUCT))
				page = new ViewProductPage();
			else if (token.equals(PAGE_EDIT_PRODUCT))
				page = new EditProductPage();
			else if (token.equals(PAGE_SEARCH))
				page = new ProductSearchPage();
			if (null != page)
				pinstMap.put(token, page);
		}
		return page;
	}

	public static void onUnload(Widget page) {
		if (page instanceof PageStateAware) {
			PageStateAware psa = (PageStateAware) page;
			psa.onExitPage();
		}
	}

	public static void onLoad(Widget page, String[] params) {
		if (page instanceof PageStateAware) {
			((PageStateAware) page).onShowPage(params);
		}
	}

	public static class TokenAndParameters {
		private String token;
		private String[] parameters;

		public TokenAndParameters(String token, String... parameters) {
			this.token = token;
			this.parameters = parameters;
		}

		public String getToken() {
			return token;
		}

		public void setToken(String token) {
			this.token = token;
		}

		public String[] getParameters() {
			return parameters;
		}

		public void setParameters(String[] parameters) {
			this.parameters = parameters;
		}
	}
}