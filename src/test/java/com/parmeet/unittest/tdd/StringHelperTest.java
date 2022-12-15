package com.parmeet.unittest.tdd;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StringHelperTest {

    StringHelper helper = new StringHelper();

    @Test
    void testTruncatAInFirstTwoPositions() {
        assertEquals("BC", helper.truncateAInFirstTwoPositions("AABC"));
        assertEquals("BC", helper.truncateAInFirstTwoPositions("ABC"));
        assertEquals("BBCC", helper.truncateAInFirstTwoPositions("BBCC"));
        assertEquals("BBA", helper.truncateAInFirstTwoPositions("ABBA"));
        assertEquals("BCD", helper.truncateAInFirstTwoPositions("BACD"));
        assertEquals("", helper.truncateAInFirstTwoPositions("AA"));
        assertEquals("", helper.truncateAInFirstTwoPositions("A"));
        assertEquals("", helper.truncateAInFirstTwoPositions(""));
        assertEquals("MNAA", helper.truncateAInFirstTwoPositions("MNAA"));
        assertEquals("BB", helper.truncateAInFirstTwoPositions("BB"));
        assertEquals("AA", helper.truncateAInFirstTwoPositions("AAAA"));
    }

    @Test
    void testAreFirstTwoAndLastTwoCharsSame() {
        assertFalse(helper.areFirstTwoAndLastTwoCharsSame(""));
        assertFalse(helper.areFirstTwoAndLastTwoCharsSame("A"));
        assertTrue(helper.areFirstTwoAndLastTwoCharsSame("AA"));
        assertTrue(helper.areFirstTwoAndLastTwoCharsSame("AB"));
        assertTrue(helper.areFirstTwoAndLastTwoCharsSame("ABCAB"));
        assertFalse(helper.areFirstTwoAndLastTwoCharsSame("ABCDEBA"));
    }
}
