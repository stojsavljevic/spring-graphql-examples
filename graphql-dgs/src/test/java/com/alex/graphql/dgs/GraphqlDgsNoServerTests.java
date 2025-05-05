package com.alex.graphql.dgs;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.alex.graphql.core.model.Post;
import com.alex.graphql.core.testing.BasicGraphQlTests;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.graphql.dgs.DgsQueryExecutor;
import com.netflix.graphql.dgs.test.EnableDgsTest;

import graphql.ErrorType;
import graphql.ExecutionResult;
import reactor.test.StepVerifier;

@EnableDgsTest
@SpringBootTest
@ActiveProfiles("no-security")
class GraphqlDgsNoServerTests extends BasicGraphQlTests {

	@Autowired
	DgsQueryExecutor dgsQueryExecutor;

	@SuppressWarnings("unchecked")
	@Test
	void test_query_all_posts() {

		var postsTitles = this.dgsQueryExecutor
				.executeAndExtractJsonPathAsObject(getAllPostsQuery(), JSON_PATH_ALL_POST_TITLES, List.class);
		validatePostTitles(postsTitles);
	}

	@Test
	void test_query_get_post() {

		var postName = this.dgsQueryExecutor
				.executeAndExtractJsonPathAsObject(getPostByIdQuery("1"), JSON_PATH_GET_POST_TITLE, String.class);
		validatePostTitle(postName);
	}

	@Test
	void test_query_get_post_author() {

		var authorName = this.dgsQueryExecutor
				.executeAndExtractJsonPathAsObject(getPostByIdQuery("1"), JSON_PATH_GET_POST_AUTHOR_NAME, String.class);
		validateAuthorName(authorName);
	}

	@Test
	void test_create_post() {

		var post = this.dgsQueryExecutor
				.executeAndExtractJsonPathAsObject(getCreatePostMutation(getNewPost()), JSON_PATH_CREATE_POST, Post.class);
		validateNewPost(post);
	}

	@Test
	void test_query_error() {

		var errors = this.dgsQueryExecutor.execute(BAD_QUERY).getErrors();

		assertThat(errors).hasSize(1);
		assertThat(errors.getFirst().getMessage().startsWith("Validation error"));
		assertThat(errors.getFirst().getErrorType().equals(ErrorType.ValidationError));
		assertThat(errors.getFirst().getLocations().get(0).getLine() == 3);
	}

	@Test
	void test_subscription() {

		Publisher<ExecutionResult> publisher = this.dgsQueryExecutor.execute(getRandomPostSubscription()).getData();

		StepVerifier
				.withVirtualTime(() -> publisher, 3)
					.expectSubscription()
					.thenRequest(3)
					.assertNext(result -> assertThat(toPost(result).getTitle()).isNotEmpty())
					.assertNext(result -> assertThat(toPost(result).getTitle()).isNotEmpty())
					.assertNext(result -> assertThat(toPost(result).getTitle()).isNotEmpty())
					.thenCancel()
					.verify();
	}

	@SuppressWarnings("unchecked")
	Post toPost(ExecutionResult result) {

		Map<String, Object> data = (Map<String, Object>) result.getData();
		return new ObjectMapper().convertValue(data.get("randomPost"), Post.class);
	}
}
