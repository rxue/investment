# Chapter 2
## Item 6: Avoid creating unnecessary objects
* avoid by using *static factory methods*, e.g. `Boolean.valueOf(String)` instead of `new Boolean`
* in the code example, `String.matches` is not suitable when this is called repeatedly over thousands of times or even more since the `matches` method creates `new Pattern` each time it is called. I made this mistake and fixed it in this commit: https://github.com/rxue/investment/issues/25
