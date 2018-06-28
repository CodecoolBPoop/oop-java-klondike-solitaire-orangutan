package com.codecool.klondike;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Stack;

class MoveHistory {
    private Stack<Move> mh;
    private ObservableList<Card> forUndoButton = FXCollections.observableArrayList();

    MoveHistory() {
        mh = new Stack<>();
    }

    void addMove(Move move) {
        mh.push(move);
        forUndoButton.add(move.whichCard);
    }

    private void removeLastMove() {
        if (!mh.isEmpty()) {
            mh.pop();
            forUndoButton.remove(forUndoButton.size()-1);
        }
    }

    void undoLastMove() {
        int n;
        Move lastMove = mh.peek();
        lastMove.previousPile.getTopCard().flip();
        do {
            n = lastMove.draggedWith;
            if (!lastMove.cardWasFaceDown
                    && lastMove.whichCard.isFaceDown()
                    || lastMove.cardWasFaceDown
                    && !lastMove.whichCard.isFaceDown()) {
                lastMove.whichCard.flip();
            }
            lastMove.whichCard.moveToPile(lastMove.previousPile);
            removeLastMove();
            lastMove = mh.peek();
            n--;
        } while (n > 0);
    }

    void clearMoveHistory() {
        mh.clear();
        forUndoButton.clear();

    }

    boolean isEmpty() {
        return mh.isEmpty();
    }

    ObservableList returnList(){
        return forUndoButton;
    }
}
