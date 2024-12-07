package fr.pantheonsorbonne.miage.game;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class DeterministDeck implements Deck {

    private final Queue<Card> cards = new LinkedList<>();

    public DeterministDeck(Card... cards) {
        for (Card card : cards){
            //don't add valet of spade
            if(card.getColor().getCode() != 127137 || card.getValue().getRank() != 11)
                this.cards.add(card);
        }
    }

    @Override
    public Card[] getCards(int length) {
        Card[] res = new Card[length];
        for (int i = 0; i < length; i++) {
            res[i] = this.cards.poll();
        }
        return res;
    }
}
