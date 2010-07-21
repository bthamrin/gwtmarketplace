package hudson.gwtmarketplace.client.pages.product;

import hudson.gwtmarketplace.client.Session;
import hudson.gwtmarketplace.client.commands.GetProductDetailsCommand;
import hudson.gwtmarketplace.client.event.ProductCommentEvent;
import hudson.gwtmarketplace.client.event.ProductCommentEvent.ProductCommentHandler;
import hudson.gwtmarketplace.client.model.Product;
import hudson.gwtmarketplace.client.model.ProductComment;
import hudson.gwtmarketplace.client.pages.PageStateAware;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.TabPanel;

public class ViewProductPage extends Composite implements
		SelectionHandler<Integer>, ProductCommentHandler, PageStateAware {

	interface MyUiBinder extends UiBinder<FlowPanel, ViewProductPage> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	private Product product;

	@UiField
	HeadingElement siteTitle;

	@UiField
	TabPanel tabs;

	ProductDetailsPanel productDetails;
	ProductCommentsPanel productComments;

	public ViewProductPage() {
		initWidget(uiBinder.createAndBindUi(this));
		tabs.add(this.productDetails = new ProductDetailsPanel(), "Details",
				false);
		tabs.add(this.productComments = new ProductCommentsPanel(), "Comments",
				false);
		tabs.addSelectionHandler(this);
		tabs.selectTab(0);
		Session.get().bus().addHandler(ProductCommentEvent.TYPE, this);
	}

	public void show(String alias) {
		show((Product) null);
		new GetProductDetailsCommand(alias) {
			@Override
			public void onSuccess(Product product) {
				show(product);
			}
		}.execute();
	}

	public void show(Product product) {
		this.product = product;
		tabs.selectTab(0);
		if (null != product)
			siteTitle.setInnerText(product.getName());
		else
			siteTitle.setInnerText("Loading Details...");
		productDetails.show(product);
		resetCommentTabTitle();
	}

	private void resetCommentTabTitle() {
		if (null == product || product.getNumComments() == 0)
			tabs.getTabBar().setTabText(1, "Comments");
		else
			tabs.getTabBar().setTabText(1,
					"Comments (" + product.getNumComments() + ")");
	}

	@Override
	public void onSelection(SelectionEvent<Integer> event) {
		if (event.getSource().equals(tabs)) {
			if (event.getSelectedItem().equals(1))
				productComments.show(product);
		}
	}

	@Override
	public void onProductCommentAdded(Product product, ProductComment comment) {
		if (null != product
				&& comment.getProductId().getId() == product.getId()
						.longValue()) {
			this.product = product;
			resetCommentTabTitle();
		}
	}

	@Override
	public void onShowPage(String[] parameters) {
		if (parameters.length > 0)
			show(parameters[0]);
	}

	@Override
	public void onExitPage() {
		show((Product) null);
	}

	@Override
	public Type getPageType() {
		return Type.STANDARD;
	}
}