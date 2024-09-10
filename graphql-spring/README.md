# Spring for GraphQL

## URLs

* [Apollo client that uses graphql-transport-ws protocol](http://localhost:8080/apollo/index.html)
* [Integrated GraphiQL](http://localhost:8080/graphiql)

## Issues
* When using Servlet stack, after closing a client for SSE subscriptions - the application keeps fetching data and throws the exception every time subscription object was supposed to be emitted:

```
org.springframework.web.context.request.async.AsyncRequestNotUsableException: Response not usable after response errors.
```

* When testing SSE subscriptions with `HttpGraphQlClient`, after testing ends, exceptions are thrown:

```
java.lang.IllegalStateException: The request associated with the AsyncContext has already completed processing.
```

* Testing SSE subscriptions using `HttpGraphQlTester` is not supported:

```
java.lang.UnsupportedOperationException: Subscriptions not supported over HTTP
```


## WS Subscriptions

If using security, add following as headers in GraphiQL: `{"Authorization":"admin:admin"}`


## SSE Subscriptions

* Use Postman or `curl`. Example `curl` request:

```
curl --location --request POST 'http://localhost:8080/graphql' \
--header 'Accept: text/event-stream' \
--header 'Cookie: JSESSIONID=C60A92B851B60DEDF8510395A3BDE7F5' \
--header 'Content-Type: application/json' \
--data-raw '{"query":"subscription {\n  randomPost {\n    title\n    content\n    releaseYear\n    author {\n      name\n    }\n  }\n}","variables":{}}'
```

## Reactive
* Enable Reactive stack by commenting out `spring-boot-starter-web`
* Reactive security is not implemented so application has to be run with `no-security` profile.