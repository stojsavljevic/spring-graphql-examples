package com.alex.graphql.spring.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;

import com.alex.graphql.core.data.DataHandler;
import com.alex.graphql.core.generated.DgsConstants;
import com.alex.graphql.core.model.Author;
import com.alex.graphql.core.model.Post;
import com.alex.graphql.core.model.PostInput;

import reactor.core.publisher.Flux;

@Controller
@Secured("ROLE_USER")
public class SpringController {

	Logger logger = LoggerFactory.getLogger(SpringController.class);

	DataHandler dataHandler = new DataHandler();

	@QueryMapping(DgsConstants.QUERY.AllPosts)
	public List<Post> allPosts() {

		return this.dataHandler.getAllPosts();
	}

	@QueryMapping(DgsConstants.QUERY.PostById)
	public Post postById(@Argument(DgsConstants.QUERY.POSTBYID_INPUT_ARGUMENT.Id) String postId) {

		return this.dataHandler.getPostById(postId);
	}

	@MutationMapping(DgsConstants.MUTATION.CreatePost)
	public Post createPost(@Argument(DgsConstants.MUTATION.CREATEPOST_INPUT_ARGUMENT.PostInput) PostInput postInput) {

		return this.dataHandler.createPost(postInput);
	}

	@SubscriptionMapping(DgsConstants.SUBSCRIPTION.RandomPost)
	public Flux<Post> randomPost() {

		return dataHandler.getRandomPostPublisher();
	}

	@SchemaMapping(typeName = DgsConstants.POST.TYPE_NAME, field = DgsConstants.POST.Author)
	public Author author(Post post) {

		return this.dataHandler.getAuthorById(post.getAuthorId());
	}

}
