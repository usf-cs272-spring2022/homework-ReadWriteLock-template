package edu.usfca.cs272;

import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.MethodName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.opentest4j.AssertionFailedError;

/**
 * Attempts to test the {@link SimpleReadWriteLock} and
 * {@link ThreadSafeIndexedSet}. These tests are not 100% accurate. They attempt
 * to create threads in such a way that problems will occur if the
 * implementation is incorrect, but the tests are inexact.
 *
 * @author CS 272 Software Development (University of San Francisco)
 * @version Spring 2022
 */
@TestMethodOrder(MethodName.class)
public class SimpleReadWriteLockTest {
	/** Specifies how long a worker thread should sleep. */
	public static final long WORKER_SLEEP = 500;

	/** How long to wait for deadlocked code. */
	public static final long DEADLOCK_WAIT = 5000;

	/**
	 * Specifies how long to wait before starting a new worker. Must be less than
	 * {@link #WORKER_SLEEP}.
	 */
	public static final long OFFSET_SLEEP = WORKER_SLEEP / 2;

	/** Used to format debug output when tests fail. */
	private static final String DEBUG = "%nExpected:%n%s%n%nActual:%n%s%n";

	/**
	 * Group of unit tests for the read lock.
	 */
	@Nested
	@TestMethodOrder(OrderAnnotation.class)
	public class A_ReadLockTest {
		/**
		 * Tests the child lock behaves as expected for multiple worker threads.
		 * Since there are only reads, no blocking should occur.
		 *
		 * @param threads the number of workers to create
		 */
		@Order(1)
		@ParameterizedTest(name = "{0} worker threads")
		@ValueSource(ints = { 1, 2, 3 })
		public void testMultipleThreads(int threads) {
			List<String> expected = new ArrayList<>();
			expected.addAll(Collections.nCopies(threads, prefix + " Lock"));
			expected.addAll(Collections.nCopies(threads, prefix + " Unlock"));

			Thread[] workers = new Thread[threads];

			for (int i = 0; i < threads; i++) {
				workers[i] = new SingleLockWorker(childLock, actual, prefix);
			}

			testOutput(workers, actual, expected, timeout);
		}

		/**
		 * Tests the child lock behaves as expected for multiple lock calls. Since
		 * there are only reads, no blocking should occur.
		 *
		 * @param locks the number of lock calls to make
		 */
		@Order(2)
		@ParameterizedTest(name = "{0} lock/unlock calls")
		@ValueSource(ints = { 1, 2, 3 })
		public void testMultipleLocks(int locks) {
			List<String> expected = new ArrayList<>();
			expected.addAll(Collections.nCopies(locks, prefix + " Lock"));
			expected.addAll(Collections.nCopies(locks, prefix + " Unlock"));

			Thread[] workers = {
					new MultipleLockWorker(childLock, actual, prefix, locks) };
			testOutput(workers, actual, expected, timeout);
		}

		/** The actual output. */
		public List<String> actual;

		/** The timeout to use for these tests. */
		public long timeout;

		/** The parent lock object (used to create child lock). */
		public SimpleReadWriteLock parentLock;

		/** The child lock (created from the parent lock, lock actually tested). */
		public SimpleLock childLock;

		/** The prefix to use in output. */
		public String prefix;

		/**
		 * Sets up the test methods.
		 *
		 * @param info the test information
		 */
		@BeforeEach
		public void setup(TestInfo info) {
			actual = Collections.synchronizedList(new ArrayList<>());
			parentLock = new SimpleReadWriteLock();

			timeout = Math.round(WORKER_SLEEP * 1.25 + OFFSET_SLEEP);
			childLock = parentLock.readLock();
			prefix = "Read";
		}
	}

