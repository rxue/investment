# Part II: Creating High Quality Code
## Chapter 5: Design in Construction
### 5.3 Design Building Blocks:Heuristics
#### Identify Values Likely to Change
**OWN TAKEWAY**

* *volatile component* : component that is likely to change

**OWN COMMENT** (20261117)
In the `investment-utility` project, I intended to make a new version, where the fetch of price series is implemented. So the old version code is likely to be removed. However, there is code snippet for sending request in the old version component, which can be reused in the new version as well. The critical question hereby is that whether the location of the extracted `RequestSender` class, say should `RequestSender` be kepted in the old version since it is extracted from there or be moved to the new version package. Assume `RequestSender` is stored in the old version, meaning the fetching classes in the new version will have dependency on `RequestSender`, then what if the old version is to be retired? so `RequestSender` should not be placed into the old version, which is a big *volatile component*, but the new version.
