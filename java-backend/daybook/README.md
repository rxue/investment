# Core Java Vol. II
## Chapter 4. Networking
### 4.4 The Http Client
#### 4.4.1 `The HttpClient` Class
> This is a common pattern for constructing *immutable objects*.

Comment: the *immutable objects* is thread-safe

# Practical Tips
## `BigDecimal` over `double` in case of money value calculation, e.g. the foreign exchange rate
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