	/**
	 * Group of unit tests for the write lock.
	 */
	@Nested
	@TestMethodOrder(OrderAnnotation.class)
	public class B_WriteLockTest {
		/**
		 * Tests the child lock behaves as expected for multiple worker threads.
		 * Since there are multiple different writer threads, blocking should occur.
		 *
		 * @param threads the number of workers to create
		 */
		@Order(1)
		@ParameterizedTest(name = "{0} worker threads")
		@ValueSource(ints = { 1, 2, 3 })
		public void testMultipleThreads(int threads) {
			List<String> expected = new ArrayList<>();
			long timeout = Math.round(WORKER_SLEEP * 1.25 * threads + OFFSET_SLEEP);
			Thread[] workers = new Thread[threads];

			for (int i = 0; i < threads; i++) {
				expected.add(prefix + " Lock");
				expected.add(prefix + " Unlock");

				workers[i] = new SingleLockWorker(childLock, actual, prefix);
			}

			testOutput(workers, actual, expected, timeout);
		}

		/**
		 * Tests the child lock behaves as expected for multiple lock calls. Since
		 * there is only one active writer (even though calling lock multiple
		 * times), no blocking should occur.
		 *
		 * @param locks the number of lock calls to make
		 */
		@Order(2)
		@ParameterizedTest(name = "{0} lock/unlock calls")
		@ValueSource(ints = { 1, 2, 3 })
		public void testMultipleLocks(int locks) {
			List<String> expected = new ArrayList<>();
			expected.addAll(Collections.nCopies(locks, prefix + " Lock"));
			expected.addAll(Collections.nCopies(locks, prefix + " Unlock"));

			long timeout = Math.round(WORKER_SLEEP * 1.25 + OFFSET_SLEEP);
			Thread[] workers = {
					new MultipleLockWorker(childLock, actual, prefix, locks) };
			testOutput(workers, actual, expected, timeout);
		}

		/** The actual output. */
		public List<String> actual;

		/** The parent lock object (used to create child lock). */
		public SimpleReadWriteLock parentLock;

		/** The child lock (created from the parent lock, lock actually tested). */
		public SimpleLock childLock;

		/** The prefix to use in output. */
		public String prefix;

		/**
		 * Sets up the test methods.
		 *
		 * @param info the test information
		 */
		@BeforeEach
		public void setup(TestInfo info) {
			actual = Collections.synchronizedList(new ArrayList<>());
			parentLock = new SimpleReadWriteLock();
			childLock = parentLock.writeLock();
			prefix = "Write";
		}
	}

	/**
	 * Group of unit tests for mixed locks.
	 */
	@Nested
	@TestMethodOrder(OrderAnnotation.class)
	public class C_MixedLockTest {
		/**
		 * Tests that reader threads block the following writer thread.
		 *
		 * @param readers number of reader threads to create
		 */
		@Order(1)
		@ParameterizedTest(name = "{0} readers, 1 writer")
		@ValueSource(ints = { 1, 2, 3 })
		public void testReadThenWrite(int readers) {
			List<String> actual = Collections.synchronizedList(new ArrayList<>());
			List<String> expected = new ArrayList<>();

			// read will be first and will prevent writing
			expected.addAll(Collections.nCopies(readers, "Read Lock"));
			expected.addAll(Collections.nCopies(readers, "Read Unlock"));

			// write will happen after all readers finish unlocking
			expected.add("Write Lock");
			expected.add("Write Unlock");

			SimpleReadWriteLock parentLock = new SimpleReadWriteLock();

			Thread[] workers = new Thread[readers + 1];

			for (int i = 0; i < readers; i++) {
				workers[i] = new SingleLockWorker(parentLock.readLock(), actual, "Read");
			}

			workers[readers] = new SingleLockWorker(parentLock.writeLock(), actual, "Write");

			long timeout = Math.round(WORKER_SLEEP * 1.25 * 2 + OFFSET_SLEEP);
			testOutput(workers, actual, expected, timeout);
		}

