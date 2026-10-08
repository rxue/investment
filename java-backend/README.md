# CLI
## `metrics` command
### Example command: `mvn -q -pl cli -am install -DskipTests && mvn -q -pl cli exec:java   -Dexec.mainClass=io.github.rxue.investment.cli.Main   -Dexec.args="metrics LATEST_PRICE BRSL --sort-by LATEST_PRICE"`
# RESTful API
example request: `http://localhost:8080/marketquotes/ELISA.HE?metrics=LATEST_MARKET_PRICE,REGULAR_MARKET_CHANGE_PERCENT,DIVIDEND_YIELD,DIVIDEND_PAYOUT_RATIO` 
