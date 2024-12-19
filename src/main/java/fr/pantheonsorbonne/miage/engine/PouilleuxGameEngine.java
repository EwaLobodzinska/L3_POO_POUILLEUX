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
        int handSize = 51 / numberOfPlayers;
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

            System.out.print(playerName + " has ");
            for (Card card : cards){
                System.out.print(card.toFancyString() + " ");
            }
            System.out.println();
        }
        System.out.println();

        final Deque<String> players = new LinkedList<>();
        players.addAll(this.getInitialPlayers());

        for (String player : players) {
            HashMap<Integer, List<Card>> pairs = new HashMap<>();
            List<Card> pair;
            while ((pair = findPairs(player)) != null) {
                int rankToRemove = removePairsFromPlayer(player, null);
                pairs.put(rankToRemove, pair);
            }
            for(Integer rankRemoved : pairs.keySet()){
                if(rankRemoved != null && (rankRemoved == 10 || rankRemoved == 11 || rankRemoved == 12 || rankRemoved == 13 || rankRemoved == 14)){
                    Card[] specialPairTable = {pairs.get(rankRemoved).get(0), pairs.get(rankRemoved).get(1)};
                    String specialPairString = Card.cardsToString(specialPairTable);
                    giveCardsToPlayer(player, specialPairString);
                }
            }    
        }

        Map<Integer, List<Integer>> tourColor = new HashMap<>();
        String loser = "";
        String winner;
        while (true) {
            if(!tourColor.isEmpty()){
                int tourColorKey = tourColor.keySet().iterator().next();
                if(tourColorKey > 0){
                    List<Integer> colors = tourColor.get(tourColorKey);
                    tourColor.clear();
                    tourColor.put(tourColorKey - 1, colors);
                    System.out.println("\nRespect the round color!");
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
                        loser = player;
                        declareLoser(player);
                    }
                }
                break;
            }
        }
        System.out.println("\n" + winner + " has won!");
        System.out.println(loser + " has lost :(");
    }

    protected abstract List<String> getInitialPlayers();

    protected abstract void giveCardsToPlayer(String playerName, String hand);

    protected String playRound(Deque<String> players, String firstPlayerInRound, String secondPlayerInRound, Map<Integer, List<Integer>> tourColor) {
        List<Integer> tourColorValue = null;
        if(!tourColor.isEmpty()){
            tourColorValue = tourColor.values().iterator().next();
        }
        
        String winner;

        Card cardToFirstPlayer = getCardOrGameOver(secondPlayerInRound);
        if (cardToFirstPlayer == null) {
            winner = secondPlayerInRound;
            return winner;
        }
        System.out.println("\n" + firstPlayerInRound + " took " + cardToFirstPlayer.toFancyString() + " from " + secondPlayerInRound);
        giveOneCardToPlayer(cardToFirstPlayer, firstPlayerInRound);

        boolean checkCardOrGameOver = checkCardOrGameOver(secondPlayerInRound);
        if (!checkCardOrGameOver) {
            winner = secondPlayerInRound;
            return winner;
        }

        int rankToRemove = removePairsFromPlayer(firstPlayerInRound, tourColorValue);

        if (rankToRemove == 10) {
            System.out.println("Pair of 10! Next player skips the turn!");
            skipNextPlayerTurn(players);
        }

        if (rankToRemove == 11) {
            System.out.println("Pair of valets! Take one extra card!");
            checkCardOrGameOver= checkCardOrGameOver(firstPlayerInRound);
            if (!checkCardOrGameOver) {
                winner = firstPlayerInRound;
                return winner;
            }
            getSecondCard(firstPlayerInRound, players, tourColor);
        }

        if (rankToRemove == 12) {
            System.out.println("Pair of Queens! Change the direction!");
            changePlayerTurn(players);
        }

        if (rankToRemove == 13) {
            System.out.println("Pair of Kings! Card exchange!");
            changeCards(firstPlayerInRound, players);
        }

        if (rankToRemove == 14) {
            System.out.println("Pair of Ace! Color defined for the next round!");
            tourColor.clear();
            tourColor.putAll(defineColor(players));
        }

        checkCardOrGameOver = checkCardOrGameOver(firstPlayerInRound);
        if (!checkCardOrGameOver) {
            winner = firstPlayerInRound;
            return winner;
        }
        
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

    protected abstract int playerHandSize(String player);

    protected String getSecondCard(String player, Deque<String> players, Map<Integer, List<Integer>> tourColor){
        String secondPlayer = "";
        int maxHand = 0;
        for(String playerHand : players){
            if(playerHandSize(playerHand) > maxHand && playerHand != player){
                maxHand = playerHandSize(playerHand);
                secondPlayer = playerHand;
            }
        }
        // List<String> listPlayers = new ArrayList<>(players);
        // Random rand = new Random();
        // String secondPlayer;
        // int indexSecondPlayer;
        // do {
        //     indexSecondPlayer = rand.nextInt(players.size());
        //     secondPlayer = listPlayers.get(indexSecondPlayer);
        // }
        // while (secondPlayer.equals(player));
        if (!tourColor.isEmpty()){
            int tourColorKey = tourColor.keySet().iterator().next();
            tourColorKey++;
        }
        return playRound(players, player, secondPlayer, tourColor);
    }
   
    protected void changeCards(String player, Deque<String> players){
        int maxHand = 0;
        int secondMaxHand = 0;
        int min = playerHandSize(players.peekFirst());
        String firstPlayer = players.peekFirst();
        String secondPlayer = players.peekLast();

        for(String playerHand : players){
            if (playerHandSize(playerHand) < min){
            min = playerHandSize(playerHand);
        }
            if(playerHandSize(playerHand) >= maxHand){
                secondMaxHand = maxHand;
                secondPlayer = firstPlayer;
                maxHand = playerHandSize(playerHand);
                firstPlayer = playerHand;

            } else if(playerHandSize(playerHand) >= secondMaxHand){
                secondMaxHand = maxHand;
                secondPlayer = playerHand;
            }
        }
        if(playerHandSize(player) == min && firstPlayer != player){
            secondPlayer = player;
        }
        // List<String> listPlayers = new ArrayList<>(players);
        // Random rand = new Random();

        // int indexFirstPlayer = rand.nextInt(players.size());
        // String firstPlayer = listPlayers.get(indexFirstPlayer);

        // int indexSecondPlayer;
        // String secondPlayer;
        // do {
        //     indexSecondPlayer = rand.nextInt(players.size()) ;
        //     secondPlayer = listPlayers.get(indexSecondPlayer);
        // }
        // while (secondPlayer.equals(firstPlayer));

        Card cardToFirstPlayer = getCardOrGameOver(secondPlayer);
        if(cardToFirstPlayer != null){
            System.out.println(secondPlayer + " gives " + cardToFirstPlayer.toFancyString());
            Card cardToSecondPlayer = getCardOrGameOver(firstPlayer);
            System.out.println(firstPlayer + " gives " + cardToSecondPlayer.toFancyString());

            giveOneCardToPlayer(cardToFirstPlayer, firstPlayer);
            giveOneCardToPlayer(cardToSecondPlayer, secondPlayer);
        }
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
            System.out.println("Round color: black");
        } else{
            colorList.add(127137 + 16);
            colorList.add(127137 + 16 * 2);
            tourColor.put(players.size(), colorList);
            System.out.println("Round color: red");
        }
        return tourColor;
    }
}
