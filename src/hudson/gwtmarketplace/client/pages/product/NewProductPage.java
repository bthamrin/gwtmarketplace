package hudson.gwtmarketplace.client.pages.product;

import hudson.gwtmarketplace.client.Pages;
import hudson.gwtmarketplace.client.model.Pair;
import hudson.gwtmarketplace.client.model.Product;
import hudson.gwtmarketplace.client.pages.PageStateAware;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.SimplePanel;

public class NewProductPage extends SimplePanel implements PageStateAware {

	EditProductPage wrapped;

	public NewProductPage() {
		add(this.wrapped = new EditProductPage(true) {
			public void onSave() {
				super.onSave();
			};
			public void onCancel() {
				if (Window.confirm("Are you sure you want to cancel?")) {
					Pages.gotoPage(Pages.PAGE_DEFAULT);
				}
			};
		});
	}

	@Override
	public void onShowPage(String[] parameters) {
		wrapped.show(new Pair<Product, String>(new Product(), null));
	}

	@Override
	public void onExitPage() {
		wrapped.onExitPage();
	}

	@Override
	public Type getPageType() {
		return Type.STANDARD_SECURE;
	}
}
