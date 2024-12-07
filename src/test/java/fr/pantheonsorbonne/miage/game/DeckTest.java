package fr.pantheonsorbonne.miage.game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

abstract class DeckTest {

    private Deck deck;

    @BeforeEach
    void setup() {
        this.deck = this.getDeck();
    }

    protected abstract Deck getDeck();

    @Test
    void get1Cards() {
        Card[] cards = deck.getCards(1);
        assertEquals(1, cards.length);
    }

    @Test
    void getnCards() {

        Card[] cards = deck.getCards(10);
        assertEquals(10, cards.length);
    }

    @Test
    void getCard() {
        Card card = deck.getCard();
        Card newCard = null;
        do {

            assertNotEquals(card, newCard);
            newCard = deck.getCard();
        } while (newCard != null);

    }


}