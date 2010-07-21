package hudson.gwtmarketplace.client.pages.product;

import hudson.gwtmarketplace.client.model.Product;
import hudson.gwtmarketplace.client.model.search.SearchResults;
import hudson.gwtmarketplace.client.pages.PageStateAware;
import hudson.gwtmarketplace.client.service.ProductService;
import hudson.gwtmarketplace.client.service.ProductServiceAsync;

import java.util.ArrayList;
import java.util.HashMap;

import org.look.widgets.client.datatable.ColumnMetaData;
import org.look.widgets.client.datatable.DataRecord;
import org.look.widgets.client.datatable.DataTable;
import org.look.widgets.client.datatable.DataTable.ChangeSortListener;
import org.look.widgets.client.datatable.MetaData;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;

public class ProductSearchPage extends Composite implements PageStateAware,
		ChangeSortListener {

	private static ProductServiceAsync productService = GWT
			.create(ProductService.class);
	private static DateTimeFormat dateFormat = DateTimeFormat
			.getMediumDateFormat();
	private static NumberFormat ratingFormat = NumberFormat.getFormat("0.00");
	private HashMap<String, String> params = new HashMap<String, String>();
	private ArrayList<String> generalParams = new ArrayList<String>();

	private DataTable table;
	private MetaData metaData;
	private String sortColumn = "name";
	private boolean orderingAsc = true;
	private Integer knownRowCount;

	public ProductSearchPage() {
		SimplePanel panel = new SimplePanel();
		metaData = new MetaData();
		metaData.setColumnData(new ColumnMetaData[] {
				new SimpleColumn("Product", "name"),
				new SimpleColumn("Website", "website"),
				new SimpleColumn("Last Updated", "updatedDate"),
				new SimpleColumn("Rating", "rating"),
		});
		table = new DataTable();
		table.setSortListener(this);
		panel.add(table);
		initWidget(panel);
	}

	@Override
	public void onShowPage(String[] parameters) {
		generalParams.clear();
		if (parameters.length > 0) {
			String[] arr = parameters[0].split(" ");
			params.clear();
			for (String s : arr) {
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
		resetGrid();
	}

	@Override
	public void onExitPage() {
		generalParams.clear();
		params.clear();
		sortColumn = "name";
		orderingAsc = true;
		knownRowCount = null;
	}

	private void resetGrid() {
		productService.search(params, generalParams, 0, 100, sortColumn,
				orderingAsc, knownRowCount,
				new AsyncCallback<SearchResults<Product>>() {
					@Override
					public void onSuccess(SearchResults<Product> result) {
						knownRowCount = result.getTotalRowCount();
						DataRecord[] results = new DataRecord[result.getEntries().size()];
						for (int i=0; i<result.getEntries().size(); i++) {
							results[i] = new ProductDataRecord(result.getEntries().get(i));
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

	private class ProductDataRecord implements DataRecord {
		private Product product;

		public ProductDataRecord(Product product) {
			this.product = product;
		}

		public String getValue(String key) {
			if (key.equals("name")) {
				return "<a href=\"#" + product.getAlias() + "\" >" + product.getName() + "</a>";
			} else if (key.equals("updatedDate")) {
				return dateFormat.format(product.getUpdatedDate());
			} else if (key.equals("rating")) {
				if (null == product.getRating())
					return "unrated";
				else
					return ratingFormat.format(product.getRating());
			} else if (key.equals("website")) {
				return "<a href=\"" + product.getWebsiteUrl() + "\" target=\"_blank\">" + product.getWebsiteUrl() + "</a>";
			} else
				return null;
		}
	}
}