		/**
		 * Tests that writer thread block the following reader threads.
		 *
		 * @param readers number of reader threads to create
		 */
		@Order(2)
		@ParameterizedTest(name = "1 writer, {0} readers")
		@ValueSource(ints = { 1, 2, 3 })
		public void testWriteThenRead(int readers) {
			List<String> actual = Collections.synchronizedList(new ArrayList<>());
			List<String> expected = new ArrayList<>();

			// write will be first and will prevent reading
			expected.add("Write Lock");
			expected.add("Write Unlock");

			// read will happen after the writing is complete
			expected.addAll(Collections.nCopies(readers, "Read Lock"));
			expected.addAll(Collections.nCopies(readers, "Read Unlock"));

			SimpleReadWriteLock parentLock = new SimpleReadWriteLock();

			Thread[] workers = new Thread[readers + 1];

			workers[0] = new SingleLockWorker(parentLock.writeLock(), actual, "Write");

			for (int i = 1; i < readers + 1; i++) {
				workers[i] = new SingleLockWorker(parentLock.readLock(), actual, "Read");
			}

			long timeout = Math.round(WORKER_SLEEP * 1.25 * 2 + OFFSET_SLEEP);
			testOutput(workers, actual, expected, timeout);
		}

		/**
		 * Tests that these tests are passing consistently by repeatedly calling
		 * them both.
		 */
		@Order(3)
		@RepeatedTest(3)
		public void testConsistency() {
			testReadThenWrite(3);
			testWriteThenRead(3);
		}
	}

	/**
	 * Group of unit tests for the active write lock.
	 */
	@Nested
	@TestMethodOrder(OrderAnnotation.class)
	public class D_ActiveWriterTest {
		/**
		 * Tests that a single thread can acquire a write lock then a read lock
		 * without releasing first the write lock.
		 */
		@Order(1)
		@RepeatedTest(3)
		public void testWriteThenRead() {
			List<String> actual = Collections.synchronizedList(new ArrayList<>());
			List<String> expected = List.of("Write Lock", "Read Lock", "Read Lock",
					"Read Unlock", "Read Unlock", "Write Unlock");

			SimpleReadWriteLock lock = new SimpleReadWriteLock();

			Thread worker = new Thread() {
				@Override
				public void run() {
					lock.writeLock().lock();
					actual.add("Write Lock");

					lock.readLock().lock();
					actual.add("Read Lock");

					lock.readLock().lock();
					actual.add("Read Lock");

					try {
						Thread.sleep(WORKER_SLEEP);
					}
					catch (Exception e) {
						actual.add("Sleep Error");
					}

					actual.add("Read Unlock");
					lock.readLock().unlock();

					actual.add("Read Unlock");
					lock.readLock().unlock();

					actual.add("Write Unlock");
					lock.writeLock().unlock();
				}
			};

			long timeout = Math.round(WORKER_SLEEP * 1.25 + OFFSET_SLEEP);
			testOutput(new Thread[] { worker }, actual, expected, timeout);
		}

		/**
		 * Tests that a single thread can acquire a write lock then any other locks
		 * without releasing first the write lock.
		 */
		@Order(2)
		@RepeatedTest(3)
		public void testWriteThenMixed() {
			List<String> actual = Collections.synchronizedList(new ArrayList<>());
			List<String> expected = List.of("Write Lock", "Read Lock", "Write Lock",
					"Write Unlock", "Read Unlock", "Write Unlock");

			SimpleReadWriteLock lock = new SimpleReadWriteLock();

			Thread worker = new Thread() {
				@Override
				public void run() {
					lock.writeLock().lock();
					actual.add("Write Lock");

					lock.readLock().lock();
					actual.add("Read Lock");

					lock.writeLock().lock();
					actual.add("Write Lock");

					try {
						Thread.sleep(WORKER_SLEEP);
					}
					catch (Exception e) {
						actual.add("Sleep Error");
					}

					actual.add("Write Unlock");
					lock.writeLock().unlock();

					actual.add("Read Unlock");
					lock.readLock().unlock();

					actual.add("Write Unlock");
					lock.writeLock().unlock();
				}
			};

			long timeout = Math.round(WORKER_SLEEP * 1.25 + OFFSET_SLEEP);
			testOutput(new Thread[] { worker }, actual, expected, timeout);
		}


