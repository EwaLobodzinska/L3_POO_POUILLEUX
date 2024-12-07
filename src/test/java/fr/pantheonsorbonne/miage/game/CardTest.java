package fr.pantheonsorbonne.miage.game;

import fr.pantheonsorbonne.miage.enums.CardColor;
import fr.pantheonsorbonne.miage.enums.CardValue;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CardTest {

    @Test
    void cardsToString() {
        {
            Card card = new Card(CardColor.CLUB, CardValue.ACE);
            assertEquals("1C", card.toString());
        }
        {
            Card card = new Card(CardColor.HEART, CardValue.TEN);
            assertEquals("10H", card.toString());
        }
    }

    @Test
    void getValue() {
        assertEquals(CardValue.ACE, Card.valueOf("1S").getValue());

    }

    @Test
    void getColor() {
        assertEquals(CardColor.SPADE, Card.valueOf("1S").getColor());
    }

    @Test
    void stringToCards() {
        Card[] cards = Card.stringToCards("10S;KH");
        assertEquals(new Card(CardColor.SPADE, CardValue.TEN), cards[0]);
        assertEquals(new Card(CardColor.HEART, CardValue.KING), cards[1]);
    }

    @Test
    void valueOf() {
        assertEquals(CardValue.TEN, Card.valueOf("10D").getValue());
        assertEquals(CardColor.DIAMOND, Card.valueOf("10D").getColor());
    }
}