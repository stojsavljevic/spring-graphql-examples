# Spring for GraphQL

## URLs

* [Apollo client that uses graphql-transport-ws protocol](http://localhost:8080/apollo/index.html)
* [Integrated GraphiQL](http://localhost:8080/graphiql)

## Issues

* Testing SSE subscriptions using `HttpGraphQlTester` is not supported:

```
java.lang.UnsupportedOperationException: Subscriptions not supported over HTTP
```


## WS Subscriptions

WS subscriptions can be tested in Apollo client or in integrated GraphiQL. If using security, add following as a header in GraphiQL: `{"Authorization":"admin:admin"}`


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