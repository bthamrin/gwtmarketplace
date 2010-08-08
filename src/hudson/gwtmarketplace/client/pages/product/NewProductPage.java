/*
 * GNU GENERAL PUBLIC LICENSE, Version 3, 29 June 2007
 */
package hudson.gwtmarketplace.client.pages.product;

import hudson.gwtmarketplace.client.model.Pair;
import hudson.gwtmarketplace.client.model.Product;

import com.google.gwt.gwtpages.client.page.AsyncPageCallback;
import com.google.gwt.gwtpages.client.page.GWTPages;
import com.google.gwt.gwtpages.client.page.GWTPagesPresenter;
import com.google.gwt.gwtpages.client.page.PageRequestSession;
import com.google.gwt.gwtpages.client.page.parameters.PageParameters;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class NewProductPage extends SimplePanel implements GWTPagesPresenter {

	EditProductPage wrapped;

	public NewProductPage() {
		add(this.wrapped = new EditProductPage(true) {
			public void onSave() {
				super.onSave();
			};
			public void onCancel() {
				if (Window.confirm("Are you sure you want to cancel?")) {
					GWTPages.get().showStartPage(false);
				}
			};
		});
	}

	@Override
	public void onShowPage(PageParameters parameters,
			PageRequestSession session, AsyncPageCallback callback) {
		wrapped.show(new Pair<Product, String>(new Product(), null));
	}

	@Override
	public void onHidePage() {
		wrapped.onHidePage();
	}

	@Override
	public Widget asWidget() {
		return this;
	}
}
