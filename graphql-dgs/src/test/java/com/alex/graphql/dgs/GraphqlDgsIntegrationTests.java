package com.alex.graphql.dgs;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;

import com.alex.graphql.core.model.Post;
import com.alex.graphql.core.model.PostInput;
import com.alex.graphql.core.testing.BasicGraphQlTests;
import com.netflix.graphql.dgs.client.GraphqlSSESubscriptionGraphQLClient;
import com.netflix.graphql.dgs.client.MonoGraphQLClient;
import com.netflix.graphql.dgs.client.ReactiveGraphQLClient;
import com.netflix.graphql.dgs.client.RestClientGraphQLClient;
import com.netflix.graphql.dgs.client.SSESubscriptionGraphQLClient;
import com.netflix.graphql.dgs.client.WebClientGraphQLClient;
import com.netflix.graphql.dgs.client.WebSocketGraphQLClient;

import graphql.ErrorType;
import reactor.test.StepVerifier;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("no-security")
@TestInstance(Lifecycle.PER_CLASS)
class GraphqlDgsIntegrationTests extends BasicGraphQlTests {

	@Value("http://localhost:${local.server.port}")
	String baseHttpPath;

	@Value("ws://localhost:${local.server.port}/subscriptions")
	String baseWsPath;

	// blocking HTTP client
	RestClientGraphQLClient restClientGraphQLClient;

	// reactive HTTP client
	WebClientGraphQLClient webGraphQLClient;

	/**
	 * This reactive client {@link WebSocketGraphQLClient} uses deprecated
	 * subscription-transport-ws protocol
	 * 
	 * @see <a href=
	 *      "https://github.com/Netflix/dgs-framework/blob/master/graphql-dgs-client/src/main/kotlin/com/netflix/graphql/dgs/client/WebSocketGraphQLClient.kt">WebSocketGraphQLClient.kt</a>
	 * 
	 */
	WebSocketGraphQLClient webSocketGraphQLClient;

	// SSE client that uses deprecated subscriptions-transport-sse protocol
	SSESubscriptionGraphQLClient sseSubscriptionGraphQLClient;

	// SSE client that uses for new graphql-sse protocol
	GraphqlSSESubscriptionGraphQLClient graphqlSSESubscriptionGraphQLClient;

	@BeforeAll
	public void setup(@Autowired RestClient.Builder restClientBuilder) {
		RestClient restClient = restClientBuilder.baseUrl(baseHttpPath + "/graphql").build();
		this.restClientGraphQLClient = new RestClientGraphQLClient(restClient);

		this.webGraphQLClient = MonoGraphQLClient.createWithWebClient(WebClient.create(baseHttpPath + "/graphql"));

		this.webSocketGraphQLClient = new WebSocketGraphQLClient(baseWsPath, new ReactorNettyWebSocketClient());

		this.sseSubscriptionGraphQLClient = new SSESubscriptionGraphQLClient("/subscriptions",
				WebClient.create(baseHttpPath));

		this.graphqlSSESubscriptionGraphQLClient = new GraphqlSSESubscriptionGraphQLClient("/subscriptions",
				WebClient.create(baseHttpPath));
	}

	@SuppressWarnings({ "unchecked" })
	@Test
	void test_query_all_posts() {

		var postsTitles = this.webGraphQLClient
				.reactiveExecuteQuery(getAllPostsQuery())
					.map(response -> response.extractValueAsObject(JSON_PATH_ALL_POST_TITLES, List.class))
					.block();
		validatePostTitles(postsTitles);
	}

	@SuppressWarnings({ "unchecked" })
	@Test
	void test_query_all_posts_blocking() {

		var postsTitles = this.restClientGraphQLClient
				.executeQuery(getAllPostsQuery())
					.extractValueAsObject(JSON_PATH_ALL_POST_TITLES, List.class);
		validatePostTitles(postsTitles);
	}

	@Test
	void test_query_get_post() {

		var postName = this.webGraphQLClient
				.reactiveExecuteQuery(getPostByIdQuery("1"))
					.map(response -> response.extractValueAsObject(JSON_PATH_GET_POST_TITLE, String.class))
					.block();
		validatePostTitle(postName);
	}

