package hudson.gwtmarketplace.client.pages.product;

import hudson.gwtmarketplace.client.Pages;
import hudson.gwtmarketplace.client.Session;
import hudson.gwtmarketplace.client.ajaxfeeds.EntryDiv;
import hudson.gwtmarketplace.client.ajaxfeeds.Feed;
import hudson.gwtmarketplace.client.ajaxfeeds.FeedListener;
import hudson.gwtmarketplace.client.components.ProductRating;
import hudson.gwtmarketplace.client.event.ProductUpdatedEvent;
import hudson.gwtmarketplace.client.event.ProductUpdatedEvent.ProductUpdateHandler;
import hudson.gwtmarketplace.client.model.License;
import hudson.gwtmarketplace.client.model.Product;
import hudson.gwtmarketplace.client.model.Status;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ProductDetailsPanel extends Composite implements FeedListener,
		ClickHandler, ProductUpdateHandler {

	interface MyUiBinder extends UiBinder<HorizontalPanel, ProductDetailsPanel> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	private static DateTimeFormat dateFormat = DateTimeFormat
			.getMediumDateFormat();
	private static NumberFormat ratingFormat = NumberFormat.getFormat("0.00");

	private Product product;

	@UiField
	DivElement description;
	@UiField
	DivElement tags;
	@UiField
	VerticalPanel links;
	@UiField
	SpanElement versionNumber;
	@UiField
	AnchorElement organization;
	@UiField
	SpanElement createdDate;
	@UiField
	SpanElement lastUpdatedDate;
	@UiField
	SpanElement developmentStatus;
	@UiField
	SpanElement license;
	@UiField
	ImageElement icon;
	@UiField
	FlowPanel newsfeedContainer;
	@UiField
	FlowPanel newsfeed;
	@UiField
	TableRowElement versionNumberRow;
	@UiField
	TableRowElement organizationRow;
	@UiField
	TableRowElement createdDateRow;
	@UiField
	TableRowElement lastUpdatedDateRow;
	@UiField
	TableRowElement developmentStatusRow;
	@UiField
	TableRowElement licenseRow;
	@UiField
	HTMLPanel additionalDetailsContainer;
	@UiField
	HTMLPanel ratingContainer;
	@UiField
	Anchor rateIt;

	public ProductDetailsPanel() {
		HorizontalPanel container = uiBinder.createAndBindUi(this);
		initWidget(container);
		container.setCellWidth(container.getWidget(0), "132px");
		container.getWidget(0).getElement().getStyle()
				.setPaddingRight(12, Unit.PX);
		rateIt.addClickHandler(this);
		Session.get().bus().addHandler(ProductUpdatedEvent.TYPE, this);
	}

	@Override
	public void onProductUpdated(Product product) {
		if (null != this.product && product.equals(this.product)) {
			show(product);
		}
	}

	public void show(Product product) {
		Product _previous = this.product;
		this.product = product;
		links.clear();
		if (null == product) {
			newsfeedContainer.setVisible(false);
			description.setInnerText("");
			tags.setInnerHTML("");
			versionNumber.setInnerText("");
			organization.setInnerText("");
			organization.setHref("#");
			createdDate.setInnerText("");
			lastUpdatedDate.setInnerText("");
			icon.setSrc("images/noicon.gif");
			ratingContainer.getElement().setInnerText("");
		} else {
			if (null != product.getWebsiteUrl()) {
				links.add(new Anchor("Website", product.getWebsiteUrl(),
						"_blank"));
			}
			if (null != product.getDemoUrl()) {
				links.add(new Anchor("Showcase", product.getDemoUrl(), "_blank"));
			}
			if (null != product.getForumUrl()) {
				links.add(new Anchor("Forum", product.getForumUrl(), "_blank"));
			}
			if (null != product.getIssueTrackerUrl()) {
				links.add(new Anchor("Issue Tracker", product.getIssueTrackerUrl(), "_blank"));
			}
			links.add(new Hyperlink("Edit settings", Pages.tokenize(
					Pages.PAGE_EDIT_PRODUCT, product.getAlias())));
			description.setInnerHTML(product.getDescription());
			tags.setInnerHTML(createTagHtml(product.getTags()));
			versionNumber.setInnerText(product.getVersionNumber());
			organization.setInnerText(product.getOrganizationName());
			organization.setHref("#");
			createdDate
					.setInnerText(dateFormat.format(product.getCreatedDate()));
			lastUpdatedDate.setInnerText(dateFormat.format(product
					.getUpdatedDate()));
			if (null == product.getIconKey())
				icon.setSrc("images/noicon.gif");
			else
				icon.setSrc("gwt_marketplace/productImage?key=" + product.getIconKey());
			if (null != product.getRating()) {
				ratingContainer.getElement().setInnerText(ratingFormat.format(product.getRating()) + " of 5");
			}
			else {
				ratingContainer.getElement().setInnerText("");
			}
			developmentStatus.setInnerText(Status.getDisplayValue(product
					.getStatus()));
			license.setInnerHTML(License.getDisplayValue(product.getLicense()));

			if (null == _previous || _previous.equals(product)) {
				newsfeed.clear();
				if (null != product.getNewsUrl()
						&& product.getNewsUrl().length() > 0) {
					newsfeedContainer.setVisible(true);
					Feed f = new Feed();
					f.getFeed(product.getNewsUrl(), this);
				} else {
					newsfeedContainer.setVisible(false);
				}
			}
		}
	}

	@Override
	public void onClick(ClickEvent event) {
		if (event.getSource().equals(rateIt)) {
			onRateIt();
		}
	}

	private void onRateIt() {
		// FIXME add rate it code
	}

	public String createTagHtml(String[] tags) {
		if (null == tags || tags.length == 0) return "(none)";
		else {
			StringBuilder sb = new StringBuilder();
			for (String s : tags) {
				if (sb.length() > 0) sb.append(", ");
				sb.append("<a class=\"tag\" href=\"#").append(Pages.PAGE_SEARCH).append("/tag:").append(s).append("\">").append(s).append("</a>");
			}
			return sb.toString();
		}
	}

	@Override
	public void onSuccess(Feed feed) {
		for (int i=0; i<feed.getEntries().size(); i++) {
			newsfeed.add(new EntryDiv(feed.getEntries().get(i), i));
		}
	}

	@Override
	public void onFailure() {
		Window.alert("Unable to load news feed");
	}
}