		/**
		 * Tests that the active writer is unset properly.
		 */
		@Order(2)
		@RepeatedTest(3)
		public void testWriteUnset() {
			List<String> actual = Collections.synchronizedList(new ArrayList<>());
			List<String> expected = List.of("Write Lock", "Write Unlock", "Read Lock",
					"Read Unlock", "Write Lock", "Write Unlock");

			SimpleReadWriteLock lock = new SimpleReadWriteLock();

			// a simple reader threa
			Thread reader = new Thread() {
				@Override
				public void run() {
					lock.readLock().lock();
					actual.add("Read Lock");

					try {
						Thread.sleep(WORKER_SLEEP);
					}
					catch (Exception e) {
						actual.add("Sleep Error");
					}

					actual.add("Read Unlock");
					lock.readLock().unlock();
				}
			};

			Thread writer = new Thread() {
				@Override
				public void run() {
					// make this thread the active writer
					lock.writeLock().lock();
					actual.add("Write Lock");

					// this should unset the active writer
					actual.add("Write Unlock");
					lock.writeLock().unlock();

					// start the reader
					reader.start();

					// wait a little bit for another thread to read
					try {
						Thread.sleep(OFFSET_SLEEP);
					}
					catch (Exception e) {
						actual.add("Sleep Error");
					}

					// try to regain the write lock
					lock.writeLock().lock();
					actual.add("Write Lock");

					actual.add("Write Unlock");
					lock.writeLock().unlock();
				}
			};

			long timeout = Math.round(WORKER_SLEEP * 2.25 + OFFSET_SLEEP);
			testOutput(new Thread[] { writer }, actual, expected, timeout);
		}
	}

	/**
	 * Group of unit tests for the active write lock.
	 */
	@Nested
	@TestMethodOrder(OrderAnnotation.class)
	public class E_LockExceptionTest {
		/**
		 * Tests that calling unlock() without previously calling lock() will throw
		 * an {@link IllegalStateException}.
		 */
		@Order(1)
		@Test
		public void testReadOnlyUnlock() {
			Executable action = () -> {
				SimpleReadWriteLock lock = new SimpleReadWriteLock();
				lock.readLock().unlock();
			};

			Duration timeout = Duration.ofMillis(DEADLOCK_WAIT);
			assertTimeoutPreemptively(timeout, () -> {
				Assertions.assertThrows(IllegalStateException.class, action);
			});
		}

		/**
		 * Tests that calling unlock() more times than lock() will throw an
		 * {@link IllegalStateException}.
		 */
		@Order(2)
		@Test
		public void testReadDoubleUnlock() {
			Executable action = () -> {
				SimpleReadWriteLock lock = new SimpleReadWriteLock();
				lock.readLock().lock();
				lock.readLock().unlock();
				lock.readLock().unlock();
			};

			Duration timeout = Duration.ofMillis(DEADLOCK_WAIT);
			assertTimeoutPreemptively(timeout, () -> {
				Assertions.assertThrows(IllegalStateException.class, action);
			});
		}

		/**
		 * Tests that calling unlock() without previously calling lock() will throw
		 * an {@link IllegalStateException}.
		 */
		@Order(3)
		@Test
		public void testWriteOnlyUnlock() {
			Executable action = () -> {
				SimpleReadWriteLock lock = new SimpleReadWriteLock();
				lock.writeLock().unlock();
			};

			Duration timeout = Duration.ofMillis(DEADLOCK_WAIT);
			assertTimeoutPreemptively(timeout, () -> {
				Assertions.assertThrows(IllegalStateException.class, action);
			});
		}

