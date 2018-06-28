package com.codecool.klondike;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Pane;

import javafx.scene.control.Button;
import javafx.scene.control.Alert.AlertType;

import java.util.*;

public class Game extends Pane {

    private List<Card> deck = new ArrayList<>();

    private Pile stockPile;
    private Pile discardPile;
    private List<Pile> foundationPiles = FXCollections.observableArrayList();
    private List<Pile> tableauPiles = FXCollections.observableArrayList();
    private  MoveHistory moveHistory = new MoveHistory();

    private double dragStartX, dragStartY;
    private List<Card> draggedCards = FXCollections.observableArrayList();

    private static double STOCK_GAP = 1;
    private static double FOUNDATION_GAP = 0;
    private static double TABLEAU_GAP = 30;


    private EventHandler<MouseEvent> onMouseClickedHandler = e -> {
        Card card = (Card) e.getSource();
        if (card.getContainingPile().getPileType() == Pile.PileType.STOCK) {
            moveHistory.addMove(new Move(card, stockPile, 0));
            card.moveToPile(discardPile);
            card.flip();
            card.setMouseTransparent(false);
            System.out.println("Placed " + card.getRank() + " of " + card.getSuit() + " to the waste.");
        }
    };

    private EventHandler<MouseEvent> stockReverseCardsHandler = e -> {
        refillStockFromDiscard();
    };

    private EventHandler<MouseEvent> onMousePressedHandler = e -> {
        dragStartX = e.getSceneX();
        dragStartY = e.getSceneY();
    };

    private EventHandler<MouseEvent> onMouseDraggedHandler = e -> {
        Card card = (Card) e.getSource();
        Pile activePile = card.getContainingPile();
        if (activePile.getPileType() == Pile.PileType.STOCK)
            return;
        double offsetX = e.getSceneX() - dragStartX;
        double offsetY = e.getSceneY() - dragStartY;

        draggedCards.clear();
        ListIterator i = card.getContainingPile().getCards().listIterator();
        Boolean pastCard = false;
        while(i.hasNext()){
            Card actual = (Card)i.next();
            if (actual.equals(card) && !pastCard){
                    pastCard = true;
            }
            if (pastCard){
                draggedCards.add(actual);
            }

        }
        for (Card drag: draggedCards) {
            drag.getDropShadow().setRadius(20);
            drag.getDropShadow().setOffsetX(10);
            drag.getDropShadow().setOffsetY(10);

            drag.toFront();
            drag.setTranslateX(offsetX);
            drag.setTranslateY(offsetY);
        }
    };

    private EventHandler<MouseEvent> onMouseReleasedHandler = e -> {
        if (draggedCards.isEmpty())
            return;
        Card card = (Card) e.getSource();
        Pile pile = getValidIntersectingPile(card, tableauPiles);
        if (pile == null )
            {pile = getValidIntersectingPile(card, foundationPiles);}

            //TODO
            if (pile != null) {
                ObservableList<Card> previousPile = card.getContainingPile().getCards();
                if (previousPile.size()>1) {                //flip prev. card
                    Card cardToFlip = previousPile.get(previousPile.size() - draggedCards.size() - 1);
                    if (cardToFlip.isFaceDown()) {
                        cardToFlip.flip();
                        addMouseEventHandlers(cardToFlip);
                    }
                }
                handleValidMove(card, pile);
            } else {
                draggedCards.forEach(MouseUtil::slideBack);
                draggedCards.clear();
            }

    };

    public boolean isGameWon() {
        if (!foundationPiles.get(0).isEmpty()
                && !foundationPiles.get(1).isEmpty()
                && !foundationPiles.get(2).isEmpty()
                && !foundationPiles.get(3).isEmpty()) {
            if (foundationPiles.get(0).getTopCard().getRank() == 13
                    && foundationPiles.get(1).getTopCard().getRank() == 13
                    && foundationPiles.get(2).getTopCard().getRank() == 13
                    && foundationPiles.get(3).getTopCard().getRank() == 13) {
                Alert alert = new Alert(AlertType.INFORMATION, "Congratulation!", ButtonType.OK);
                alert.getDialogPane().setMinHeight(100);
                alert.show();
                return true;
            }
        }
        return false;
    }

    public Game() {
        deck = Card.createNewDeck();
        initPiles();
        dealCards();
        initButtons();
    }

    public void addMouseEventHandlers(Card card) {
        card.setOnMousePressed(onMousePressedHandler);
        card.setOnMouseDragged(onMouseDraggedHandler);
        card.setOnMouseReleased(onMouseReleasedHandler);
        card.setOnMouseClicked(onMouseClickedHandler);
    }

    public void refillStockFromDiscard() {
        for(int i = discardPile.getCards().size()-1; i>=0; i--) {
            discardPile.getCards().get(i).flip();
            discardPile.getCards().get(i).moveToPile(stockPile);
        }
        System.out.println("Stock refilled from discard pile.");
    }

