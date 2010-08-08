/*
 * GNU GENERAL PUBLIC LICENSE, Version 3, 29 June 2007
 */
package hudson.gwtmarketplace.client.commands;

import hudson.gwtmarketplace.client.event.ProductUpdatedEvent;
import hudson.gwtmarketplace.client.event.TopsDateCheckEvent;
import hudson.gwtmarketplace.client.model.Pair;
import hudson.gwtmarketplace.client.model.Product;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.gwtpages.client.GWTPagesSettings;
import com.google.gwt.user.client.rpc.AsyncCallback;

public abstract class GetProductDetailsCommand extends
		AbstractAsyncCommand<Product> {

	public static Map<String, Product> productAliasMap = new HashMap<String, Product>();
	public static Map<Long, Product> productIdMap = new HashMap<Long, Product>();

	static {
		GWTPagesSettings
				.get()
				.getEventBus()
				.addHandler(ProductUpdatedEvent.TYPE,
						new ProductUpdatedEvent.ProductUpdateHandler() {
							@Override
							public void onProductUpdated(Product product) {
								cache(product);
							}
						});
	}

	private Long productId;
	private String alias;
	private boolean forViewing = true;

	public GetProductDetailsCommand(long productId) {
		this.productId = productId;
	}

	public GetProductDetailsCommand(String alias) {
		this.alias = alias;
	}

	public GetProductDetailsCommand(String alias, boolean forViewing) {
		this.alias = alias;
		this.forViewing = forViewing;
	}

	@Override
	public void execute() {
		Product p = null;
		if (null != productId)
			p = productIdMap.get(productId);
		else if (null != alias)
			p = productAliasMap.get(alias);
		if (null != p)
			onSuccess(p);

		final AsyncCommandCallback callback = new AsyncCommandCallback() {
			public void onSuccess(Product result) {
				cache(result);
				GetProductDetailsCommand.this.onSuccess(result);
			};
		};

		if (forViewing) {
			productService().getForViewing(alias,
					new AsyncCallback<Pair<Product, Date>>() {

						@Override
						public void onSuccess(Pair<Product, Date> result) {
							if (null != result && null != result.getEntity2())
								GWTPagesSettings
										.get()
										.getEventBus()
										.fireEvent(
												new TopsDateCheckEvent(result
														.getEntity2()));
							callback.onSuccess(result.getEntity1());
						}

						@Override
						public void onFailure(Throwable caught) {
							callback.onFailure(caught);
						}
					});
		}
		if (null != productId)
			productService().getById(productId, callback);
		else if (null != alias)
			productService().getByAlias(alias, callback);
		else
			onSuccess(null);
	}

	public static void cache(Product p) {
		if (null == p)
			return;
		productAliasMap.put(p.getAlias(), p);
		productIdMap.put(p.getId(), p);
	}
}