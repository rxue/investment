# Core Java Vol. II
## Chapter 4. Networking
### 4.4 The Http Client
#### 4.4.1 `The HttpClient` Class
> This is a common pattern for constructing *immutable objects*.

Comment: the *immutable objects* is thread-safe

# Practical Tips
## Mistakes
### 20261118
When trying to form the fx rate array, I need to use `ChronoUnit.DAYS.between` to know the amount of days between the period, but got problem since I mistakenly consider the 2nd end date argument as inclusive.
The cause of this mistake is I did not know the *half-open interval* design principle

### 20261119
#### misuse of `record`
I added an array field to `record`, which is against the original intention of the `record`

```
public record DateIndex(int firstEpochDay, BigDecimal[] values) {
    BigDecimal get(LocalDate date) {
        throw new UnsupportedOperationException();
    }
}
```
#### lack of understanding on *value object*
reference to read: IDDD > Chapter 6 Value Characteristics > Value Equality

## Java
### `BigDecimal` over `double` in case of money value calculation, e.g. the foreign exchange rate
Reasoning from *Claude Code*:
```
Why BigDecimal wins for an FX rate:

- Precision/rounding correctness: double is binary floating point, so decimal values like exchange rates (e.g. 1.0866) often can't be represented exactly — you get tiny representation errors (1.0865999999999998...). That's usually invisible in isolation but compounds when the rate is multiplied against a price and rounded to cents; you can end up off by a cent in a way that's hard to reproduce/debug because it depends on the exact binary representation.
- Multiplication with money: The natural next step after fetching an FX rate is price.multiply(rate) to convert currencies. Price already stores money as long centValue/BigDecimal-derived; multiplying a long/BigDecimal by a double forces an awkward conversion (BigDecimal.valueOf(double)) and reintroduces the same precision risk at the boundary. Keeping the rate as BigDecimal lets that multiplication stay exact and controlled (explicit MathContext/RoundingMode, as YahooFinanceFetcher.toCentValue does).
- Auditability: financial/accounting code generally wants deterministic, exact decimal arithmetic with explicit rounding rules — that's the whole reason BigDecimal/MathContext were imported in FxRateFetcher.java in the first place (they're now unused dead imports, which is itself a leftover clue the type was originally BigDecimal and got changed to double).
- Jackson support is equally easy either way: JsonNode.decimalValue() gives you a BigDecimal just as directly as .doubleValue() gives a double, so there's no real convenience gain from double.

When double would be fine: if this rate were only ever used for display/estimation (e.g., a rough dashboard tooltip) and never fed into a monetary calculation, double performance/simplicity might be an acceptable tradeoff. But given FxRate sits in the vo package next to Price/Percentage and is clearly meant to convert monetary amounts between currencies, that's not the case here.

Recommendation: revert the return type of fetchFXRateFromEuro (and FxRate.value) to BigDecimal, matching the codebase's existing convention, and drop the now-genuinely-needed MathContext/BigDecimal imports back into real use (e.g., observations.path(...).get(0).decimalValue()).
```
Collectors.toMap and toUnmodifiableMap does not accept neither null key nor null value input
## OAuth2
I asked: is OAuth2 3rd-party authentication?
Answer: no, it is not 3rd-party authentication but 3rd-party authorization (delegated access)
## 20261112 
* *upstream provider* for the quote fetch is Yahoo Finance RESTful API
## Relevant learning point on https://github.com/rxue/investment-utility/issues/39
## Yahoo Finance API 20261117
The timestamps of stock price series is the open time in eposecond, i.e. UTC, of the corresponding stock exchange, then the question is: on base of a given `LocalDate` how to get the timestamp in the `DataSet.Result` so that price is retrieved