    public boolean isMoveValid(Card card, Pile destPile) {
        if ( destPile.isEmpty()) {
            if (destPile.getPileType() == Pile.PileType.TABLEAU) {
                return card.getRank() == Ranks.THIRTEENTH.rankNumber;
            } else {
                return card.getRank() == Ranks.FIRST.rankNumber && draggedCards.size() == 1;
            }
        }
        if (destPile.getPileType() == Pile.PileType.TABLEAU){
            return (card.isOppositeColor(destPile.getTopCard(),card)
                        && card.getRank()+1 == destPile.getTopCard().getRank());
        } else {
            return (card.isSameSuit(destPile.getTopCard(),card)
                            && card.getRank() == destPile.getTopCard().getRank()+1
                            && draggedCards.size() == 1);
        }
    }
    private Pile getValidIntersectingPile(Card card, List<Pile> piles) {
        Pile result = null;
        for (Pile pile : piles) {
            if (!pile.equals(card.getContainingPile()) &&
                    isOverPile(card, pile) &&
                    isMoveValid(card, pile))
                result = pile;
        }
        return result;
    }

    private boolean isOverPile(Card card, Pile pile) {
        if (pile.isEmpty())
            return card.getBoundsInParent().intersects(pile.getBoundsInParent());
        else
            return card.getBoundsInParent().intersects(pile.getTopCard().getBoundsInParent());
    }

    private void handleValidMove(Card card, Pile destPile) {
        String msg = null;
        if (destPile.isEmpty()) {
            if (destPile.getPileType().equals(Pile.PileType.FOUNDATION))
                msg = String.format("Placed %s to the foundation.", card);
            if (destPile.getPileType().equals(Pile.PileType.TABLEAU))
                msg = String.format("Placed %s to a new pile.", card);
        } else {
            msg = String.format("Placed %s to %s.", card, destPile.getTopCard());
        }
        /*
        for (Card c: draggedCards) {
            moveHistory.addMove(new Move(c, destPile, draggedCards.size()));
        }
        */
        System.out.println(msg);
        MouseUtil.slideToDest(draggedCards, destPile);
        draggedCards.clear();
        isGameWon();
    }

    private void initButtons() {
        Button restartButton = new Button("Restart");
        restartButton.setLayoutX(30);
        restartButton.setLayoutY(850);
        getChildren().add(restartButton);
        restartButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                restart();
            }
        });

        Button undoButton = new Button("Undo");
        undoButton.setLayoutX(110);
        undoButton.setLayoutY(850);
        getChildren().add(undoButton);


        undoButton.disableProperty().bind(Bindings.size(moveHistory.returnList()).isEqualTo(0));

        undoButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                moveHistory.undoLastMove();
            }
        });
    }

    private void initPiles() {
        stockPile = new Pile(Pile.PileType.STOCK, "Stock", STOCK_GAP);
        stockPile.setBlurredBackground();
        stockPile.setLayoutX(95);
        stockPile.setLayoutY(20);
        stockPile.setOnMouseClicked(stockReverseCardsHandler);
        getChildren().add(stockPile);

        discardPile = new Pile(Pile.PileType.DISCARD, "Discard", STOCK_GAP);
        discardPile.setBlurredBackground();
        discardPile.setLayoutX(285);
        discardPile.setLayoutY(20);
        getChildren().add(discardPile);

        for (int i = 0; i < 4; i++) {
            Pile foundationPile = new Pile(Pile.PileType.FOUNDATION, "Foundation " + i, FOUNDATION_GAP);
            foundationPile.setBlurredBackground();
            foundationPile.setLayoutX(610 + i * 180);
            foundationPile.setLayoutY(20);
            foundationPiles.add(foundationPile);
            getChildren().add(foundationPile);
        }
        for (int i = 0; i < 7; i++) {
            Pile tableauPile = new Pile(Pile.PileType.TABLEAU, "Tableau " + i, TABLEAU_GAP);
            tableauPile.setBlurredBackground();
            tableauPile.setLayoutX(95 + i * 180);
            tableauPile.setLayoutY(275);
            tableauPiles.add(tableauPile);
            getChildren().add(tableauPile);
        }
    }

    public void dealCards() {
        Collections.shuffle(deck);
        Iterator<Card> deckIterator = deck.iterator();

        //TODO
        for( int i = 0; i < 7; i++) {
            for( int j = 0; j<i+1; j++) {
                Card card = deckIterator.next();
                tableauPiles.get(i).addCard(card);
                getChildren().add(card);
            }
            addMouseEventHandlers(tableauPiles.get(i).getTopCard());
            tableauPiles.get(i).getTopCard().flip();
        }

        deckIterator.forEachRemaining(card -> {
            stockPile.addCard(card);
            addMouseEventHandlers(card);
            getChildren().add(card);
        });

    }

    void setTableBackground(Image tableBackground) {
        setBackground(new Background(new BackgroundImage(tableBackground,
                BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT,
                BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));
    }

    private void restart () {
        for (Pile p: tableauPiles) {
            for (Card c: p.getCards()) {
                getChildren().remove(c);
            }
            p.getCards().clear();
        }

        for (Pile p: foundationPiles) {
            for (Card c: p.getCards()) {
                getChildren().remove(c);
            }
            p.getCards().clear();
        }

        for (Card c: stockPile.getCards()) {
            getChildren().remove(c);
        }
        stockPile.getCards().clear();

        for (Card c: discardPile.getCards()) {
            getChildren().remove(c);
        }

        deck.clear();
        moveHistory.clearMoveHistory();
        discardPile.getCards().clear();
        deck = Card.createNewDeck();
        dealCards();
    }

}
