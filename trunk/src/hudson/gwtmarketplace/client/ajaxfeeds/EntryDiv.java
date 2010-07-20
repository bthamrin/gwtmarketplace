package hudson.gwtmarketplace.client.ajaxfeeds;

import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;

public class EntryDiv extends FlowPanel {

	public EntryDiv (Entry entry, int index) {
		add(new Anchor(entry.getTitle(), entry.getLink(), "_blank"));
		if (index % 2 == 0) addStyleName("feed feed-even");
		else addStyleName("feed feed-odd");
	}
}
