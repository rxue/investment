# Financial Statement
## Income Statement
* financial income is the gross income before operating expense deduction

# Return (part of CFA)
## Time-Weighted Rate of Return
### CFA Training tutorial from Undamy:
#### 3 step-process of calculation
1. pricing the portfolio before any specific additions or withdrawals occur, meaning the overall investment horizon is broken to smaller evaluation sub-period
2. calculate the holding period return (HPR) for each sub-period
3. link the HPR by *compunding* them to obtain  the annual rate of rate

#### Example
##### My own question: How does the given example differ from the real world problem, e.g. when calculating the TWR of my own portfolio
Answer from Claude

1. Cash flows don't land conveniently at valuation dates

In the problem, the new share purchase happens exactly at year-end, so there's a clean, obvious break point. In your real portfolio, deposits/withdrawals/trades happen on random days throughout the year. True TWR requires you to **revalue the portfolio and start a new sub-period every single time there's an external cash flow** — which could be daily, or even multiple times a day. This is why **real-world TWR (as brokerages/GIPS-compliant firms do it) is usually computed with daily valuation**, not annual.

2. The dividend-treatment nuance is different in practice

This is the big one, and it connects to your last question. In the textbook problem, "does not reinvest" implicitly means the $10/share is paid out of the account — an external cash outflow — which is why it's counted in Period 1's return and then disappears from Period 2's base.

In your real *brokerage account*, if a dividend is paid in cash and you don't reinvest it, it usually just sits in the account as uninvested cash — it hasn't left the portfolio. That cash is still part of your portfolio value and must be included in the next valuation, just earning ~0% (or a money-market rate) instead of the stock's return. It only becomes an external cash flow (and drops out of the base) if you actually withdraw it from the account. So the treatment depends on whether the cash stayed in the account or not — this is a real distinction you'd need to check.

3. No clean, single-security valuation

You likely hold multiple securities. Each valuation point requires the total portfolio market value (sum of all positions + cash), not just one stock's price. You also need to handle:
- Multiple, overlapping cash flows (buys/sells of different securities on different days)
- Corporate actions (splits, spin-offs, mergers)
- Accrued-but-unpaid dividends/interest

4. Fees, taxes, and costs

The textbook example is frictionless. Your real TWR should specify gross-of-fees or net-of-fees — advisory fees, expense ratios, transaction costs, and taxes all affect what return you actually experienced, and industry standard (GIPS) usually reports both.

5. You usually don't have exact valuations at every cash-flow date

True daily-valuation TWR requires knowing your exact portfolio value right before every single flow — which brokerages can compute internally (they know intraday), but you as an individual often only have periodic statements (monthly, say). In that case, people use an approximation like the *Modified Dietz method*, which weights each cash flow by the fraction of the period it was invested, rather than breaking into a true sub-period at every flow. It converges to true TWR but isn't identical.




# Benchmark
## Question: How to benchmark my own portfolio again an index or an ETF such as VOO
### Essence
The market value plots of my portfolio can be calculated, whereas ETF has price plot directly. They are different things.
