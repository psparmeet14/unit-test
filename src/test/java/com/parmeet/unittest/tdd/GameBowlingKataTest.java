package com.parmeet.unittest.tdd;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GameBowlingKataTest {

    GameBowlingKata game = new GameBowlingKata();

    @Test
    public void testAll0s() {
        rollMultipleTimes(0, 20);
        assertEquals(0, game.score());
    }

    @Test
    public void testAll1s() {
        rollMultipleTimes(1, 20);
        assertEquals(20, game.score());
    }

    @Test
    public void testAll2s() {
        rollMultipleTimes(2, 20);
        assertEquals(40, game.score());
    }

    @Test
    public void testHalf1sAndHalf2s() {
        // 1 10 2 10
        rollMultipleTimes(1, 10);
        rollMultipleTimes(2, 10);
        assertEquals(10 + 20, game.score());
    }

    @Test
    public void testSpare() {
        rollASpare();
        rollMultipleTimes(1, 18);
        assertEquals(29, game.score());
    }

    @Test
    public void test2Spares() {
        rollASpare();
        rollASpare();
        rollMultipleTimes(1, 16);
        assertEquals(42, game.score());
    }

    @Test
    public void testStrike() {
        game.roll(10);
        rollMultipleTimes(1, 18);
        assertEquals(30, game.score());
    }

    private void rollASpare() {
        rollMultipleTimes(5, 2);
    }

    private void rollMultipleTimes(int pinsKnockedDown, int noOfTimes) {
        for (int i = 1; i <= noOfTimes; i++) {
            game.roll(pinsKnockedDown);
        }
    }
}
