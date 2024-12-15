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

        Map<Integer, List<Integer>> tourColor = new HashMap<>();
        int rankToRemove = 0;
        for (String player : players) {
            while (findPairs(player) != null) {
                rankToRemove = removePairsFromPlayer(player, null);
            }
        }

        String winner = "";
        // int initialPlayerSize = players.size();

        while (true) {
            if(!tourColor.isEmpty()){
                int tourColorKey = tourColor.keySet().iterator().next();
                if(tourColorKey > 0){
                    List<Integer> colors = tourColor.get(tourColorKey);
                    tourColor.clear();
                    tourColor.put(tourColorKey - 1, colors);
                    System.out.println("Respect the color !");
                } else {
                    tourColor.clear();
                }  
            }

            String firstPlayerInRound = players.poll();
            players.addLast(firstPlayerInRound);

            String secondPlayerInRound = players.poll();
            players.addFirst(secondPlayerInRound);
            String play;
            if ((play = playRound(players, firstPlayerInRound, secondPlayerInRound, tourColor)) != "") {
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

    protected String playRound(Deque<String> players, String firstPlayerInRound, String secondPlayerInRound, Map<Integer, List<Integer>> tourColor) {
        List<Integer> tourColorValue = null;
        if(!tourColor.isEmpty()){
            tourColorValue = tourColor.values().iterator().next();
        }
        
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
        rankToRemove = removePairsFromPlayer(firstPlayerInRound, tourColorValue);

        if (rankToRemove == 10) {
            System.out.println("Paire de 10 ! Le joueur suivant saute son tour !");
            skipNextPlayerTurn(players);
        }

        if (rankToRemove == 11) {
            System.out.println("Paire de valets ! Piocher une carte supplemantaire !");
            getSecondCard(firstPlayerInRound, players, tourColor);

        }

        if (rankToRemove == 12) {
            System.out.println("Paire de reines ! Changement de sens !");
            changePlayerTurn(players);
        }

        if (rankToRemove == 13) {
            System.out.println("Paire de rois ! Changement de cartes !");
            changeCards(players);
        }

        if (rankToRemove == 14) {
            System.out.println("Paire d'as' ! Couleur defini pour la prochaine tour !");
            tourColor.clear();
            tourColor.putAll(defineColor(players));
        }

        boolean checkCardOrGameOver = checkCardOrGameOver(firstPlayerInRound);
        if (!checkCardOrGameOver) {
            winner = firstPlayerInRound;
            // players.remove(secondPlayerInRound);
            return winner;
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

    protected abstract int removePairsFromPlayer(String player, List<Integer> tourColors);

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

    //void??
    protected String getSecondCard(String player, Deque<String> players, Map<Integer, List<Integer>> tourColor){
        List<String> listPlayers = new ArrayList<>(players);
        Random rand = new Random();
        String secondPlayer;
        int indexSecondPlayer;
        do {
            indexSecondPlayer = rand.nextInt(players.size());
            secondPlayer = listPlayers.get(indexSecondPlayer);
        }
        while (secondPlayer.equals(player));
        if (!tourColor.isEmpty()){
            int tourColorKey = tourColor.keySet().iterator().next();
            tourColorKey++;
        }
        return playRound(players, player, secondPlayer, tourColor);
    }

    protected void changeCards(Deque<String> players){
        List<String> listPlayers = new ArrayList<>(players);
        Random rand = new Random();

        int indexFirstPlayer = rand.nextInt(players.size());
        String firstPlayer = listPlayers.get(indexFirstPlayer);
        System.out.println(firstPlayer);

        int indexSecondPlayer;
        String secondPlayer;
        do {
            indexSecondPlayer = rand.nextInt(players.size()) ;
            secondPlayer = listPlayers.get(indexSecondPlayer);
        }
        while (secondPlayer.equals(firstPlayer));
        System.out.println(secondPlayer);

        Card cardToFirstPlayer = getCardOrGameOver(secondPlayer);
        System.out.println(cardToFirstPlayer.toString());
        giveOneCardToPlayer(cardToFirstPlayer, firstPlayer);

        Card cardToSecondPlayer = getCardOrGameOver(firstPlayer);
        System.out.println(cardToSecondPlayer.toString());
        giveOneCardToPlayer(cardToSecondPlayer, secondPlayer);
    }

    protected Map<Integer, List<Integer>> defineColor(Deque<String> players){
        Map<Integer, List<Integer>> tourColor = new HashMap<>();
        Random rand = new Random();
        int color = rand.nextInt(0,2);
        List<Integer> colorList = new ArrayList<>();
        if(color == 0){
            colorList.add(127137);
            colorList.add(127137 + 16 * 3);
            tourColor.put(players.size(), colorList);
            System.out.println("The color is black");
        } else{
            colorList.add(127137 + 16);
            colorList.add(127137 + 16 * 2);
            tourColor.put(players.size(), colorList);
            System.out.println("The color is red");
        }
        return tourColor;
    }
}
