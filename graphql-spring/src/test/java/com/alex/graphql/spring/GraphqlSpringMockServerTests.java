package com.alex.graphql.spring;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureHttpGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.graphql.test.tester.HttpGraphQlTester;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;
import org.springframework.web.context.WebApplicationContext;

/**
 * Testing in MOCK web environment.
 */
@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
@AutoConfigureHttpGraphQlTester
class GraphqlSpringMockServerTests extends BasicSpringGraphQlTests {

	@Autowired
	HttpGraphQlTester httpGraphQlTester;

	/**
	 * Example on how to manually set up the tester.
	 * 
	 * @param context
	 */
//	@BeforeAll
	void setUp(@Autowired WebApplicationContext context) {

		WebTestClient client = MockMvcWebTestClient
				.bindToApplicationContext(context)
					.configureClient()
					.baseUrl("/graphql")
					.build();
		this.httpGraphQlTester = HttpGraphQlTester.create(client);
	}

	@Test
	void test_query_all_posts() {
		super.test_query_all_posts(this.httpGraphQlTester);
	}

	@Test
	void test_query_get_post() {
		super.test_query_get_post(this.httpGraphQlTester);
	}

	@Test
	void test_query_get_post_author() {
		super.test_query_get_post_author(this.httpGraphQlTester);
	}

	@Test
	void test_create_post() {
		super.test_create_post(this.httpGraphQlTester);
	}

	@Test
	void test_query_error() {
		super.test_query_error(this.httpGraphQlTester);
	}

	@Test
	void test_subscriptions_sse_failing() {
		super.test_subscriptions_sse_failing(this.httpGraphQlTester);
	}
}