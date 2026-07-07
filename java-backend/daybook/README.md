# Practical Tips
## Java SE
Since Java SE 8, there is a `UncheckedIOException` added to `java.io`, to which `IOException` can be propogated
### Use of `record` (20260628)
`record` cannot `extends` a class but can `implements` *interface*

## add of `spring-boot-starter-data-jpa` makes database configuration a must
That is to say, app will not start up successfully until database connection is configured
## `jackson` is a *transitive* dependency of Spring
## Yahoo Finance has *crumb* in session for querying the market quote
### Tips about Yahoo Finance RESTful API endpoint
A *crumb* has limit times of request from the endpoint, so in order to always getting response, each time get a new *crumb* to avoid request being banned due to *too many request*
## Tips about Applying AOP
### persist JPA entity by means of AOP
AOP in Spring is done by *proxy*, so in case of persisting an entity by using AOP, the returned entity is *proxy*, which is different from the *persisted state entity*. So using AOP to persist entities might not be a good practice at all
## Design Tips
### [Command Query Separation](https://martinfowler.com/bliki/CommandQuerySeparation.html)
### Redesign on the investment portfolio calculation
There are 2 ways to run the app:
* local run with non-interactive CLI without database
* run with Spring framework with database

database, frameworks and CLI are the *details* in the architecture
#### Modeling
Each calculation is as per a portfolio => As to each csv upload, there should be a corresponding portfolio with a *portfoio ID*, which be either automatically generated or given explicitly.
* When running locally, input is directly in a directory or a single file => no *portfolio ID* is needed
* When running with Spring framework, it can either run by
  * getting input from the request without a database, in this case no *portfolio ID* is needed
  * getting input from the database, in this case *portfolio ID* is need to distinguish itself from other portfolios
