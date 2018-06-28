package com.codecool.klondike;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.EmptyStackException;
import java.util.List;
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
        try {
            int n;
            Move lastMove = mh.peek();
            if (lastMove.previousPile.getPileType() == Pile.PileType.TABLEAU && !lastMove.previousPile.isEmpty()) {
                List<Card> cards = lastMove.previousPile.getCards();
                int i = 0;
                while (i < cards.size()) {
                    if (!cards.get(i).isFaceDown()) {
                        break;
                    }
                    i++;
                }
                if (i == cards.size() - 1) {
                    lastMove.previousPile.getTopCard().flip();
                }
            }
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
        } catch (EmptyStackException e) {}
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
