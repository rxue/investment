# Lesson 1: React Basics, JSX
## What React is
### React is an *external library*
[What is *external library*](https://www.youtube.com/watch?v=TtPXvEcE11E&t=480s)
### [2 external libraries of React](https://www.youtube.com/watch?v=TtPXvEcE11E&t=1000s)
#### `react`
#### [`react-dom`](https://www.youtube.com/watch?v=TtPXvEcE11E&t=1026s) - features specific to websites
## [How to use React](https://www.youtube.com/watch?v=TtPXvEcE11E&t=1075s)
## Babel
### [example React code, i.e. jsx](https://www.youtube.com/watch?v=TtPXvEcE11E&t=1585s&pp=0gcJCTIAlc8ueATH)
equivalent JavaScript code: https://www.youtube.com/watch?v=TtPXvEcE11E&t=1627s
### [What does Babel do](https://www.youtube.com/watch?v=TtPXvEcE11E&t=1627s)

## [How React makes develop wesite easier](https://www.youtube.com/watch?v=TtPXvEcE11E&t=2167s)
### [insert JavaScript code inside curly bracket](https://www.youtube.com/watch?v=TtPXvEcE11E&t=2232s)

# Lesson 2: *Components*, *Props*, Start the Charbot
## [*Component*](https://www.youtube.com/watch?v=TtPXvEcE11E&t=2675s) - a piece of the web-site
### demo: create the first component
#### [use `function` to create the component](https://www.youtube.com/watch?v=TtPXvEcE11E&t=2727s) 20261124
* [*Component* name must start with capital letter (in PascalCase) in React](https://www.youtube.com/watch?v=TtPXvEcE11E&t=2775s&pp=0gcJCTIAlc8ueATH)
* component function returns block with parenthesis! (but not explained why in the tutorial) (20261124)

#### Insert component
- [`{}`](https://www.youtube.com/watch?v=TtPXvEcE11E&t=2970s) to insert component
- [Equivalent React syntax to insert component](https://www.youtube.com/watch?v=TtPXvEcE11E&t=3051s)
    - Main idea of React: make your own HTML element
##### [*Fragment*](https://www.youtube.com/watch?v=TtPXvEcE11E&t=3240s) (`<>`) to avoid insert extra `div` (20261124)
##### [*Props*](https://www.youtube.com/watch?v=TtPXvEcE11E&t=4055s) in relation to *attribute*
* *Props*, short for *properties*, make our component reusable since it passes attributes to components

###### [*shortcut* for getting attribute from `props`](https://www.youtube.com/watch?v=TtPXvEcE11E&t=4788s)
####### a further [*destructuring* shortcut](https://www.youtube.com/watch?v=TtPXvEcE11E&t=4910s 20261124)
##### [`if` inside jsx](https://www.youtube.com/watch?v=TtPXvEcE11E&t=4995s&pp=0gcJCTEAlc8ueATH)

# Lesson 3: State, Event Handlers, Chatbot Project Features 20261125
## [Event handler](https://www.youtube.com/watch?v=TtPXvEcE11E&t=6896s) - run function when we interact with the website
simple [`onClick` event handling example](https://www.youtube.com/watch?v=TtPXvEcE11E&t=6951s)
### [details about event handler](https://www.youtube.com/watch?v=TtPXvEcE11E&t=7043s)
## [*state*](https://www.youtube.com/watch?v=TtPXvEcE11E&t=7288s)
## [How to get the text in a text box](https://www.youtube.com/watch?v=TtPXvEcE11E&t=8039s)
Best practice in React: do not use DOM manually
solution: `onChange` event from React
#### [lift state up](https://www.youtube.com/watch?v=TtPXvEcE11E&t=8559s) (component tree visualization) IMPORTANT!
##### [share the state between components with *props*](https://www.youtube.com/watch?v=TtPXvEcE11E&t=8694s)
#### [demo: remove text after clicking the send button](https://www.youtube.com/watch?v=TtPXvEcE11E&t=9024s)
##### [controlled inputs](https://www.youtube.com/watch?v=TtPXvEcE11E&t=9035s)
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
- the 1st parameter: [React will run the hook function inside `useEffect` after the component creation and everytime when the component is updated](https://www.youtube.com/watch?v=TtPXvEcE11E&t=13022s)
- the second parameter: *dependency array*, which controls when the `useEffect` run
- [best practice](https://www.youtube.com/watch?v=TtPXvEcE11E&t=13180s): Give `useEffect` a *dependency array* to avoid running too often
#### Last step of implementation: after the chat message is updated, we want to scroll the chat box to the bottom - use of [`useRef`](https://www.youtube.com/watch?v=TtPXvEcE11E&t=13180s)
- how: get the `chat-message-container` into the `useEffect`'s lambda function
- [`ref` is a container with special React features](https://www.youtube.com/watch?v=TtPXvEcE11E&t=13277s&pp=0gcJCTIAlc8ueATH)
- [**how to access the HTML elements saved inside the `ref` - `chatMessagesRef.current` inside the useEffect lambda**](https://www.youtube.com/watch?v=TtPXvEcE11E&t=13377s)
    - key part to make the scroll automatic - https://www.youtube.com/watch?v=TtPXvEcE11E&t=13460s
# Lesson 5:
## [`create-vite`](https://www.youtube.com/watch?v=TtPXvEcE11E&t=14352s) help us set up a new React project
### [npx create-vite](https://www.youtube.com/watch?v=TtPXvEcE11E&t=14400s)
#### [`npm install`](https://www.youtube.com/watch?v=TtPXvEcE11E&t=14637s)
**OWN TAKEWAY**
this did not go into detail!

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
