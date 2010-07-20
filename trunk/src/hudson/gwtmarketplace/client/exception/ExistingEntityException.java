package hudson.gwtmarketplace.client.exception;

public class ExistingEntityException extends Exception {

	private String propertyName;

	public ExistingEntityException() {
	}

	public ExistingEntityException(String propertyName) {
		this.propertyName = propertyName;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}
}
