package hudson.gwtmarketplace.client.ajaxfeeds;

public interface FeedListener {

	public void onSuccess(Feed feed);

	public void onFailure();
}
