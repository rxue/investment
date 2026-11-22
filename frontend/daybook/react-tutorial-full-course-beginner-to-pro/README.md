# Lesson 2: *Components*, *Props*, Start the Charbot
## [*Component*](https://www.youtube.com/watch?v=TtPXvEcE11E&t=2675s) - a piece of the web-site
### demo: create the first component
#### [use `function` to create the component](https://www.youtube.com/watch?v=TtPXvEcE11E&t=2727s)
#### Insert component
- [`{}`](https://www.youtube.com/watch?v=TtPXvEcE11E&t=2970s) to insert component
- [Equivalent React syntax to insert component](https://www.youtube.com/watch?v=TtPXvEcE11E&t=3051s)
    - Main idea of React: make your own HTML element
##### [*Fragment*](https://www.youtube.com/watch?v=TtPXvEcE11E&t=3240s) to avoid insert extra `div`
##### [*Props*](https://www.youtube.com/watch?v=TtPXvEcE11E&t=4055s) in relation to *attribute*
#### Syntax short cut
* [*shortcut* for getting attribute from `props` - *destructuring*](https://www.youtube.com/watch?v=TtPXvEcE11E&t=4788s)
* [`if` inside jsx](https://www.youtube.com/watch?v=TtPXvEcE11E&t=4995s&pp=0gcJCTEAlc8ueATH)
# Lesson 3: State, Event Handlers, Chatbot Project Features

# Lesson 4: CSS with React, Hook, Finish Chatbot Project (20261122)
## [Demo: Auto Scrolling feature implementation](https://www.youtube.com/watch?v=TtPXvEcE11E&t=12876s)
### [Hooks: a key feature of React](https://www.youtube.com/watch?v=TtPXvEcE11E&t=12916s) - insert React features into our component
Typically used to implement the auto-scrolling feature of the Chatbot
#### [`useState()`](https://www.youtube.com/watch?v=TtPXvEcE11E&t=12930s)
*State* automatically updates the HTML when the data changes
#### [`useEffect()`]() run some code when the component is created or updated
**OWN TAKEAWAY**
- In the demo, the execution of the code for scrolling automatically to the bottom is triggered by the update of chat history data. `useEffect()` is used as this trigger
- practical tip: put the hooks at the top of the component, and it should not be inside anything!
- [React will run the hook function inside `useEffect` after the component creation and everytime when the component is updated](https://www.youtube.com/watch?v=TtPXvEcE11E&t=13022s)
# Lesson 5:
## [`create-vite`](https://www.youtube.com/watch?v=TtPXvEcE11E&t=14352s) help us set up a new React project
### [npx create-vite](https://www.youtube.com/watch?v=TtPXvEcE11E&t=14400s)
#### [`npm install`](https://www.youtube.com/watch?v=TtPXvEcE11E&t=14637s)
#### [project directory structure explained](https://www.youtube.com/watch?v=TtPXvEcE11E&t=14740s)
* [*.jsx*](https://www.youtube.com/watch?v=TtPXvEcE11E&t=14766s) is the React code developers need to write

**OWN COMMENT**
`npx create-vite@latest .` to initialize a new project in the current dir

# lesson 6: React Router, `git`, Start with E-commerce Project
## [Routing](https://www.youtube.com/watch?v=TtPXvEcE11E&t=36636s) - create multiple pages (20261122)
- [install `react-router`](https://www.youtube.com/watch?v=TtPXvEcE11E&t=19851s)
- [load the *rounter* when rendering the `App` component](https://www.youtube.com/watch?v=TtPXvEcE11E&t=19944s)
- [add `Routes` component to the `App`](https://www.youtube.com/watch?v=TtPXvEcE11E&t=20035s)
    - `<Route>` adds a page to our website
### [Details about routing](https://www.youtube.com/watch?v=TtPXvEcE11E&t=20308s)
### [Shortcuts with React Router](https://www.youtube.com/watch?v=TtPXvEcE11E&t=20346s)

# Lesson 7: React with Backend, Data Fetching, Load Ecommerce Project Data
## [Data Fetching (from the backend)](https://www.youtube.com/watch?v=TtPXvEcE11E&t=13513s)
### `fetch` built-in provided by Javascript
* [`fetch` returns a `Promise`](https://www.youtube.com/watch?v=TtPXvEcE11E&t=23260s)
* [`response.json()`](https://www.youtube.com/watch?v=TtPXvEcE11E&t=23385s&pp=0gcJCTEAlc8ueATH)
### [`axios` library](https://www.youtube.com/watch?v=TtPXvEcE11E&t=23568s) to make the syntax cleaner
# Lesson 10: Deploy React to the Internet, Intro to AWS
## Deploy frontend (20261031)
* [browser does not understand `.jsx` code](https://www.youtube.com/watch?v=TtPXvEcE11E&t=36337s) => need to convert these .jsx code into normal Javascript code => [`npm run build`](https://www.youtube.com/watch?v=TtPXvEcE11E&t=36371s) - shortcut for `vite build`. js code generated to `dist` directory by default
