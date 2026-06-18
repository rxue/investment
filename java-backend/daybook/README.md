# Practical Tips
## add of `spring-boot-starter-data-jpa` makes database configuration a must
That is to say, app will not start up successfully until database connection is configured
## `jackson` is a *transitive* dependency of Spring
## Yahoo Finance has *crumb* in session for querying the market quote
### Tips about Yahoo Finance RESTful API endpoint
A *crumb* has limit times of request from the endpoint, so in order to always getting response, each time get a new *crumb* to avoid request being banned due to *too many requests*
