package com.codecool.klondike;

public enum Suits {
    HEARTS (1, Color.RED),
    DIAMONDS (2, Color.RED),
    SPADES (3, Color.BLACK),
    CLUBS (4, Color.BLACK);

    public final int suitNumber;
    private final Color color;

    Suits(int suitNumber, Color color) {
        this.suitNumber = suitNumber;
        this.color = color;
    }

    public int getSuitNumber(){
        return this.suitNumber;
    }

    public boolean isSameColor(Suit other) {
        return color == other.color;
    }

    private enum Color {RED, BLACK}

}
