ReadWriteLock
=================================================

![Points](../../blob/badges/points.svg)

For this homework, you will create a conditional read write lock and a thread safe indexed set using that lock.

## Hints ##

Below are some hints that may help with this homework assignment:

  - There are some differences between the `IndexedSet` version presented in lecture and the one provided here. You will have to determine if those differences change which methods should or shouldn't be overridden!

  - Start with using built-in options to make the `ThreadSafeIndexedSet` thread safe, *instead* of your own custom `SimpleReadWriteLock` class. This should help you pass the non-approach tests in `ThreadSafeIndexedSetTest` to make sure you are `@Overriding` methods correctly. Specifically:
  
      - Use the `synchronized` keyword and `@Override` the appropriate methods. Try to pass the non-approach `ThreadSafeIndexedSetTest` tests.
      
      - Switch to using the built-in `ReentrantReadWriteLock` to make those methods thread-safe instead. Try to pass the non-approach `ThreadSafeIndexedSetTest` tests. When using the conditional lock, decide whether a block of operations are read operations, write operations, or mixed read and write operations with respect to the **shared** data. Look at the lecture notes for discussion and code on conditional locks and how they can be used.
      
      - Finally, switch to using the `SimpleReadWriteLock` class and switch your focus on passing the `SimpleReadWriteLock` tests next. (The `ReentrantReadWriteLock` and `SimpleReadWriteLock` classes are designed to be interchangeable, so you should be able to swap without modifying most of your code.)
  
  - The Javadoc comments for the `SimpleReadWriteLock` are written to give clues on the conditions the code should use for the `wait()` or `notifyAll()` calls.

  - The lecture notes discuss a simple read write lock, but the one you must create is more complicated. In addition to tracking the number of readers and writers, it must track the **active** writer as well.

      This includes *setting the active writer* when a thread acquires the write lock and *unsetting the active writer* when the lock is *fully* released. The active writer can acquire additional read or write locks as long as it is the active writer.

      **Leave this part for the end, after you have a simple read write lock already working.**

  - Passing the provided tests consistently is a good sign, but the tests may not catch all implementation issues. However, any time a test fails indicates there is a multithreading problem somewhere (even if it sometimes passes). Logging can help debug these cases.

These hints are *optional*. There may be multiple approaches to solving this homework.

## Requirements ##

See the Javadoc and `TODO` comments in the template code in the `src/main/java` directory for additional details. You must pass the tests provided in the `src/test/java` directory. Do not modify any of the files in the `src/test` directory.

See the [Homework Guides](https://usf-cs272-spring2022.github.io/guides/homework/) for additional details on homework requirements and submission.
