package com.codecool.klondike;

import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

import java.util.*;

public class Card extends ImageView {

    private Suits suit;
    private Ranks rank;
    private boolean faceDown;

    private Image backFace;
    private Image frontFace;
    private Pile containingPile;
    private DropShadow dropShadow;

    static Image cardBackImage;
    private static final Map<String, Image> cardFaceImages = new HashMap<>();
    public static final int WIDTH = 150;
    public static final int HEIGHT = 215;

    public Card(int suit, int rank, boolean faceDown) {
        this.suit = Suits.values()[suit-1];
        this.rank = Ranks.values()[rank-1];
        this.faceDown = faceDown;
        this.dropShadow = new DropShadow(2, Color.gray(0, 0.75));
        backFace = cardBackImage;
        frontFace = cardFaceImages.get(getShortName());
        setImage(faceDown ? backFace : frontFace);
        setEffect(dropShadow);
    }

    public String getSuit() {
        return this.suit.name();
    }

    public String getRank() {
        return rank.name();
    }

    public boolean isFaceDown() {
        return faceDown;
    }

    public String getShortName() {
        return "S" + suit.suitNumber + "R" + rank.rankNumber;
    }

    public DropShadow getDropShadow() {
        return dropShadow;
    }

    public Pile getContainingPile() {
        return containingPile;
    }

    public void setContainingPile(Pile containingPile) {
        this.containingPile = containingPile;
    }

    public void moveToPile(Pile destPile) {
        this.getContainingPile().getCards().remove(this);
        destPile.addCard(this);
    }

    public void flip() {
        faceDown = !faceDown;
        setImage(faceDown ? backFace : frontFace);
    }

    @Override
    public String toString() {
        return "The " + rank.name() + " of " + suit.name();
    }

    public static boolean isOppositeColor(Card card1, Card card2) {
        return card1.suit.suitNumber != card2.suit.suitNumber
                && (card1.suit.suitNumber + card2.suit.suitNumber) <=6
                && (card1.suit.suitNumber + card2.suit.suitNumber) >=4
                || card2.suit.suitNumber != card1.suit.suitNumber
                && (card2.suit.suitNumber + card1.suit.suitNumber) <=6
                && (card2.suit.suitNumber + card1.suit.suitNumber) >=4;

    }

    public static boolean isSameSuit(Card card1, Card card2) {
        return card1.getSuit().equals(card2.getSuit());
    }

    public static List<Card> createNewDeck() {
        List<Card> result = new ArrayList<>();
        for (Suits s: Suits.values()) {
            for (Ranks r: Ranks.values()) {
                result.add(new Card(s.suitNumber, r.rankNumber, true));
            }
        }
        return result;
    }

    public static void loadCardImages() {
        cardBackImage = new Image("card_images/card_back.png");
        String suitName;
        for (Suits s: Suits.values() ) {
            suitName = s.name();
            for (Ranks r: Ranks.values()) {
                String cardName = suitName + r.rankNumber;
                String cardId = "S" + s.suitNumber + "R" + r.rankNumber;
                String imageFileName = "card_images/" + cardName.toLowerCase() + ".png";
                cardFaceImages.put(cardId, new Image(imageFileName));
            }
        }
    }

}
