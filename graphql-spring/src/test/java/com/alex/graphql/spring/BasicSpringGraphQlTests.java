package com.alex.graphql.spring;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.TestInstance;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.context.ActiveProfiles;

import com.alex.graphql.core.generated.DgsConstants;
import com.alex.graphql.core.model.Post;
import com.alex.graphql.core.testing.BasicGraphQlTests;

import graphql.ErrorType;
import reactor.test.StepVerifier;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("no-security")
abstract class BasicSpringGraphQlTests extends BasicGraphQlTests {

	void test_query_all_posts(GraphQlTester tester) {

		var titles = tester
				.document(getAllPostsQuery())
					.execute()
					.path(JSON_PATH_ALL_POST_TITLES)
					.entityList(String.class)
					.get();

		validatePostTitles(titles);
	}

	void test_query_get_post(GraphQlTester tester) {

		var postTitle = tester
				.document(getPostByIdQuery("1"))
					.execute()
					.path(JSON_PATH_GET_POST_TITLE)
					.entity(String.class)
					.get();

		validatePostTitle(postTitle);
	}

	void test_query_get_post_author(GraphQlTester tester) {

		var authorName = tester
				.document(getPostByIdQuery("1"))
					.execute()
					.path(JSON_PATH_GET_POST_AUTHOR_NAME)
					.entity(String.class)
					.get();

		validateAuthorName(authorName);
	}

	void test_create_post(GraphQlTester tester) {

		var post = tester
				.document(getCreatePostMutation(getNewPost()))
					.execute()
					.path(JSON_PATH_CREATE_POST)
					.entity(Post.class)
					.get();

		validateNewPost(post);
	}

	void test_query_error(GraphQlTester tester) {

		var response = tester.document(BAD_QUERY).execute();
		response
				.errors()
					.expect(error -> error.getMessage().startsWith("Validation error"))
					.expect(error -> error.getErrorType().equals(ErrorType.ValidationError))
					.expect(error -> error.getLocations().get(0).getLine() == 3)
					.verify();
	}

	void test_subscriptions(GraphQlTester tester) {

		var subscriptionPost = tester
				.document(getRandomPostSubscription())
					.executeSubscription()
					.toFlux(DgsConstants.SUBSCRIPTION.RandomPost, Post.class);
		StepVerifier
				.create(subscriptionPost)
					.assertNext(post -> assertThat(post.getTitle()).isNotEmpty())
					.assertNext(post -> assertThat(post.getTitle()).isNotEmpty())
					.assertNext(post -> assertThat(post.getTitle()).isNotEmpty())
					.thenCancel()
					.verify();
	}

	/**
	 * Testing subscriptions over HTTP (SSE) is still not supported, so I created
	 * the test to detect when support is added.
	 */
	void test_subscriptions_sse_failing(GraphQlTester tester) {

		assertThrows(UnsupportedOperationException.class, () -> {
			tester
					.document(getRandomPostSubscription())
						.executeSubscription()
						.toFlux(DgsConstants.SUBSCRIPTION.RandomPost, Post.class);
		});
	}
}
