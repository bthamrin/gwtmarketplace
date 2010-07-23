/*
 * GNU GENERAL PUBLIC LICENSE, Version 3, 29 June 2007
 */
package hudson.gwtmarketplace.client.commands;

import hudson.gwtmarketplace.client.Session;
import hudson.gwtmarketplace.client.event.TopsDateCheckEvent;
import hudson.gwtmarketplace.client.event.TopsUpdatedEvent;
import hudson.gwtmarketplace.client.model.Top10Lists;

import java.util.Date;

import com.google.gwt.user.client.rpc.AsyncCallback;

public abstract class GetTopsCommand extends AbstractAsyncCommand<Top10Lists> {

	private static Top10Lists top10Lists;

	private boolean doCheck = false;

	static {
		Session.get()
				.bus()
				.addHandler(TopsDateCheckEvent.TYPE,
						new TopsDateCheckEvent.TopsDateCheckHandler() {

							@Override
							public void onTopsDateCheck(Date date) {
								if (null != date
										&& (null == top10Lists || date
												.getTime() > top10Lists
												.getMaxDate().getTime())) {
									refresh();
								}
							}
						});
	}

	private static void refresh() {
		productService().getTops(
				(null != top10Lists) ? top10Lists.getMaxDate() : null,
				new AsyncCallback<Top10Lists>() {

					@Override
					public void onSuccess(Top10Lists result) {
						if (null != result) {
							System.out.println(result);
							System.out.println("Success: "
									+ result.getMostViewed().size());
							GetTopsCommand.top10Lists = result;
							Session.get().bus()
									.fireEvent(new TopsUpdatedEvent(result));
						}
					}

					@Override
					public void onFailure(Throwable caught) {
						caught.printStackTrace();
					}
				});
	}

	public void execute() {
		if (doCheck || null == top10Lists || null == top10Lists.getMaxDate()) {
			productService().getTops(
					(null != top10Lists) ? top10Lists.getMaxDate() : null,
					new AsyncCallback<Top10Lists>() {

						@Override
						public void onSuccess(Top10Lists result) {
							GetTopsCommand.top10Lists = result;
							GetTopsCommand.this.onSuccess(result);
						}

						@Override
						public void onFailure(Throwable caught) {
							GetTopsCommand.this.onFailure(caught);
						}
					});
		} else {
			onSuccess(top10Lists);
		}
	}
}
