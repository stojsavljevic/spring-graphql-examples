package com.alex.graphql.core.testing;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Year;
import java.util.List;

import com.alex.graphql.core.generated.DgsConstants;
import com.alex.graphql.core.generated.client.AllPostsGraphQLQuery;
import com.alex.graphql.core.generated.client.AllPostsProjectionRoot;
import com.alex.graphql.core.generated.client.CreatePostGraphQLQuery;
import com.alex.graphql.core.generated.client.CreatePostProjectionRoot;
import com.alex.graphql.core.generated.client.PostByIdGraphQLQuery;
import com.alex.graphql.core.generated.client.PostByIdProjectionRoot;
import com.alex.graphql.core.generated.client.RandomPostGraphQLQuery;
import com.alex.graphql.core.generated.client.RandomPostProjectionRoot;
import com.alex.graphql.core.model.Post;
import com.alex.graphql.core.model.PostInput;
import com.netflix.graphql.dgs.client.codegen.GraphQLQueryRequest;

public abstract class BasicGraphQlTests {

	final String POST_1_TITLE = "Pet Sematary";

	final String POST_2_TITLE = "Animal Farm";

	final String POST_3_TITLE = "The Brothers Karamazov";

	final String AUTHOR_1_NAME = "Stephen King";

	final String NEW_POST_TITLE = "The Shining";

	final String NEW_POST_CONTENT = "I believe that children are our future. Unless we stop them now.";

	protected final String JSON_PATH_ALL_POST_TITLES = "data." + DgsConstants.QUERY.AllPosts + "[*]." + DgsConstants.POST.Title;

	protected final String JSON_PATH_GET_POST_TITLE = "data." + DgsConstants.QUERY.PostById + "." + DgsConstants.POST.Title;

	protected final String JSON_PATH_GET_POST_AUTHOR_NAME = "data." + DgsConstants.QUERY.PostById + "." + DgsConstants.POST.Author + "." + DgsConstants.AUTHOR.Name;

	protected final String JSON_PATH_CREATE_POST = "data." + DgsConstants.MUTATION.CreatePost;

	protected final String JSON_PATH_RANDOM_POST = "data." + DgsConstants.SUBSCRIPTION.RandomPost;

	protected final String BAD_QUERY = "{\n"
			+ "  allPosts {\n"
			+ "    WRONG_FIELD\n"
			+ "    author {\n"
			+ "      name\n"
			+ "    }\n"
			+ "  }\n"
			+ "}";

	protected void validatePostTitles(List<String> titles) {
		assertThat(titles).contains(POST_1_TITLE, POST_2_TITLE, POST_3_TITLE);
	}

	protected void validatePostTitle(String postTitle) {
		assertThat(postTitle).isEqualTo(POST_1_TITLE);
	}

	protected void validateAuthorName(String authorName) {
		assertThat(authorName).isEqualTo(AUTHOR_1_NAME);
	}

	protected void validateNewPost(Post post) {
		assertThat(post.getTitle()).isEqualTo(NEW_POST_TITLE);
		assertThat(post.getContent()).isEqualTo(NEW_POST_CONTENT);
		assertThat(post.getReleaseYear()).isEqualTo(Year.now().getValue());
	}

	protected PostInput getNewPost() {
		PostInput postInput = new PostInput();
		postInput.setAuthorId("1");
		postInput.setTitle(NEW_POST_TITLE);
		postInput.setContent(NEW_POST_CONTENT);
		return postInput;
	}

	@SuppressWarnings("rawtypes")
	protected String getPostByIdQuery(String postId) {
		GraphQLQueryRequest allAuthorsRequest = new GraphQLQueryRequest(
				PostByIdGraphQLQuery.newRequest().id(postId).build(),
				new PostByIdProjectionRoot().title().content().author().name()
		);
		return allAuthorsRequest.serialize();
	}

	protected String getAllPostsQuery() {
		GraphQLQueryRequest allPostsRequest = new GraphQLQueryRequest(
				AllPostsGraphQLQuery.newRequest().build(),
				new AllPostsProjectionRoot<>().title().author().name()
		);
		return allPostsRequest.serialize();
	}

	protected String getCreatePostMutation(PostInput postInput) {
		GraphQLQueryRequest createPostRequest = new GraphQLQueryRequest(
				CreatePostGraphQLQuery.newRequest().postInput(postInput).build(),
				new CreatePostProjectionRoot<>().title().content().releaseYear()
		);
		return createPostRequest.serialize();
	}

	protected String getRandomPostSubscription() {
		GraphQLQueryRequest randomPostRequest = new GraphQLQueryRequest(
				RandomPostGraphQLQuery.newRequest().build(),
				new RandomPostProjectionRoot<>().title().content().releaseYear()
		);
		return randomPostRequest.serialize();
	}
}
