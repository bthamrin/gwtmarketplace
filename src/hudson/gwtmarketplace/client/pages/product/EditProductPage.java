package hudson.gwtmarketplace.client.pages.product;

import hudson.gwtmarketplace.client.Pages;
import hudson.gwtmarketplace.client.Session;
import hudson.gwtmarketplace.client.commands.GetProductCategoriesCommand;
import hudson.gwtmarketplace.client.commands.GetProductDetailsCommand;
import hudson.gwtmarketplace.client.commands.SaveProductCommand;
import hudson.gwtmarketplace.client.components.LabeledContainer;
import hudson.gwtmarketplace.client.components.LabeledListBox;
import hudson.gwtmarketplace.client.components.LabeledTextBox;
import hudson.gwtmarketplace.client.components.RichTextToolbar;
import hudson.gwtmarketplace.client.components.ThreeByXTextBox;
import hudson.gwtmarketplace.client.model.Category;
import hudson.gwtmarketplace.client.model.License;
import hudson.gwtmarketplace.client.model.Product;
import hudson.gwtmarketplace.client.model.Status;
import hudson.gwtmarketplace.client.pages.PageStateAware;
import hudson.gwtmarketplace.client.util.Message;
import hudson.gwtmarketplace.client.util.WidgetUtil;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RichTextArea;

public class EditProductPage extends Composite implements PageStateAware, ChangeHandler, ClickHandler {

	interface MyUiBinder extends UiBinder<HorizontalPanel, EditProductPage> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	Product product;
	
	@UiField
	ImageElement icon;
	@UiField
	FlowPanel descriptionToolbarContainer;
	@UiField
	RichTextArea description;
	@UiField
	ThreeByXTextBox tags;
	@UiField
	LabeledTextBox name;
	@UiField
	LabeledTextBox versionNumber;
	@UiField
	LabeledTextBox organization;
	@UiField
	LabeledListBox status;
	@UiField
	LabeledListBox category;
	@UiField
	LabeledListBox license;
	@UiField
	LabeledTextBox webpageUrl;
	@UiField
	LabeledTextBox showcaseUrl;
	@UiField
	LabeledTextBox forumUrl;
	@UiField
	LabeledTextBox newsfeedUrl;
	@UiField
	Button saveBtn;
	@UiField
	Button cancelBtn;

	public EditProductPage() {
		this(false);
	}

	public EditProductPage(boolean isNew) {
		HorizontalPanel panel = uiBinder.createAndBindUi(this);
		initWidget(panel);
		descriptionToolbarContainer.add(new RichTextToolbar(description));
		panel.setCellWidth(panel.getWidget(0), "132px");
		WidgetUtil.load(status.getComponent(), Status.VALUES, "Choose a status");
		WidgetUtil.load(license.getComponent(), License.VALUES, "Choose a license");
		newsfeedUrl.getComponent().addChangeHandler(this);
		saveBtn.addClickHandler(this);
		cancelBtn.addClickHandler(this);
		if (!isNew)
			name.getComponent().setEnabled(false);
		new GetProductCategoriesCommand() {
			@Override
			public void onSuccess(ArrayList<Category> result) {
				WidgetUtil.load(category.getComponent(), result, "Select a category");
			}
		}.execute();
	}

	public void show(final Product product) {
		this.product = product;
		if (null != product.getDescription())
			description.setHTML(product.getDescription());
		else
			description.setText("");	
		tags.setValues(product.getTags());
		name.setValue(product.getName());
		versionNumber.setValue(product.getVersionNumber());
		organization.setValue(product.getOrganizationName());
		webpageUrl.setValue(product.getWebsiteUrl());
		showcaseUrl.setValue(product.getDemoUrl());
		forumUrl.setValue(product.getForumUrl());
		newsfeedUrl.setValue(product.getNewsUrl());
		WidgetUtil.selectValue(license.getComponent(), product.getLicense());
		WidgetUtil.selectValue(status.getComponent(), product.getStatus());
		new GetProductCategoriesCommand() {
			
			@Override
			public void onSuccess(ArrayList<Category> result) {
				WidgetUtil.selectValue(category.getComponent(), product.getCategoryId());
			}
		}.execute();
		icon.setSrc("images/noicon.gif");
	}

	@Override
	public void onShowPage(String[] parameters) {
		if (parameters.length > 0) {
			new GetProductDetailsCommand(parameters[0], false) {
				
				@Override
				public void onSuccess(Product product) {
					show(product);
				}
			}.execute();
		}
	}

	@Override
	public void onExitPage() {
		show(new Product());
	}

	@Override
	public Type getPageType() {
		return Type.STANDARD_SECURE;
	}

	@Override
	public void onClick(ClickEvent event) {
		if (event.getSource().equals(saveBtn)) {
			onSave();
		}
		else if (event.getSource().equals(cancelBtn)) {
			onCancel();
		}
	}

	@Override
	public void onChange(ChangeEvent event) {
	}

	private boolean isNull(String s) {
		return (null == s || s.trim().length() == 0);
	}
	
	public void onSave() {
		List<Message> messages = new ArrayList<Message>();
		WidgetUtil.checkNull(new LabeledContainer[]{name, category, versionNumber, status, license, webpageUrl}, messages);
		if (isNull(description.getText())) {
			messages.add(Message.error("Please enter the description"));
		}
		if (messages.size() > 0) {
			Session.get().addMessages(messages);
			return;
		}
		
		product.setDescription(description.getHTML());
		if (null == product.getId())
			product.setName(name.getComponent().getValue());
		product.setOrganizationName(organization.getValue());
		product.setVersionNumber(versionNumber.getComponent().getValue());
		product.setStatus(status.getValue(status.getSelectedIndex()));
		product.setLicense(license.getValue(license.getSelectedIndex()));
		product.setCategoryId(category.getValue(category.getSelectedIndex()));
		product.setWebsiteUrl(webpageUrl.getValue());
		product.setDemoUrl(showcaseUrl.getValue());
		product.setForumUrl(forumUrl.getValue());
		product.setNewsUrl(newsfeedUrl.getValue());
		new SaveProductCommand(product) {
			@Override
			public void onSuccess(Product result) {
				Pages.gotoPage(Pages.PAGE_VIEW_PRODUCT, result.getAlias());
			}
		}.execute();
	}

	public void onCancel() {
		if (Window.confirm("Are you sure you want to cancel?")) {
			Pages.gotoPage(Pages.PAGE_VIEW_PRODUCT, product.getAlias());
		}
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}
}