package com.alex.graphql.core.model;

public class Author extends com.alex.graphql.core.generated.types.Author {

	private String id;

	Author() {
	}

	public Author(String id, String name, String email) {
		setId(id);
		setName(name);
		setEmail(email);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
