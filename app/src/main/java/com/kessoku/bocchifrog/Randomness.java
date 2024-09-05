package com.kessoku.bocchifrog;

public class Randomness {
    public static float rand(float min, float max) {
        return (float) (Math.random() * (max - min)) + min;
    }

    public static float rand(float max) {
        return rand(0, max);
    }

    public static float randPosNeg(float maxPosNeg) {
        return rand(-maxPosNeg, maxPosNeg);
    }
}
