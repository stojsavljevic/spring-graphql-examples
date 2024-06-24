package com.alex.graphql.spring;

import java.net.URI;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.graphql.test.tester.HttpGraphQlTester;
import org.springframework.graphql.test.tester.WebSocketGraphQlTester;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;

/**
 * Testing against a live server.
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class GraphqlSpringLiveServerTests extends BasicSpringGraphQlTests {

	@Value("ws://localhost:${local.server.port}${spring.graphql.websocket.path}")
	String baseWebSocketPath;

	@Autowired
	HttpGraphQlTester httpGraphQlTester;

	WebSocketGraphQlTester webSocketGraphQlTester;

	@BeforeAll
	void setUp(@Value("http://localhost:${local.server.port}${spring.graphql.path:/graphql}") String baseHttpPath) {

		this.webSocketGraphQlTester = WebSocketGraphQlTester
				.builder(URI.create(this.baseWebSocketPath), new ReactorNettyWebSocketClient())
					.build();

		// Example on how to manually set up HTTP tester:
		// Builder httpBuilder = WebTestClient.bindToServer().baseUrl(baseHttpPath);
		// this.httpGraphQlTester = HttpGraphQlTester.builder(httpBuilder).build();
	}

	Stream<GraphQlTester> graphQlTesterProvider() {
		return Stream.of(this.httpGraphQlTester, this.webSocketGraphQlTester);
	}

	@ParameterizedTest
	@MethodSource("graphQlTesterProvider")
	void test_query_all_posts(GraphQlTester tester) {
		super.test_query_all_posts(tester);
	}

	@ParameterizedTest
	@MethodSource("graphQlTesterProvider")
	void test_query_get_post(GraphQlTester tester) {
		super.test_query_get_post(tester);
	}

	@ParameterizedTest
	@MethodSource("graphQlTesterProvider")
	void test_query_get_post_author(GraphQlTester tester) {
		super.test_query_get_post_author(tester);
	}

	@ParameterizedTest
	@MethodSource("graphQlTesterProvider")
	void test_create_post(GraphQlTester tester) {
		super.test_create_post(tester);
	}

	@ParameterizedTest
	@MethodSource("graphQlTesterProvider")
	void test_query_error(GraphQlTester tester) {
		super.test_query_error(tester);
	}

	@Test
	void test_subscriptions() {
		super.test_subscriptions(this.webSocketGraphQlTester);
	}

	@Test
	void test_subscriptions_sse_failing() {
		super.test_subscriptions_sse_failing(this.httpGraphQlTester);
	}
}