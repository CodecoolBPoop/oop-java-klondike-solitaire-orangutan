package com.codecool.klondike;

public enum BoardBackgrounds {
    IMAGE1 ("table/green.png"),
    IMAGE2 ("table/jungle1.png"),
    IMAGE3 ("table/jungle2.png");

    public final String url;

    BoardBackgrounds(String url) {
        this.url = url;
    }
}
