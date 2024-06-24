# GraphQL support in Spring  ![build status](https://github.com/stojsavljevic/graphql-spring-examples/actions/workflows/maven.yml/badge.svg)

This project showcases two different Spring implementations of the GraphQL protocol: [Netflix DGS](https://github.com/Netflix/dgs-framework) and [Spring for GraphQL](https://github.com/spring-projects/spring-graphql). Each implementation is placed in its own module, but they share the same GraphQL schema and utilize the same generated code, following a schema-first approach. Data fetching is handled by a common class, and the security configuration is identical for both modules. Additionally, they both use the same [Apollo React](https://www.apollographql.com/docs/react) GraphQL client.

Each module is thoroughly tested.

## Modules

* `graphql-core` library with common code and resources
* `graphql-dgs` Netflix DGS module
* `graphql-spring` Spring for GraphQL module

## Security

* Every module uses form login: `admin/admin`.
* In order to disable security start components with `no-security` spring profile.
* WebSocket authentication can be done in two ways:
	- using session cookies when client is embedded in a page
	- using `Authorization` in connection payload that simulates Bearer token authentication. This is demonstrated in Apollo React clients. Note that DGS implementation doesn't support it.

## Misc

* Every component starts on `8080` port by default.
* Every component exposes the same Apollo React client on <http://localhost:8080/apollo/index.html>. Source of the client is in `graphql-core/src/main/apollo-frontend`.

## Example Queries

```
query {
  allPosts {
    title
    content
    releaseYear
    author {
      name
    }
  }
}
```

```
subscription {
  randomPost {
    title
    content
    releaseYear
    author {
      name
    }
  }
}

```

```
mutation {
  createPost(postInput: {
    title: "The Shining"
    content: "I believe that children are our future. Unless we stop them now."
    authorId: 1
  }) {
    title
    content
    releaseYear
    author {
      name
    }
  }
}
```