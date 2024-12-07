package fr.pantheonsorbonne.miage.game;

public class RandomDeckTest extends DeckTest {
    @Override
    protected Deck getDeck() {
        return new RandomDeck();
    }
}
