package com.codecool.klondike;

class Move {
    Pile previousPile;
    Card whichCard;
    int draggedWith;
    boolean cardWasFaceDown;

    /**
     * @param   card            Card which was moved.
     * @param   fromPile        Pile where the card was moved from.
     * @param   draggedWith     Number of cards dragged with (including) this Card. (0 if it wasn't dragged)
     */
    Move (Card card, Pile fromPile, int draggedWith) {
        this.previousPile = fromPile;
        this.whichCard = card;
        this.draggedWith = draggedWith;
        this.cardWasFaceDown = card.isFaceDown();

    }

}