	@Test
	void test_query_get_post_blocking() {

		var postName = this.restClientGraphQLClient
				.executeQuery(getPostByIdQuery("1"))
					.extractValueAsObject(JSON_PATH_GET_POST_TITLE, String.class);
		validatePostTitle(postName);
	}

	@Test
	void test_query_get_post_author() {

		var authorName = this.webGraphQLClient
				.reactiveExecuteQuery(getPostByIdQuery("1"))
					.map(response -> response.extractValueAsObject(JSON_PATH_GET_POST_AUTHOR_NAME, String.class))
					.block();
		validateAuthorName(authorName);
	}

	@Test
	void test_query_get_post_author_blocking() {

		var authorName = this.restClientGraphQLClient
				.executeQuery(getPostByIdQuery("1"))
					.extractValueAsObject(JSON_PATH_GET_POST_AUTHOR_NAME, String.class);
		validateAuthorName(authorName);
	}

	@Test
	void test_create_post() {

		PostInput postInput = getNewPost();
		var post = this.webGraphQLClient
				.reactiveExecuteQuery(getCreatePostMutation(postInput))
					.map(response -> response.extractValueAsObject(JSON_PATH_CREATE_POST, Post.class))
					.block();

		validateNewPost(post);
	}

	@Test
	void test_create_post_blocking() {

		PostInput postInput = getNewPost();
		var post = this.restClientGraphQLClient
				.executeQuery(getCreatePostMutation(postInput))
					.extractValueAsObject(JSON_PATH_CREATE_POST, Post.class);

		validateNewPost(post);
	}

	@Test
	void test_query_error() {

		var errors = this.webGraphQLClient.reactiveExecuteQuery(BAD_QUERY).block().getErrors();

		assertThat(errors).hasSize(1);
		assertThat(errors.getFirst().getMessage().startsWith("Validation error"));
		assertThat(errors.getFirst().getExtensions().getClassification().equals(ErrorType.ValidationError));
	}

	@Test
	void test_query_error_blocking() {

		var errors = this.restClientGraphQLClient.executeQuery(BAD_QUERY).getErrors();

		assertThat(errors).hasSize(1);
		assertThat(errors.getFirst().getMessage().startsWith("Validation error"));
		assertThat(errors.getFirst().getExtensions().getClassification().equals(ErrorType.ValidationError));
	}

	@Test
	void test_subscription_ws() {

		var randomPost = this.webSocketGraphQLClient
				.reactiveExecuteQuery(getRandomPostSubscription(), Collections.emptyMap())
					.map(response -> response.extractValueAsObject(JSON_PATH_RANDOM_POST, Post.class));

		StepVerifier
				.create(randomPost)
					.consumeNextWith(post -> assertThat(post.getTitle()).isNotEmpty())
					.consumeNextWith(post -> assertThat(post.getTitle()).isNotEmpty())
					.consumeNextWith(post -> assertThat(post.getTitle()).isNotEmpty())
					.thenCancel()
					.verify();
	}

	/**
	 * Tests SSE subscriptions using different protocols. Enable
	 * graphql-dgs-subscriptions-sse-autoconfigure dependency to work.
	 */
//	@ParameterizedTest
//	@MethodSource("sseSubscriptionClientProvider")
	void test_subscription_sse_subscriptions_transport_sse(ReactiveGraphQLClient sseSubscriptionClient) {

		var randomPost = sseSubscriptionClient
				.reactiveExecuteQuery(getRandomPostSubscription(), Collections.emptyMap())
					.map(response -> response.extractValueAsObject(JSON_PATH_RANDOM_POST, Post.class));

		StepVerifier
				.create(randomPost)
					.consumeNextWith(post -> assertThat(post.getTitle()).isNotEmpty())
					.consumeNextWith(post -> assertThat(post.getTitle()).isNotEmpty())
					.consumeNextWith(post -> assertThat(post.getTitle()).isNotEmpty())
					.thenCancel()
					.verify();
	}

	Stream<ReactiveGraphQLClient> sseSubscriptionClientProvider() {
		return Stream.of(this.sseSubscriptionGraphQLClient, this.graphqlSSESubscriptionGraphQLClient);
	}
}
