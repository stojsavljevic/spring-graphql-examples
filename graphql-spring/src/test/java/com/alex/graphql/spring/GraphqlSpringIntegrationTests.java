package com.alex.graphql.spring;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.graphql.client.DgsGraphQlClient;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.graphql.client.WebGraphQlClient;
import org.springframework.graphql.client.WebSocketGraphQlClient;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;

import com.alex.graphql.core.generated.DgsConstants;
import com.alex.graphql.core.generated.client.AllPostsGraphQLQuery;
import com.alex.graphql.core.generated.client.AllPostsProjectionRoot;
import com.alex.graphql.core.generated.client.RandomPostGraphQLQuery;
import com.alex.graphql.core.model.Post;
import com.alex.graphql.core.testing.BasicGraphQlTests;

import graphql.ErrorType;
import reactor.test.StepVerifier;

/**
 * Integration tests.
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("no-security")
class GraphqlSpringIntegrationTests extends BasicGraphQlTests {

	@Value("http://localhost:${local.server.port}${spring.graphql.path:/graphql}")
	String baseHttpPath;

	@Value("ws://localhost:${local.server.port}${spring.graphql.websocket.path:/subscriptions}")
	String baseWebSocketPath;

	HttpGraphQlClient httpGraphQlClient;

	WebSocketGraphQlClient webSocketGraphQlClient;

	DgsGraphQlClient dgsGraphQlClient;

	@BeforeAll
	void setUp() {
		this.httpGraphQlClient = HttpGraphQlClient.builder(WebClient.create(baseHttpPath)).build();

		this.webSocketGraphQlClient = WebSocketGraphQlClient
				.builder(baseWebSocketPath, new ReactorNettyWebSocketClient())
					.build();

		// The DgsGraphQlClient can be wrapped around either the HttpGraphQlClient or
		// the WebSocketGraphQlClient, with the transport method depending on the
		// wrapped client
		this.dgsGraphQlClient = DgsGraphQlClient.create(this.httpGraphQlClient);
	}
	
	Stream<WebGraphQlClient> graphQlClientProvider() {
		return Stream.of(this.httpGraphQlClient, this.webSocketGraphQlClient);
	}

	@ParameterizedTest
	@MethodSource("graphQlClientProvider")
	void test_query_all_posts(WebGraphQlClient client) {

		var posts = client
				.document(getAllPostsQuery())
					.retrieve(DgsConstants.QUERY.AllPosts)
					.toEntityList(Post.class)
					.block();
		validatePostTitles(posts.stream().map(post -> post.getTitle()).toList());
	}

	@Test
	void test_query_all_posts_dgs_client() {

		var posts = this.dgsGraphQlClient
				.request(AllPostsGraphQLQuery.newRequest().build())
					.projection(new AllPostsProjectionRoot<>().title().author().name())
					.retrieveSync()
					.toEntityList(Post.class);
		validatePostTitles(posts.stream().map(post -> post.getTitle()).toList());
	}

	@ParameterizedTest
	@MethodSource("graphQlClientProvider")
	void test_query_get_post(WebGraphQlClient client) {

		var post = client
				.document(getPostByIdQuery("1"))
					.retrieve(DgsConstants.QUERY.PostById)
					.toEntity(Post.class)
					.block();
		validatePostTitle(post.getTitle());
	}

	@ParameterizedTest
	@MethodSource("graphQlClientProvider")
	void test_query_get_post_author(WebGraphQlClient client) {

		var post = client
				.document(getPostByIdQuery("1"))
					.retrieve(DgsConstants.QUERY.PostById)
					.toEntity(Post.class)
					.block();
		validateAuthorName(post.getAuthor().getName());
	}

	@ParameterizedTest
	@MethodSource("graphQlClientProvider")
	void test_create_post(WebGraphQlClient client) {

		var post = client
				.document(getCreatePostMutation(getNewPost()))
					.retrieve(DgsConstants.MUTATION.CreatePost)
					.toEntity(Post.class)
					.block();
		validateNewPost(post);
	}

	@ParameterizedTest
	@MethodSource("graphQlClientProvider")
	void test_query_error(WebGraphQlClient client) {

		var errors = client.document(BAD_QUERY).execute().block().getErrors();

		assertThat(errors).hasSize(1);
		assertThat(errors.getFirst().getMessage().startsWith("Validation error"));
		assertThat(errors.getFirst().getErrorType().equals(ErrorType.ValidationError));
		assertThat(errors.getFirst().getLocations().get(0).getLine() == 3);
	}

	@ParameterizedTest
	@MethodSource("graphQlClientProvider")
	void test_subscriptions(WebGraphQlClient client) {

		var subscriptionPost = client
				.document(getRandomPostSubscription())
					.retrieveSubscription(DgsConstants.SUBSCRIPTION.RandomPost)
					.toEntity(Post.class);

		StepVerifier
				.create(subscriptionPost)
					.assertNext(post -> assertThat(post.getTitle()).isNotEmpty())
					.assertNext(post -> assertThat(post.getTitle()).isNotEmpty())
					.assertNext(post -> assertThat(post.getTitle()).isNotEmpty())
					.thenCancel()
					.verify();
	}

	@Test
	void test_subscriptions_dgs_client() {

		var subscriptionPost = this.dgsGraphQlClient
				.request(RandomPostGraphQLQuery.newRequest().build())
					.projection(new AllPostsProjectionRoot<>().title().author().name())
					.retrieveSubscription()
					.toEntity(Post.class);

		StepVerifier
				.create(subscriptionPost)
					.assertNext(post -> assertThat(post.getTitle()).isNotEmpty())
					.assertNext(post -> assertThat(post.getTitle()).isNotEmpty())
					.assertNext(post -> assertThat(post.getTitle()).isNotEmpty())
					.thenCancel()
					.verify();
	}
}