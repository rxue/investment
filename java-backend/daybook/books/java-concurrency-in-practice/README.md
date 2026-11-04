# Java Concurrency in Practice
## II: Structuring Concurrent Applications
### Chapter 8 Applying Thread Pools
#### 8.5 Parallelzing Recursive Algorithms
> Loops whose bodies contain nontrivial computation perform potentially blocking I/O are frequently good candidates for parallelization, as long as the iterations are independent.

> If you want to submit a set of tasks and wait for them all to complete, you can use `ExecutorService.invokeAll`; to retrieve the results as they become available, you can use `CompletionService`
