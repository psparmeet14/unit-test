package com.parmeet.unittest.tdd;

public class StringHelper {
    public String truncateAInFirstTwoPositions(String str) {
        if (str.length() < 2)
            return str.replaceAll("A", "");

        String firstTwoChars = str.substring(0, 2);
        String remainingString = str.substring(2);
        return firstTwoChars.replaceAll("A", "") + remainingString;
    }
}
