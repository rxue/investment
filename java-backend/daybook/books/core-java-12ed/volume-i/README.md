# Core Java, Volume I: Fundamentals, 12th Edition
## Chapter 3: Fundamental Programming Structures in Java
### 3.5.9 Switch Expressions
## Chapter 4: Objects and Classes
### 4.7 Records
#### 4.7.1 The Record Concept
> every `record` has three methods defined automatically: `toString`,`equals` and `hashCode`

**OWN TAKEAWAY**
* the `record` itself is *not immutable* but its *state*
* so if you intend to use `record` as immutable, don't use immutable types for fields


**OWN TAKEAWAY**
So `Record` is not `Comparable` by default
# Chapter 9: Collections
## 9.5 Copies and Views
### 9.5.1 Small Collections (20261026)

**OWN TAKEAWAY**

`List.of(...)` returns an *immutable* list containing n elements

Reference: https://docs.oracle.com/javase//9/docs/api/java/util/List.html#of--

## Chapter 9: Collections
### 9.5 Copies and Views
#### 9.5.2 Unmodifiable Copies and Views (20261103)
> The Collections class has methods that produce unmodiﬁable *views* of collections. These views add a runtime check to an existing collection. If an attempt to modify the unmodiﬁable collection is detected, an exception is thrown.

> However, if the original collection changes, the view reﬂects those changes. That is what makes views different from copies.
