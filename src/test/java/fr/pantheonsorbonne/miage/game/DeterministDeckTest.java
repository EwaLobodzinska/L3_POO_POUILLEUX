package fr.pantheonsorbonne.miage.game;

public class DeterministDeckTest extends DeckTest {


    @Override
    protected Deck getDeck() {
        return new DeterministDeck(Card.getAllPossibleCards().toArray(new Card[0]));
    }
}
