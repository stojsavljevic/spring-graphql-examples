# Netflix Domain Graph Service (DGS) Framework

## URLs

* [Apollo client that uses graphql-transport-ws protocol](http://localhost:8080/apollo/index.html)
* [Integrated GraphiQL](http://localhost:8080/graphiql)

## Issues

* `@DgsData` methods (field resolvers) can't be used with `@Secured("ROLE_USER")` because authentication is not found when using WebSocket subscriptions. Probably related to this [GitHub Issue](https://github.com/Netflix/dgs-framework/issues/1294) or this [GitHub Issue](https://github.com/Netflix/dgs-framework/issues/458).
* No support for java records as inputs: [GitHub Issue](https://github.com/Netflix/dgs-framework/issues/1138)
* SSE subscriptions currently don't work with DGS Spring GraphQL Integration: [Official Documentation](https://netflix.github.io/dgs/spring-graphql-integration/#known-gaps-and-limitations)
    - ~~SSE and WebSocket subscriptions don't work at the same time.~~
    - ~~SSE don't work on Reactive stack.~~

## WS Subscriptions

If using security, add following as headers in GraphiQL: `{"Authorization":"admin:admin"}`

## SSE Subscriptions - CURRENTLY DON'T WORK

* Enable subscriptions related dependency in `pom.xml`: `graphql-dgs-subscriptions-sse-autoconfigure` and disable `graphql-dgs-subscriptions-websockets-autoconfigure`
* Use Postman or `curl`. Example `curl` request:

```
curl --location --request POST 'http://localhost:8080/subscriptions' \
--header 'Accept: application/json' \
--header 'Accept: text/event-stream' \
--header 'Cookie: JSESSIONID=F022D2476711DD88FA5910D822E1D876' \
--header 'Content-Type: application/json' \
--data-raw '{"query":"subscription {\n  randomPost {\n    title\n    content\n    releaseYear\n    author {\n      name\n    }\n  }\n}","variables":{}}'
```

## Reactive
* Enable Reactive stack by commenting out `spring-boot-starter-web`
* Reactive security is not implemented so application has to be run with `no-security` profile.
