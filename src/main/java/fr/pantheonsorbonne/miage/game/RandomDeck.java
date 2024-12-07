package fr.pantheonsorbonne.miage.game;

import java.util.*;

/**
 * Represents a Deck of cards
 */
public class RandomDeck implements Deck {

    private final static Random random = new Random();

    private final Queue<Card> deck = new LinkedList<>();


    public RandomDeck() {

        //generate all cards
        List<Card> cards = Card.getAllPossibleCards();
        //shuffle
        Collections.shuffle(cards);
        //associate with the deck
        for (int i = 0; i < cards.size(); i++) {
            this.deck.offer(cards.get(i));
        }

    }


    @Override
    public Card[] getCards(int length) {
        Card[] result = new Card[length];
        for (int i = 0; i < length; i++) {
            result[i] = this.deck.poll();
        }
        return result;
    }

}
