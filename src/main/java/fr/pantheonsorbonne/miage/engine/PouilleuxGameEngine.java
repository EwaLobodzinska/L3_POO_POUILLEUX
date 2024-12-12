package fr.pantheonsorbonne.miage.engine;

import fr.pantheonsorbonne.miage.exception.NoMoreCardException;
import fr.pantheonsorbonne.miage.game.Card;
import fr.pantheonsorbonne.miage.game.Deck;

import java.util.*;

public abstract class PouilleuxGameEngine {

    private final Deck deck;

    protected PouilleuxGameEngine(Deck deck) {
        this.deck = deck;
    }

    public void play() {
        int numberOfPlayers = getInitialPlayers().size();
        int handSize = 51/numberOfPlayers; //comment trouver deck.size? pour ne pas avoir le variable
        int remainingDeck = 51;
        for (String playerName : getInitialPlayers()) {
            Card[] cards;
            if((remainingDeck)%numberOfPlayers == 0){
                cards = deck.getCards(handSize);
                remainingDeck -= handSize;
            }
            else{
                cards = deck.getCards(handSize+1);
                remainingDeck -= (handSize + 1);
            }
            numberOfPlayers--;
        String hand = Card.cardsToString(cards);
        giveCardsToPlayer(playerName, hand);
        }

        final Queue<String> players = new LinkedList<>();
        players.addAll(this.getInitialPlayers());

        String winner = "";
        int initialPlayerSize = players.size();

        while (players.size() == initialPlayerSize) {

            String firstPlayerInRound = players.poll();
            players.offer(firstPlayerInRound);

            String secondPlayerInRound = players.poll();
            players.offer(secondPlayerInRound);

            if (playRound(players, firstPlayerInRound, secondPlayerInRound)) 
                winner = secondPlayerInRound;

            
        }
        //since we've left the loop, we have only 1 player left: the winner
        //send him the gameover and leave
        declareWinner(winner);
    }

    protected abstract List<String> getInitialPlayers();

    protected abstract void giveCardsToPlayer(String playerName, String hand);

    protected boolean playRound(Queue<String> players, String firstPlayerInRound, String secondPlayerInRound) {

        //here, we try to get the first player card
        Card cardToFirstPlayer = getCardOrGameOver(firstPlayerInRound, secondPlayerInRound);
        if (cardToFirstPlayer == null) {
            //secondPlayerInRound --> winner
            players.remove(firstPlayerInRound);
            return true;
        }

        giveOneCardToPlayer(cardToFirstPlayer, firstPlayerInRound);

        //verifier la paire 
        removePairsFromPlayer(firstPlayerInRound);
        //otherwise we do another round.
        return false;
    }

    protected abstract void declareWinner(String winner);

    protected abstract Card getCardOrGameOver(String cardProviderPlayer, String cardProviderPlayerOpponent);

    protected abstract void giveCardsToPlayer(Collection<Card> cards, String playerName);

    protected abstract Card getCardFromPlayer(String player) throws NoMoreCardException;

    protected abstract void removePairsFromPlayer(String player); 
    
    protected abstract void giveOneCardToPlayer(Card card, String player); 

    protected abstract int findPairs(String playerName);



}
