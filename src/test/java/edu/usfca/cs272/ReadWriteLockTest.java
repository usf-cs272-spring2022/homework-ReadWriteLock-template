package edu.usfca.cs272;

import org.junit.jupiter.api.MethodOrderer.MethodName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * Runs both the {@link SimpleReadWriteLockTest} unit tests and the
 * {@link ThreadSafeIndexedSetTest} unit tests.
 *
 * @author CS 272 Software Development (University of San Francisco)
 * @version Spring 2022
 */
@TestMethodOrder(MethodName.class)
public class ReadWriteLockTest {
	/**
	 * This is the test class run by Github Actions. It should not be the test
	 * class run in Eclipse.
	 */

	/**
	 * All of the tests in {@link SimpleReadWriteLockTest}.
	 *
	 * @see SimpleReadWriteLockTest
	 */
	@Nested
	public class Group1Tests {
		/** Explicitly includes this test class for maven. */
		@Nested
		public class A_Group extends SimpleReadWriteLockTest.A_ReadLockTest {
			/** Needed to extend an inner class. */
			public A_Group() {
				new SimpleReadWriteLockTest().super();
			}
		}

		/** Explicitly includes this test class for maven. */
		@Nested
		public class B_Group extends SimpleReadWriteLockTest.B_WriteLockTest {
			/** Needed to extend an inner class. */
			public B_Group() {
				new SimpleReadWriteLockTest().super();
			}
		}

		/** Explicitly includes this test class for maven. */
		@Nested
		public class C_Group extends SimpleReadWriteLockTest.C_MixedLockTest {
			/** Needed to extend an inner class. */
			public C_Group() {
				new SimpleReadWriteLockTest().super();
			}
		}

		/** Explicitly includes this test class for maven. */
		@Nested
		public class D_Group extends SimpleReadWriteLockTest.D_ActiveWriterTest {
			/** Needed to extend an inner class. */
			public D_Group() {
				new SimpleReadWriteLockTest().super();
			}
		}

		/** Explicitly includes this test class for maven. */
		@Nested
		public class E_Group extends SimpleReadWriteLockTest.E_LockExceptionTest {
			/** Needed to extend an inner class. */
			public E_Group() {
				new SimpleReadWriteLockTest().super();
			}
		}
	}

	/**
	 * All of the tests in {@link ThreadSafeIndexedSetTest}.
	 *
	 * @see ThreadSafeIndexedSetTest
	 */
	@Nested
	public class Group2Tests {
		/** Explicitly includes this test class for maven. */
		@Nested
		public class F_Group extends ThreadSafeIndexedSetTest.F_AddTests {
			/** Needed to extend an inner class. */
			public F_Group() {
				new ThreadSafeIndexedSetTest().super();
			}
		}

		/** Explicitly includes this test class for maven. */
		@Nested
		public class G_Group extends ThreadSafeIndexedSetTest.G_GetTests {
			/** Needed to extend an inner class. */
			public G_Group() {
				new ThreadSafeIndexedSetTest().super();
			}
		}

		/** Explicitly includes this test class for maven. */
		@Nested
		public class H_Group extends ThreadSafeIndexedSetTest.H_MixedTests {
			/** Needed to extend an inner class. */
			public H_Group() {
				new ThreadSafeIndexedSetTest().super();
			}
		}

		/** Explicitly includes this test class for maven. */
		@Nested
		public class I_Group extends ThreadSafeIndexedSetTest.I_ApproachTests {
			/** Needed to extend an inner class. */
			public I_Group() {
				new ThreadSafeIndexedSetTest().super();
			}
		}
	}
}