		/**
		 * Tests that calling unlock() more times than lock() will throw an
		 * {@link IllegalStateException}.
		 */
		@Order(4)
		@Test
		public void testWriteDoubleUnlock() {
			Executable action = () -> {
				SimpleReadWriteLock lock = new SimpleReadWriteLock();
				lock.writeLock().lock();
				lock.writeLock().unlock();
				lock.writeLock().unlock();
			};

			Duration timeout = Duration.ofMillis(DEADLOCK_WAIT);
			assertTimeoutPreemptively(timeout, () -> {
				Assertions.assertThrows(IllegalStateException.class, action);
			});
		}

		/**
		 * Tests that the wrong writer calling unlock will throw an
		 * {@link ConcurrentModificationException}.
		 */
		@Order(5)
		@Test
		public void testWrongWriter() {
			SimpleReadWriteLock lock = new SimpleReadWriteLock();
			JUnitThreadHandler handler = new JUnitThreadHandler();

			Thread lockWorker = new Thread(lock.writeLock()::lock);
			Thread unlockWorker = new Thread(lock.writeLock()::unlock);

			lockWorker.setUncaughtExceptionHandler(handler);
			unlockWorker.setUncaughtExceptionHandler(handler);

			Executable action = () -> {
				lockWorker.start();
				lockWorker.join();

				unlockWorker.start();
				unlockWorker.join();
			};

			long timeout = Math.round(WORKER_SLEEP * 1.25 + OFFSET_SLEEP);
			assertTimeoutPreemptively(Duration.ofMillis(timeout), action);

			boolean result = !handler.thrown.isEmpty() && handler.thrown.get(0)
					.getClass() == ConcurrentModificationException.class;

			String debug = "ConcurrentModificationException expected. Exceptions thrown: %s";
			Assertions.assertTrue(result, () -> debug.formatted(handler.thrown));
		}

		/**
		 * Tests that a single thread cannot acquire a read lock then a write lock
		 * without releasing first the read lock.
		 *
		 * THIS CODE SHOULD DEADLOCK AND TIMEOUT.
		 */
		@Order(6)
		@Test
		public void testReadThenWrite() {
			List<String> actual = Collections.synchronizedList(new ArrayList<>());
			List<String> expected = List.of("Read Lock");

			SimpleReadWriteLock lock = new SimpleReadWriteLock();

			Thread worker = new Thread() {
				@Override
				public void run() {
					lock.readLock().lock();
					actual.add("Read Lock");

					lock.writeLock().lock();
					actual.add("Write Lock");

					try {
						Thread.sleep(WORKER_SLEEP);
					}
					catch (Exception e) {
						actual.add("Sleep Error");
					}

					actual.add("Write Unlock");
					lock.writeLock().unlock();

					actual.add("Read Unlock");
					lock.readLock().unlock();
				}
			};

			try {
				long timeout = Math.round(WORKER_SLEEP * 1.25 * 2 + OFFSET_SLEEP);
				testOutput(new Thread[] { worker }, actual, expected, timeout);
			}
			catch (AssertionFailedError e) {
				Throwable cause = e.getCause();

				if (cause == null) {
					Assertions.fail("Unexpected exception occurred.", e);
				}
				else if (cause.getClass().toString().endsWith("ExecutionTimeoutException")) {
					// the test timed out as expected
					return;
				}
				else {
					Assertions.fail("Unexpected exception occurred.", cause);
				}
			}
			catch (Exception e) {
				Assertions.fail("Unexpected exception occurred.", e);
			}

			Assertions.fail("This code should timeout!");
		}
	}

