package fr.pantheonsorbonne.miage.engine;

import fr.pantheonsorbonne.miage.exception.NoMoreCardException;
import fr.pantheonsorbonne.miage.game.Card;
import fr.pantheonsorbonne.miage.game.Deck;

import java.util.*;
import java.util.stream.Collectors;

public abstract class PouilleuxGameEngine {

    private final Deck deck;

    protected PouilleuxGameEngine(Deck deck) {
        this.deck = deck;
    }

    public void play() {
        int numberOfPlayers = getInitialPlayers().size();
        int handSize = 51 / numberOfPlayers; // comment trouver deck.size? pour ne pas avoir le variable
        int remainingDeck = 51;
        for (String playerName : getInitialPlayers()) {
            Card[] cards;
            if ((remainingDeck) % numberOfPlayers == 0) {
                cards = deck.getCards(handSize);
                remainingDeck -= handSize;
            } else {
                cards = deck.getCards(handSize + 1);
                remainingDeck -= (handSize + 1);
            }
            numberOfPlayers--;
            String hand = Card.cardsToString(cards);
            giveCardsToPlayer(playerName, hand);
        }

        final Deque<String> players = new LinkedList<>();
        players.addAll(this.getInitialPlayers());

        for (String player : players) {
            while (findPairs(player) != null) {
                removePairsFromPlayer(player);
            }
        }

        String winner = "";
        // int initialPlayerSize = players.size();

        while (true) {
            String firstPlayerInRound = players.poll();
            players.addLast(firstPlayerInRound);

            String secondPlayerInRound = players.poll();
            players.addFirst(secondPlayerInRound);
            String play;
            if ((play = playRound(players, firstPlayerInRound, secondPlayerInRound)) != "") {
                winner = play;
                declareWinner(winner);
                for (String player : players) {
                    if (player != winner && checkLoser(player)) {
                        declareLoser(player);
                    }
                }
                break;
            }

        }
        // since we've left the loop, we have only 1 player left: the winner
        // send him the gameover and leave

    }

    protected abstract List<String> getInitialPlayers();

    protected abstract void giveCardsToPlayer(String playerName, String hand);

    protected String playRound(Deque<String> players, String firstPlayerInRound, String secondPlayerInRound) {
        String winner;
        int rankToRemove;
        // here, we try to get the first player card
        Card cardToFirstPlayer = getCardOrGameOver(secondPlayerInRound);
        if (cardToFirstPlayer == null) {
            // secondPlayerInRound --> winner
            winner = secondPlayerInRound;
            // players.remove(firstPlayerInRound);
            return winner;
        }

        System.out.println(cardToFirstPlayer.toString());
        giveOneCardToPlayer(cardToFirstPlayer, firstPlayerInRound);

        boolean checkCardOrGameOverSecond = checkCardOrGameOver(secondPlayerInRound);
        if (!checkCardOrGameOverSecond) {
            // secondPlayerInRound --> winner
            winner = secondPlayerInRound;
            // players.remove(firstPlayerInRound);
            return winner;
        }

        // verifier la paire
        rankToRemove = removePairsFromPlayer(firstPlayerInRound);

        boolean checkCardOrGameOver = checkCardOrGameOver(firstPlayerInRound);
        if (!checkCardOrGameOver) {
            winner = firstPlayerInRound;
            // players.remove(secondPlayerInRound);
            return winner;
        }

        if (rankToRemove == 12) {
            System.out.println("Paire de reines ! Changement de sens !");
            changePlayerTurn(players);
        }

        if (rankToRemove == 10) {
            System.out.println("Paire de 10 ! Le joueur suivant saute son tour !");
            skipNextPlayerTurn(players);
        }
        
        // otherwise we do another round.
        return "";
    }

    protected abstract void declareWinner(String winner);

    protected abstract void declareLoser(String loser);

    protected abstract Card getCardOrGameOver(String cardProviderPlayer);

    protected abstract boolean checkCardOrGameOver(String cardProviderPlayer);

    protected abstract void giveCardsToPlayer(Collection<Card> cards, String playerName);

    protected abstract Card getCardFromPlayer(String player) throws NoMoreCardException;

    protected abstract int removePairsFromPlayer(String player);

    protected abstract void giveOneCardToPlayer(Card card, String player);

    protected abstract List<Card> findPairs(String player);

    protected abstract boolean checkLoser(String player);

    protected void changePlayerTurn(Deque<String> players) {

        Deque<String> reversedPlayers = new LinkedList<>();
        String lastPlayer = players.pollLast();
        players.addFirst(lastPlayer);

        while (!players.isEmpty()) {
            reversedPlayers.addFirst(players.pollFirst());
        }

        players.addAll(reversedPlayers);
    }

    protected void skipNextPlayerTurn(Deque<String> players) {
        String skippedPlayer = players.pollFirst();
        players.addLast(skippedPlayer);
    }
}
