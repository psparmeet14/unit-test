import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Assertions:
 * assertEquals()
 * assertTrue()
 * assertFalse()
 * assertNotNull()
 * assertNull()
 * assertArrayEquals()
 * assertThrows()
 * assertTimeout()
 * assertAll()
 *
 * Annotations:
 * @Test
 * @BeforeAll
 * @AfterAll
 * @BeforeEach
 * @AfterEach
 * @DisplayName
 * @ParameterizedTest
 * @ValueSource
 * @CsvSource
 * @RepeatedTest
 * @Disabled
 * @Nested
 *
 * Classes:
 * TestInfo
 *
 */
public class ExamplesJUnit5Test {

    private String str;

    @BeforeAll
    static void beforeAll() {
        System.out.println("Initialize connection to database");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("Close connection to database");
    }

    @BeforeEach
    void beforeEach(TestInfo info) {
        System.out.println("Initialize test data for "+info.getDisplayName());
    }

    @AfterEach
    void afterEach(TestInfo info) {
        System.out.println("Clean up test data for "+info.getDisplayName());
    }

    @Test
    void length_basic() {
       assertEquals(3, "ABC".length());
    }

    @Test
    @DisplayName("When string is null, throw an exception")
    void length_exception() {
        String str = null;
        assertThrows(NullPointerException.class, () -> str.length());
    }

    @ParameterizedTest(name = "Length of {0} is greater than 0")
    @ValueSource(strings = {"ABCD", "ABC", "A", "BB"})
    void length_greater_than_zero_parameterized_test(String str) {
        assertTrue(str.length() > 0);
    }

    @ParameterizedTest(name = "Length of {0} is {1}")
    @CsvSource(value = {"abcd, 4", "abc, 3", "'', 0", "ab, 2"})
    void length_parameterized_test(String word, int expectedLength) {
        assertEquals(expectedLength, word.length());
    }

    @RepeatedTest(10)
    void contains_basic() {
        assertTrue("ABCDE".contains("D"));
        assertFalse("ABCDE".contains("X"));
    }

    @Test
    void toUpperCase_basic() {
        assertNotNull("abc".toUpperCase());
        assertEquals("ABC", "abc".toUpperCase());
    }

    @ParameterizedTest(name = "{0} toUpperCase is {1}")
    @CsvSource(value = {"abc, ABC", "abcdef, ABCDEF", "aa, AA", "'', ''"})
    void toUpperCase_parameterized_test(String word, String expectedWord) {
        assertEquals(expectedWord, word.toUpperCase());
    }

    @Test
    void split_basic() {
        String[] actualResult = "abc def ghi".split(" ");
        String[] expectedResult = new String[] {"abc", "def", "ghi"};
        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    @Disabled
    void performancetest_basic() {
        assertTimeout(Duration.ofMillis(1000), () -> {
            for (int i = 0; i <= 100000; i++) {
                int j = i;
                System.out.println(j);
            }
        });
    }

    @Nested
    @DisplayName("For an empty string")
    class EmptyStringTests {

        @BeforeEach
        void setToEmpty() {
            str = "";
        }

        @Test
        @DisplayName("Length should be 0")
        void lengthIsZero() {
            assertEquals(0, str.length());
        }

        @Test
        @DisplayName("toUpperCase() should be empty")
        void uppercaseIsEmpty() {
            assertEquals("", str.toUpperCase());
        }
    }
}
