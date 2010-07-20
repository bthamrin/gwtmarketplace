package hudson.gwtmarketplace.client.ajaxfeeds;

public class Entry {
	private String title;
	private String link;
	private String content;
	private String contentSnippet;
	private String publishedDate;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getContentSnippet() {
		return contentSnippet;
	}

	public void setContentSnippet(String contentSnippet) {
		this.contentSnippet = contentSnippet;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getPublishedDate() {
		return publishedDate;
	}

	public void setPublishedDate(String publishedDate) {
		this.publishedDate = publishedDate;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}