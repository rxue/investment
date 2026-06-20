# Practical Tips
## add of `spring-boot-starter-data-jpa` makes database configuration a must
That is to say, app will not start up successfully until database connection is configured
## `jackson` is a *transitive* dependency of Spring
## Yahoo Finance has *crumb* in session for querying the market quote
### Tips about Yahoo Finance RESTful API endpoint
A *crumb* has limit times of request from the endpoint, so in order to always getting response, each time get a new *crumb* to avoid request being banned due to *too many requests*## Tips about Applying AOP
### persist JPA entity by means of AOP
AOP in Spring is done by *proxy*, so in case of persisting an entity by using AOP, the returned entity is *proxy*, which is different from the *persisted state entity*. So using AOP to persist entities might not be a good practice at all

