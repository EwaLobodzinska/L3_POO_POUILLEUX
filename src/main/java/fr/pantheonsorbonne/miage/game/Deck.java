package fr.pantheonsorbonne.miage.game;

public interface Deck {
    default Card getCard() {
        Card[] res = getCards(1);

        return getCards(1)[0];
    }

    Card[] getCards(int length);

}
