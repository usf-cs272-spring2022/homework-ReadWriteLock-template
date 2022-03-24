package edu.usfca.cs272;

/**
 * A thread-safe version of {@link IndexedSet} using a read/write lock.
 *
 * @param <E> element type
 * @see IndexedSet
 * @see SimpleReadWriteLock
 *
 * @author CS 272 Software Development (University of San Francisco)
 * @version Spring 2022
 */
public class ThreadSafeIndexedSet<E> extends IndexedSet<E> {
	/** The lock used to protect concurrent access to the underlying set. */
	private final SimpleReadWriteLock lock;

	/**
	 * Initializes an unsorted thread-safe indexed set.
	 */
	public ThreadSafeIndexedSet() {
		this(false);
	}

	/**
	 * Initializes a thread-safe indexed set.
	 *
	 * @param sorted whether the set should be sorted
	 */
	public ThreadSafeIndexedSet(boolean sorted) {
		super(sorted);
		lock = new SimpleReadWriteLock();
	}

	/**
	 * Returns the identity hashcode of the lock object. Not particularly useful.
	 *
	 * @return the identity hashcode of the lock object
	 */
	public int lockCode() {
		return System.identityHashCode(lock);
	}

	/*
	 * TODO Override methods as necessary to make this class thread-safe using the
	 * simple read write lock.
	 */
}
