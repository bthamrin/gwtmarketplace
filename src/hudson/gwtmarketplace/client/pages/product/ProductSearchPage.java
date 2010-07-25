/*
 * GNU GENERAL PUBLIC LICENSE, Version 3, 29 June 2007
 */
package hudson.gwtmarketplace.client.pages.product;

import hudson.gwtmarketplace.client.commands.GetProductCategoriesCommand;
import hudson.gwtmarketplace.client.components.LabeledListBox;
import hudson.gwtmarketplace.client.components.LabeledTextBox;
import hudson.gwtmarketplace.client.model.Category;
import hudson.gwtmarketplace.client.model.Product;
import hudson.gwtmarketplace.client.model.search.SearchResults;
import hudson.gwtmarketplace.client.pages.PageStateAware;
import hudson.gwtmarketplace.client.pages.product.EditProductPage.MyUiBinder;
import hudson.gwtmarketplace.client.service.ProductService;
import hudson.gwtmarketplace.client.service.ProductServiceAsync;
import hudson.gwtmarketplace.client.util.WidgetUtil;

import java.util.ArrayList;
import java.util.HashMap;

import org.look.widgets.client.datatable.ColumnMetaData;
import org.look.widgets.client.datatable.DataRecord;
import org.look.widgets.client.datatable.DataTable;
import org.look.widgets.client.datatable.DataTable.ChangeSortListener;
import org.look.widgets.client.datatable.MetaData;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SubmitButton;

public class ProductSearchPage extends Composite implements PageStateAware,
		ChangeSortListener, ClickHandler {

	interface MyUiBinder extends UiBinder<FlowPanel, ProductSearchPage> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	private static ProductServiceAsync productService = GWT
			.create(ProductService.class);
	private static DateTimeFormat dateFormat = DateTimeFormat
			.getMediumDateFormat();
	private static NumberFormat ratingFormat = NumberFormat.getFormat("0.00");
	private HashMap<String, String> params = new HashMap<String, String>();
	private ArrayList<String> generalParams = new ArrayList<String>();

	@UiField
	LabeledTextBox searchFor;
	@UiField
	LabeledTextBox tag;
	@UiField
	LabeledListBox category;
	@UiField
	SimplePanel searchResultsContainer;
	@UiField
	DisclosurePanel searchPanel;
	@UiField
	SubmitButton searchBtn;

	private DataTable table;
	private MetaData metaData;
	private String sortColumn = "name";
	private boolean orderingAsc = true;
	private Integer knownRowCount;

	public ProductSearchPage() {
		initWidget(uiBinder.createAndBindUi(this));

		metaData = new MetaData();
		metaData.setColumnData(new ColumnMetaData[] {
				new SimpleColumn("Product", "name"),
				new SimpleColumn("Website", "website"),
				new SimpleColumn("Last Updated", "updatedDate"),
				new SimpleColumn("Rating", "rating"), });
		table = new DataTable();
		table.setSortListener(this);
		searchResultsContainer.add(table);
		searchPanel.setHeader(new Label("Search Options"));
		searchBtn.addClickHandler(this);

		new GetProductCategoriesCommand() {
			@Override
			public void onSuccess(ArrayList<Category> result) {
				WidgetUtil.load(category.getComponent(), result,
						"Select a category");
			}
		}.execute();
	}

	private void onSearch() {
		params.clear();
		generalParams.clear();
		if (null != tag.getComponent().getValue()
				&& tag.getComponent().getValue().trim().length() > 0) {
			params.put("tag", tag.getComponent().getValue().trim());
		}
		int index = category.getComponent().getSelectedIndex();
		if (index > 1) {
			params.put("category", category.getComponent().getValue(index));
		}
		String searchFor = this.searchFor.getComponent().getValue().trim();
		if (null != searchFor && searchFor.length() > 0) {
			String[] arr = searchFor.split(" ");
			if (arr.length > 0) {
				for (String s : arr)
					generalParams.add(s);
			}
		}
		resetGrid();
	}

	@Override
	public void onShowPage(String[] parameters) {
		generalParams.clear();
		boolean valueSet = false;
		if (parameters.length > 0) {
			String[] arr = parameters[0].split(" ");
			params.clear();
			for (String s : arr) {
				if (s.length() > 0) {
					valueSet = true;
					int index = s.indexOf(':');
					if (index > 0 && s.length() > index) {
						String key = s.substring(0, index);
						String value = s.substring(index + 1);
						params.put(key, value);
					} else {
						generalParams.add(s);
					}
				}
			}
		}
		String _tag = params.get("tag");
		if (null != _tag) tag.getComponent().setValue(_tag);
		String _category = params.get("category");
		if (null != _category) WidgetUtil.selectValue(category.getComponent(), _category);
		if (generalParams.size() > 0) {
			StringBuilder sb = new StringBuilder();
			for (String s : generalParams) {
				if (sb.length() > 0) sb.append(" ");
				sb.append(s);
			}
			searchFor.getComponent().setValue(sb.toString());
		}
		resetGrid();
	}

	@Override
	public void onExitPage() {
		generalParams.clear();
		params.clear();
		sortColumn = "name";
		orderingAsc = true;
		knownRowCount = null;
		tag.getComponent().setValue(null);
		searchFor.getComponent().setValue(null);
		category.setSelectedIndex(0);
	}

	private void resetGrid() {
		productService.search(params, generalParams, 0, 100, sortColumn,
				orderingAsc, knownRowCount,
				new AsyncCallback<SearchResults<Product>>() {
					@Override
					public void onSuccess(SearchResults<Product> result) {
						knownRowCount = result.getTotalRowCount();
						DataRecord[] results = new DataRecord[result
								.getEntries().size()];
						for (int i = 0; i < result.getEntries().size(); i++) {
							results[i] = new ProductDataRecord(result
									.getEntries().get(i));
						}
						table.AssignResults(results, metaData);
					}

					@Override
					public void onFailure(Throwable caught) {
						caught.printStackTrace();
					}
				});
	}

	@Override
	public void onChangeSort(int newColumn, boolean newDesc) {
		// TODO Auto-generated method stub

	}

	@Override
	public Type getPageType() {
		return Type.STACK_START;
	}

	private class SimpleColumn implements ColumnMetaData {
		private String title;
		private String width;
		private String key;

		public SimpleColumn(String title, String key) {
			this.title = title;
			this.width = "";
			this.key = key;
		}

		public String getTitle() {
			return title;
		}

		public String getWidth() {
			return "";
		}

		public String getHtml(DataRecord dataRecord) {
			return ((ProductDataRecord) dataRecord).getValue(key);
		}
	}

	@Override
	public void onClick(ClickEvent event) {
		if (event.getSource().equals(searchBtn)) {
			onSearch();
		}
	}

	private class ProductDataRecord implements DataRecord {
		private Product product;

		public ProductDataRecord(Product product) {
			this.product = product;
		}

		public String getValue(String key) {
			if (key.equals("name")) {
				return "<a href=\"#" + product.getAlias() + "\" >"
						+ product.getName() + "</a>";
			} else if (key.equals("updatedDate")) {
				return dateFormat.format(product.getUpdatedDate());
			} else if (key.equals("rating")) {
				if (null == product.getRating())
					return "unrated";
				else
					return ratingFormat.format(product.getRating());
			} else if (key.equals("website")) {
				return "<a href=\"" + product.getWebsiteUrl()
						+ "\" target=\"_blank\">" + product.getWebsiteUrl()
						+ "</a>";
			} else
				return null;
		}
	}
}