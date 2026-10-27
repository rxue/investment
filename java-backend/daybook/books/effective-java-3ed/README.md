# Chapter 1 Creating and Destroying Objects
## Item 5: Prefer dependency injection to hardwiring resources
* normal ways of dependency injection: *constructor*, *setter*, *static factories*
* `Supplier` is a perfect fit of factories pattern
* DI can clutter up large project with thousands of dependencies => use *dependency injection framework* such as *Spring*

**OWN COMMNT**
In Spring's official documentation, it was written that Spring is an *IoC (inversion of control)* frame work, and DI is a form of *IoC*

# Chapter 2
## Item 6: Avoid creating unnecessary objects
* avoid by using *static factory methods*, e.g. `Boolean.valueOf(String)` instead of `new Boolean`
* in the code example, `String.matches` is not suitable when this is called repeatedly over thousands of times or even more since the `matches` method creates `new Pattern` each time it is called. I made this mistake and fixed it in this commit: https://github.com/rxue/investment/issues/25
# Chapter 11 Concurrency
## Item 80 Prefer executors, tasks, and streams to threads
**Key takeaways**
- *Executor Framework* - essence: *unit of work*, i.e. the *task*, and the execution mechanism are separated
    - critical question: what is the sense of using `newSingleThreadExecutor`
        - answer: there is a *work queue* to achieve the goal of asynchronous execution on the task
    - `ExecutorService` - the essence of it is *thread pool*
        - blocking methods: `invokeAny`, `invokeAll`
        - types of pools, i.e. concrete class of `ExecutorService`
            - `Executors.newScheduledThreadPool` returns `ScheduledExecutorService`
            - `Executors.newCachedThreadPool` returns the thread pool  where **sumitted tasks are note queued but immediately handed off to a thread for execution** => use case: lightly loaded server
            - `Executors.newFixedThreadPool` => use case: heavily loaded production server
    - *tasks*
        - `Runnable` vs `Callable`
            - `Runnable` - `void run()`
            - `Callable<V>` - `V call() throws Exception`
