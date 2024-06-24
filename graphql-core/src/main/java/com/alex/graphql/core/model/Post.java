package com.alex.graphql.core.model;

public class Post extends com.alex.graphql.core.generated.types.Post {

	private String authorId;

	Post() {
	}

	public Post(String id, String title, String content, Integer releaseYear, String authorId) {
		setId(id);
		setTitle(title);
		setReleaseYear(releaseYear);
		setContent(content);
		setAuthorId(authorId);
	}

	public String getAuthorId() {
		return authorId;
	}

	public void setAuthorId(String authorId) {
		this.authorId = authorId;
	}
}