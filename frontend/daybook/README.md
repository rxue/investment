# Javascript Reference
## Standard built-in objects
### [`Object`](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Object)
# Practical Problems Encountered
## 20261030
### When running both backend and frontend in local, frontend fails to get response through the Spring Boot backend.
**Error Cause**: [*CORS*](https://developer.mozilla.org/en-US/docs/Web/HTTP/Guides/CORS)
## 20261123
### Questions
### Question left: when using `lightweight-chart` to render the stock price/return curves, the last statement inside the `useEffect` is `return () => chart.remove();`, I don't fully understand it yet
### What is the mechanism of `npm run dev`
`dev` is a script defined in `package.json`
### Meaning the *mount* in React context
*mount* - get added to the screen

reference: https://react.dev/reference/react/Component
### Problem: I was confused on how to test the React app together with the RESTful API in the dev
cause: I was confused on the server proxy config in vite.config.js

So note that
> Unless noted, the options in this section are only applied to dev
official reference: https://vite.dev/config/server-options

Solution: config only in vite.config.js is enough
