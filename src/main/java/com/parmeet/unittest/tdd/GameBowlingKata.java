package com.parmeet.unittest.tdd;

// 10 pins
// 10 frames
// 1 frame - 2 roll
// total 20 rolls
// TC - 0 20
// TC - 1 20
// TC - 1 10 2 10
// TC - spare, 5,5,1,1,1... -> 5 2 1 18
public class GameBowlingKata {

    private final int[] rolls = new int[21];
    private int rollIndex = 0;

    public void roll(int pinsKnockedDown) {
        rolls[rollIndex++] = pinsKnockedDown;
    }

    public int score() {
        int sum = 0;
        int rollIndex = 0;

        for (int frame = 0; frame < 10; frame++) {
            if (isStrike(rollIndex)) {
                sum += 10 + bonusForStrike(rollIndex);
                rollIndex += 1;
            } else if (isSpare(rollIndex)) {
                sum += 10 + bonusForSpare(rollIndex);
                rollIndex += 2;
            } else {
                sum += rolls[rollIndex] + rolls[rollIndex + 1];
                rollIndex += 2;
            }
        }
        return sum;
    }

    private int bonusForSpare(int rollIndex) {
        return rolls[rollIndex + 2];
    }

    private int bonusForStrike(int rollIndex) {
        return rolls[rollIndex + 1] + rolls[rollIndex + 2];
    }

    private boolean isStrike(int rollIndex) {
        return rolls[rollIndex] == 10;
    }

    private boolean isSpare(int rollIndex) {
        return rolls[rollIndex] + rolls[rollIndex + 1] == 10;
    }
}