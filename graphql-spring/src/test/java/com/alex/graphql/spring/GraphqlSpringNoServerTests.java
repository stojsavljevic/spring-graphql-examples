package com.alex.graphql.spring;

import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest;
import org.springframework.graphql.ExecutionGraphQlService;
import org.springframework.graphql.server.WebGraphQlHandler;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.graphql.test.tester.WebGraphQlTester;
import org.springframework.http.CacheControl;

import com.alex.graphql.spring.controller.SpringController;

/**
 * Server-side testing without a client.
 */
@GraphQlTest(SpringController.class)
class GraphqlSpringNoServerTests extends BasicSpringGraphQlTests {

	@Autowired
	GraphQlTester graphQlTester;

	WebGraphQlTester webGraphQlTester;

	@BeforeAll
	void setUp(@Autowired ExecutionGraphQlService executionGraphQlService) {
		WebGraphQlHandler webGraphQlHandler = WebGraphQlHandler
				.builder(executionGraphQlService)
					.interceptor((request, chain) -> {
						request.getHeaders().add("X-Test-Request", "spring-graphql-examples");
						System.out.println("WebGraphQlHandler: request");
						return chain.next(request);
					})
					.interceptor((input, next) -> next.next(input).doOnNext(response -> {
						response.getResponseHeaders().add("X-Test-Response", "spring-graphql-examples");
						System.out.println("WebGraphQlHandler: response");
					}))
					.build();
		this.webGraphQlTester = WebGraphQlTester
				.builder(webGraphQlHandler)
					.headers(headers -> headers.setCacheControl(CacheControl.noCache()))
					.build();
	}

	Stream<GraphQlTester> graphQlTesterProvider() {
		return Stream.of(this.graphQlTester, this.webGraphQlTester);
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

	@ParameterizedTest
	@MethodSource("graphQlTesterProvider")
	void test_subscriptions(GraphQlTester tester) {
		super.test_subscriptions(tester);
	}
}