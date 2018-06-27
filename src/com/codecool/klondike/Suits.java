package com.codecool.klondike;

public enum Suits {
    HEARTS (1),
    DIAMONDS (2),
    SPADES (3),
    CLUBS (4);

    public final int suitNumber;

    Suits(int suitNumber) {
        this.suitNumber = suitNumber;
    }

    public int getSuitNumber(){
        return this.suitNumber;
    }

}
