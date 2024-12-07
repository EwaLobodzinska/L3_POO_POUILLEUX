package fr.pantheonsorbonne.miage.game;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class DeterministDeck implements Deck {

    private final Queue<Card> cards = new LinkedList<>();


    public DeterministDeck(Card... cards) {
        this.cards.addAll(Arrays.asList(cards));
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
