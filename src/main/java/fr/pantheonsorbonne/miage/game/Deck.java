package fr.pantheonsorbonne.miage.game;

public interface Deck {
    default Card getCard() {
        return getCards(1)[0];
    }

    Card[] getCards(int length);

}
