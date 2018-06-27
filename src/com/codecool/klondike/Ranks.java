package com.codecool.klondike;

public enum Ranks {
    FIRST (1),
    SECOND (2),
    THIRD (3),
    FOURTH (4),
    FIFTH (5),
    SIXTH (6),
    SEVENTH (7),
    EIGHTH (8),
    NINTH (9),
    TENTH (10),
    ELEVENTH (11),
    TWELFTH (12),
    THIRTEENTH (13);

    public final int rankNumber;

    Ranks(int rankNumber) {
        this.rankNumber = rankNumber;
    }
}
