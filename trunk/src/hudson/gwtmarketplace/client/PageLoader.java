package hudson.gwtmarketplace.client;

import hudson.gwtmarketplace.client.pages.MainPage;
import hudson.gwtmarketplace.client.pages.product.EditProductPage;
import hudson.gwtmarketplace.client.pages.product.NewProductPage;
import hudson.gwtmarketplace.client.pages.product.ProductSearchPage;
import hudson.gwtmarketplace.client.pages.product.ViewProductPage;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.gwtpages.client.Pages;
import com.google.gwt.gwtpages.client.page.LoadedPageContainer;
import com.google.gwt.gwtpages.client.page.loader.PageLoadCallback;
import com.google.gwt.gwtpages.client.page.loader.SimplePageLoader;

public class PageLoader extends SimplePageLoader {

	public static final String PAGE_DEFAULT = "";
	public static final String PAGE_VIEW_PRODUCT = "_view";
	public static final String PAGE_SEARCH = "_search";
	public static final String PAGE_NEW_PRODUCT = "_new";
	public static final String PAGE_EDIT_PRODUCT = "_edit";

	public static final String SEARCH_CATEGORY = "cat";

	private static final List<String> VIEW_PRODUCT_PAGE_TOKEN_ARR = Arrays
			.asList(new String[] { PAGE_VIEW_PRODUCT });

	private ViewProductPage viewProductPage;

	public PageLoader() {

	}

	@Override
	public boolean isValidPageToken(String token) {
		return super.isValidPageToken(token) || !token.startsWith("_");
	}

	/**
	 * Allow a wildcard for the product alias as the page token because all
	 * registered pages start with '_'
	 */
	@Override
	public void getPage(String pageToken, PageLoadCallback pageHandler) {
		if (null == pageToken || pageToken.equals(PAGE_DEFAULT)
				|| pageToken.startsWith("_")) {
			super.getPage(pageToken, pageHandler);
		} else {
			pageHandler.onPageFound(new LoadedPageContainer(pageToken, viewProductPage,
					null, this));
		}
	}

	@Override
	public void init(Pages settings) {
		viewProductPage = new ViewProductPage();
		registerPage(PAGE_DEFAULT, new MainPage());
		registerPage(PAGE_NEW_PRODUCT, new NewProductPage(),
				AuthenticationPageEventHandler.createPageAttributes());
		registerPage(PAGE_VIEW_PRODUCT, viewProductPage);
		registerPage(PAGE_EDIT_PRODUCT, new EditProductPage(),
				AuthenticationPageEventHandler.createPageAttributes());
		registerPage(PAGE_SEARCH, new ProductSearchPage());
	}
}