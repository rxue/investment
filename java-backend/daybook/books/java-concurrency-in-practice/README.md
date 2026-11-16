# Java Concurrency in Practice
## I: Fundamentals
### Chapter 5 Building Blocks
#### 5.4 Blocking and interruptible methods (20261115)
> Interruption is a *coorporative* mechanism. One thread cannot force another to stop what it is doing and do something else; when thread A interrupts thread B, A is merely requesting that B stop what it is doing when it gets to a convenient stopping point - if it feels like it. While there is nothing in the API or language specification that demands any specific application-level semantics for interruption, **the most sensible use for interruption is to cancel an activity.** Blocking methods that are responsive to interruption make it easier to cancel long-running activities on a timely basis.

> When a method can throw `InterruptedException`, it is telling you that it is a *blocking method*, and further that it is *interrupted*, it will make an effort to stop blocking early.

**OWN TAKEAWAY**
- `InterruptedException handling` handling 
    - one thing you should not do: never swallow it, i.e. catch it and do nothing in response

## II: Structuring Concurrent Applications
### Chapter 8 Applying Thread Pools
#### 8.5 Parallelzing Recursive Algorithms
> Loops whose bodies contain nontrivial computation perform potentially blocking I/O are frequently good candidates for parallelization, as long as the iterations are independent.

> If you want to submit a set of tasks and wait for them all to complete, you can use `ExecutorService.invokeAll`; to retrieve the results as they become available, you can use `CompletionService`