	/**
	 * Tests the thread output within the provided timeout.
	 *
	 * @param workers the array of workers
	 * @param actual the actual results
	 * @param expected the expected results
	 * @param timeout the timeout
	 */
	public static void testOutput(Thread[] workers, List<String> actual,
			List<String> expected, long timeout) {
		JUnitThreadHandler handler = new JUnitThreadHandler();

		Executable action = () -> {
			// start the first thread with an offset
			workers[0].setUncaughtExceptionHandler(handler);
			workers[0].start();
			Thread.sleep(OFFSET_SLEEP);

			// start the remaining threads (if any)
			for (int i = 1; i < workers.length; i++) {
				workers[i].setUncaughtExceptionHandler(handler);
				workers[i].start();
			}

			// join back with all of the other threads
			for (Thread thread : workers) {
				thread.join();
			}
		};

		Assertions.assertTimeoutPreemptively(Duration.ofMillis(timeout), action,
				"Test timed out. It is possible blocking is occuring when it shouldn't, "
						+ "or threads are not being notified properly.");

		if (handler.thrown.isEmpty()) {
			Assertions.assertEquals(expected, actual, () -> DEBUG
					.formatted(String.join("\n", expected), String.join("\n", actual)));
		}
		else {
			String debug = "Unexpected exception(s) occurred within run() method: "
					+ handler.thrown;
			System.err.println(debug);
			Assertions.fail(debug, handler.thrown.get(0));
		}

	}

	/** Handles uncaught exceptions from threads so JUnit tests can still fail. */
	public static class JUnitThreadHandler
			implements Thread.UncaughtExceptionHandler {
		/** Tracks whether an exception was thrown. */
		public final List<Throwable> thrown;

		/** Initializes this handler. */
		public JUnitThreadHandler() {
			thrown = Collections.synchronizedList(new ArrayList<>());
		}

		@Override
		public void uncaughtException(Thread t, Throwable e) {
			thrown.add(e);
		}
	}

	/**
	 * Used to test single lock/unlock class.
	 */
	public static class SingleLockWorker extends Thread {
		/** The simple lock to use (should come from a SimpleReadWriteLock). */
		public final SimpleLock lock;

		/** The shared list used to capture output. */
		public final List<String> output;

		/** The prefix to use in the buffer output. */
		public final String prefix;

		/**
		 * Sets up the worker.
		 *
		 * @param lock the lock to use
		 * @param output the shared list of results
		 * @param prefix the prefix to use before the result output
		 */
		public SingleLockWorker(SimpleLock lock, List<String> output, String prefix) {
			this.lock = lock;
			this.output = output;
			this.prefix = prefix;
		}

		@Override
		public void run() {
			lock.lock();
			output.add(prefix + " Lock");

			try {
				Thread.sleep(WORKER_SLEEP);
			}
			catch (Exception e) {
				output.add(prefix + " Error");
			}

			output.add(prefix + " Unlock");
			lock.unlock();
		}
	}

	/**
	 * Used to test multiple lock/unlock class.
	 */
	public static class MultipleLockWorker extends Thread {
		/** The simple lock to use (should come from a SimpleReadWriteLock). */
		public final SimpleLock lock;

		/** The shared list used to capture output. */
		public final List<String> output;

		/** The prefix to use in the buffer output. */
		public final String prefix;

		/** The number of repeated calls to lock and unlock. */
		public final int repeats;

		/**
		 * Sets up the worker.
		 *
		 * @param lock the lock to use
		 * @param output the shared list of results
		 * @param prefix the prefix to use before the result output
		 * @param repeats the number of times to call lock/unlock
		 */
		public MultipleLockWorker(SimpleLock lock, List<String> output,
				String prefix, int repeats) {
			this.lock = lock;
			this.output = output;
			this.prefix = prefix;
			this.repeats = repeats;
		}

		@Override
		public void run() {
			for (int i = 0; i < repeats; i++) {
				lock.lock();
				output.add(prefix + " Lock");
			}

			try {
				Thread.sleep(WORKER_SLEEP);
			}
			catch (Exception e) {
				output.add(prefix + " Error");
			}

			for (int i = 0; i < repeats; i++) {
				output.add(prefix + " Unlock");
				lock.unlock();
			}
		}
	}
}
