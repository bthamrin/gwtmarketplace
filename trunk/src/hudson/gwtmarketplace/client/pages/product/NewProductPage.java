/*
 * GNU GENERAL PUBLIC LICENSE, Version 3, 29 June 2007
 */
package hudson.gwtmarketplace.client.pages.product;

import gwtpages.client.page.Page;
import gwtpages.client.page.Pages;
import gwtpages.client.page.parameters.PageParameters;
import hudson.gwtmarketplace.client.model.Pair;
import hudson.gwtmarketplace.client.model.Product;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class NewProductPage extends SimplePanel implements Page {

	EditProductPage wrapped;

	public NewProductPage() {
		add(this.wrapped = new EditProductPage(true) {
			public void onSave() {
				super.onSave();
			};
			public void onCancel() {
				if (Window.confirm("Are you sure you want to cancel?")) {
					Pages.get().showStartPage(false);
				}
			};
		});
	}

	@Override
	public void onShowPage(PageParameters parameters) {
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
