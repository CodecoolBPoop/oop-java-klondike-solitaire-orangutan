package com.codecool.klondike;

public enum CardBackgrounds {
    IMAGE1 ("card_images/card_back1.png"),
    IMAGE2 ("card_images/card_back2.png"),
    IMAGE3 ("card_images/card_back3.png"),
    IMAGE4 ("card_images/card_back4.png");

    public final String url;

    CardBackgrounds(String url) {
        this.url = url;
    }
}
