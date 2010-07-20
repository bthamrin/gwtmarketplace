package hudson.gwtmarketplace.client.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Id;

public class Category implements Serializable, DisplayEntity {
	private static final long serialVersionUID = 1L;

	@Id
	private String id;
	@Column
	private String name;
	@Column
	private Integer numProducts;

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getNumProducts() {
		return numProducts;
	}
	public void setNumProducts(Integer numProducts) {
		this.numProducts = numProducts;
	}

	@Override
	public String getIdValue() {
		return getId();
	}

	@Override
	public String getDisplayValue() {
		return getName();
	}
}
