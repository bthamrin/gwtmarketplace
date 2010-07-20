package hudson.gwtmarketplace.client.pages;

import java.util.List;

import hudson.gwtmarketplace.client.Pages;
import hudson.gwtmarketplace.client.ajaxfeeds.Entry;
import hudson.gwtmarketplace.client.ajaxfeeds.EntryDiv;
import hudson.gwtmarketplace.client.ajaxfeeds.Feed;
import hudson.gwtmarketplace.client.ajaxfeeds.FeedListener;
import hudson.gwtmarketplace.client.commands.GetTopsCommand;
import hudson.gwtmarketplace.client.components.Section;
import hudson.gwtmarketplace.client.model.Product;
import hudson.gwtmarketplace.client.model.Top10Lists;
import hudson.gwtmarketplace.client.pages.product.ProductSectionEntry;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;

public class MainPage extends Composite implements PageStateAware, FeedListener {

	interface Binder extends UiBinder<FlowPanel, MainPage> {
	}

	private static Binder uiBinder = GWT.create(Binder.class);

	private Top10Lists top10Lists;
	private boolean blogfeedLoaded = false;

	@UiField
	Section newAndUpdated;
	@UiField
	Section topRated;
	@UiField
	Section mostActive;
	@UiField
	HorizontalPanel horizPanel;
	@UiField
	Section blogFeed;

	public MainPage() {
		initWidget(uiBinder.createAndBindUi(this));
		horizPanel.setCellWidth(horizPanel.getWidget(0), "33%");
		horizPanel.setCellWidth(horizPanel.getWidget(1), "34%");
		horizPanel.setCellWidth(horizPanel.getWidget(2), "33%");
		blogFeed.addStyleName("gwtblogContainer");
	}

	@Override
	public void onShowPage(String[] parameters) {
		new GetTopsCommand() {

			@Override
			public void onSuccess(Top10Lists result) {
				refresh(result);
			}
		}.execute();
		
		if (!blogfeedLoaded) {
			Feed f = new Feed();
			f.getFeed("http://feeds.feedburner.com/blogspot/NWLT?format=xml", this);
		}
	}

	public void refresh(Top10Lists top10Lists) {
		this.top10Lists = top10Lists;
		newAndUpdated.clear();
		topRated.clear();
		mostActive.clear();

		List<Product> products = top10Lists.getRecentUpdates();
		for (int i=0; i<products.size(); i++)
			newAndUpdated.add(new ProductSectionEntry(products.get(i), ProductSectionEntry.TYPE_UPDATE_AGE, i));
		products = top10Lists.getHighestRated();
		for (int i=0; i<products.size(); i++)
			topRated.add(new ProductSectionEntry(products.get(i), ProductSectionEntry.TYPE_RATING, i));
		products = top10Lists.getMostViewed();
		for (int i=0; i<products.size(); i++)
			mostActive.add(new ProductSectionEntry(products.get(i), ProductSectionEntry.TYPE_ACTIVE_VIEWS, i));
	}

	@Override
	public void onExitPage() {
		// TODO Auto-generated method stub

	}

	@Override
	public Type getPageType() {
		return Type.STANDARD;
	}

	@Override
	public void onSuccess(Feed feed) {
		blogfeedLoaded = true;
		for (int i=0; i<feed.getEntries().size(); i++)
			blogFeed.add(new EntryDiv(feed.getEntries().get(i), i));
	}

	@Override
	public void onFailure() {
	}
}