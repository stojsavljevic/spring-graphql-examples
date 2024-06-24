package com.alex.graphql.dgs.fetchers;

import java.util.List;

import org.reactivestreams.Publisher;
import org.springframework.security.access.annotation.Secured;

import com.alex.graphql.core.data.DataHandler;
import com.alex.graphql.core.generated.DgsConstants;
import com.alex.graphql.core.model.Author;
import com.alex.graphql.core.model.Post;
import com.alex.graphql.core.model.PostInput;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsData;
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.DgsSubscription;
import com.netflix.graphql.dgs.InputArgument;

@DgsComponent
public class DgsDatafetcher {

	DataHandler dataHandler = new DataHandler();

	@DgsQuery(field = DgsConstants.QUERY.AllPosts)
	@Secured("ROLE_USER")
	public List<Post> allPosts() {

		return dataHandler.getAllPosts();
	}

	@DgsQuery(field = DgsConstants.QUERY.PostById)
	@Secured("ROLE_USER")
	public Post postById(@InputArgument(DgsConstants.QUERY.POSTBYID_INPUT_ARGUMENT.Id) String id) {

		return dataHandler.getPostById(id);
	}

	@DgsMutation(field = DgsConstants.MUTATION.CreatePost)
	@Secured("ROLE_USER")
	public Post createPost(
			@InputArgument(DgsConstants.MUTATION.CREATEPOST_INPUT_ARGUMENT.PostInput) PostInput postInput) {

		return dataHandler.createPost(postInput);
	}

	@DgsSubscription(field = DgsConstants.SUBSCRIPTION.RandomPost)
	@Secured("ROLE_USER")
	public Publisher<Post> randomPost() {

		return dataHandler.getRandomPostPublisher();
	}

	// security annotation here doesn't work for subscriptions 
	// https://github.com/Netflix/dgs-framework/issues/1294
	// https://github.com/Netflix/dgs-framework/issues/458
	// @Secured("ROLE_USER")
	@DgsData(parentType = DgsConstants.POST.TYPE_NAME, field = DgsConstants.POST.Author)
	public Author author(DgsDataFetchingEnvironment dfe) {

		Post post = dfe.getSource();
		return dataHandler.getAuthorById(post.getAuthorId());
	}

}
