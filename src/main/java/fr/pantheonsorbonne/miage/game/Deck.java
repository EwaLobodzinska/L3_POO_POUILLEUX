package fr.pantheonsorbonne.miage.game;

public interface Deck {
    default Card getCard() {
        Card[] res = getCards(1);

        return getCards(1)[0];
    }

    /**
     * return an array of random cards
     *
     * @param length the size of the array
     * @return
     */
    Card[] getCards(int length);

}
