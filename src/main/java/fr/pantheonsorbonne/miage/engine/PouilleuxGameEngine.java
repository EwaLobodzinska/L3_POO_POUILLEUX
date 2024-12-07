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

        for (String playerName : getInitialPlayers()) {
            numberOfPlayers--;
            Card[] cards;
            if((51-handSize)%numberOfPlayers == 0){
                cards = deck.getCards(handSize);
            }
            else{
                cards = deck.getCards(handSize+1);
            }
        String hand = Card.cardsToString(cards);
        giveCardsToPlayer(playerName, hand);
        }

        final Queue<String> players = new LinkedList<>();
        players.addAll(this.getInitialPlayers());

        while (players.size() > 1) {
            Queue<Card> roundDeck = new LinkedList<>();

            String firstPlayerInRound = players.poll();
            players.offer(firstPlayerInRound);

            String secondPlayerInRound = players.poll();
            players.offer(secondPlayerInRound);

            while (true) {
                if (playRound(players, firstPlayerInRound, secondPlayerInRound, roundDeck)) 
                    break;
            }
        }
        //since we've left the loop, we have only 1 player left: the winner
        String winner = players.poll();
        //send him the gameover and leave
        declareWinner(winner);
        System.out.println(winner + " won! bye");

    }

    protected abstract List<String> getInitialPlayers();

    protected abstract void giveCardsToPlayer(String playerName, String hand);

    protected boolean playRound(Queue<String> players, String firstPlayerInRound, String secondPlayerInRound, Queue<Card> roundDeck) {

        //here, we try to get the first player card
        Card firstPlayerCard = getCardOrGameOver(roundDeck, firstPlayerInRound, secondPlayerInRound);
        if (firstPlayerCard == null) {
            players.remove(firstPlayerInRound);
            this.giveCardsToPlayer(roundDeck, secondPlayerInRound);
            return true;
        }
        //here we also get the second player card
        Card secondPlayerCard = getCardOrGameOver(roundDeck, secondPlayerInRound, firstPlayerInRound);
        if (secondPlayerCard == null) {
            players.remove(secondPlayerInRound);
            //
            this.giveCardsToPlayer(roundDeck, firstPlayerInRound);
            this.giveCardsToPlayer(Arrays.asList(firstPlayerCard), firstPlayerInRound);

            return true;
        }

        //put the two cards on the roundDeck
        roundDeck.offer(firstPlayerCard);
        roundDeck.offer(secondPlayerCard);

        //compute who is the winner
        String winner = getWinner(firstPlayerInRound, secondPlayerInRound, firstPlayerCard, secondPlayerCard);
        //if there's a winner, we distribute the card to him
        if (winner != null) {
            giveCardsToPlayer(roundDeck, winner);
            return true;
        }
        //otherwise we do another round.
        return false;
    }

    protected abstract void declareWinner(String winner);

    protected abstract Card getCardOrGameOver(Collection<Card> leftOverCard, String cardProviderPlayer, String cardProviderPlayerOpponent);

    protected abstract void giveCardsToPlayer(Collection<Card> cards, String playerName);

    protected static String getWinner(String contestantA, String contestantB, Card contestantACard, Card contestantBCard) {
        if (contestantACard.getValue().getRank() > contestantBCard.getValue().getRank()) {
            return contestantA;
        } else if (contestantACard.getValue().getRank() < contestantBCard.getValue().getRank()) {
            return contestantB;
        }
        return null;
    }

    protected abstract Card getCardFromPlayer(String player) throws NoMoreCardException;
}
