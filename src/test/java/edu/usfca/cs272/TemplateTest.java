package edu.usfca.cs272;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.MethodName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Tests for the {@link Template} class.
 *
 * @author CS 272 Software Development (University of San Francisco)
 * @version Spring 2022
 */
@TestMethodOrder(MethodName.class)
public class TemplateTest {

	/**
	 * Placeholder.
	 */
	@Nested
	@TestMethodOrder(OrderAnnotation.class)
	public class A_Tests {
		/**
		 * Placeholder.
		 */
		@Test
		@Order(1)
		public void test() {
			Assertions.fail("Placeholder.");
		}

		/**
		 * Placeholder.
		 *
		 * @param text placeholder
		 */
		@Order(2)
		@ParameterizedTest(name = "[{index}: \"{0}\"]")
		@ValueSource(strings = { "hello", "world" })
		public void testParam(String text) {
			System.out.println(text);
			Assertions.assertNotNull(text);
		}
	}

	/**
	 * Placeholder.
	 */
	@Nested
	public class B_Tests {
		/**
		 * Placeholder.
		 */
		@RepeatedTest(2)
		public void test1() {
			Assertions.fail("Placeholder.");
		}

		/**
		 * Placeholder.
		 */
		@Test
		@DisplayName("Hello!")
		public void test2() {
			Assertions.assertNull(null, "Placeholder.");
		}

		/**
		 * Placeholder.
		 */
		@Test
		@Timeout(value = 100, unit = TimeUnit.MILLISECONDS)
		public void test3() {
			Executable e = () -> Integer.parseInt("hello");
			Assertions.assertThrows(NumberFormatException.class, e);
		}
	}

	/**
	 * Placeholder.
	 */
	@Nested
	public class C_Tests {
		/**
		 * Placeholder.
		 */
		@Test
		public void test() {
			Assertions.assertDoesNotThrow(() -> true);
		}
	}

	/**
	 * Placeholder.
	 */
	@Nested
	public class D_Tests {
		/**
		 * Placeholder.
		 */
		@Test
		public void test() {
			Assertions.assertDoesNotThrow(() -> true);
		}
	}

	/**
	 * Placeholder.
	 */
	@Nested
	public class E_Tests {
		/**
		 * Placeholder.
		 */
		@Test
		public void test() {
			Assertions.assertDoesNotThrow(() -> true);
		}
	}

	/**
	 * Placeholder.
	 */
	@Nested
	public class F_Tests {
		/**
		 * Placeholder.
		 */
		@Test
		public void test() {
			Assertions.assertDoesNotThrow(() -> true);
		}
	}
}
