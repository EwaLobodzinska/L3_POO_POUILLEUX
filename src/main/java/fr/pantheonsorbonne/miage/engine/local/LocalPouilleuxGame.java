package fr.pantheonsorbonne.miage.engine.local;

import fr.pantheonsorbonne.miage.engine.PouilleuxGameEngine;
import fr.pantheonsorbonne.miage.exception.NoMoreCardException;
import fr.pantheonsorbonne.miage.game.Card;
import fr.pantheonsorbonne.miage.game.Deck;
import fr.pantheonsorbonne.miage.game.RandomDeck;

import java.util.*;
import java.util.stream.Collectors;

/**
 * this class implements the war game locally
 */
public class LocalPouilleuxGame extends PouilleuxGameEngine {

    private final List<String> initialPlayers;
    private final Map<String, Queue<Card>> playerCards = new HashMap<>();

    public LocalPouilleuxGame(Deck deck, List<String> initialPlayers) {
        super(deck);
        this.initialPlayers = initialPlayers;
        for (String player : initialPlayers) {
            playerCards.put(player, new LinkedList<>());
        }
    }

    public static void main(String... args) {
        LocalPouilleuxGame localPouilleuxGame = new LocalPouilleuxGame(new RandomDeck(),
                Arrays.asList("Player1", "Player2"));
        localPouilleuxGame.play();
        System.exit(0);

    }

    @Override
    protected List<String> getInitialPlayers() {
        return this.initialPlayers;
    }

    @Override
    protected void giveCardsToPlayer(String playerName, String hand) {
        List<Card> cards = Arrays.asList(Card.stringToCards(hand));
        this.giveCardsToPlayer(cards, playerName);
    }

    @Override
    protected String playRound(Deque<String> players, String playerA, String playerB, Map<Integer, List<Integer>> tourColor) {
        System.out.println("\nRound of: " + playerA);
        System.out
                .println(
                        this.playerCards
                                .keySet().stream().filter(p -> !this.playerCards.get(p).isEmpty()).map(
                                        p -> p + " has now "
                                                + this.playerCards.get(p).stream().map(c -> c.toFancyString())
                                                        .collect(Collectors.joining(" ")))
                                .collect(Collectors.joining("\n")));
        //System.out.println();

        return super.playRound(players, playerA, playerB, tourColor);
    }

    @Override
    protected void declareWinner(String winner) {
    }

    @Override
    protected void declareLoser(String loser) {
    }

    @Override
    protected Card getCardOrGameOver(String cardProviderPlayer) {

        if (!this.playerCards.containsKey(cardProviderPlayer) || this.playerCards.get(cardProviderPlayer).isEmpty()) {
            this.playerCards.remove(cardProviderPlayer); //necessaire?
            return null;
        } else {
            return this.playerCards.get(cardProviderPlayer).poll();
        }
    }

    @Override
    protected boolean checkCardOrGameOver(String cardProviderPlayer) {

        if (!this.playerCards.containsKey(cardProviderPlayer) || this.playerCards.get(cardProviderPlayer).isEmpty()) {
            this.playerCards.remove(cardProviderPlayer); //??
            return false;
        }
        return true;
    }

    @Override
    protected void giveCardsToPlayer(Collection<Card> roundStack, String player) {
        List<Card> cards = new ArrayList<>();
        cards.addAll(roundStack);
        Collections.shuffle(cards);
        this.playerCards.get(player).addAll(cards);
    }

    @Override
    protected Card getCardFromPlayer(String player) throws NoMoreCardException {

        if (!this.playerCards.containsKey(player) || this.playerCards.get(player).isEmpty()) {
            throw new NoMoreCardException();
        } else {
            return this.playerCards.get(player).poll();
        }
    }

    @Override
    protected void giveOneCardToPlayer(Card card, String player) {
        Queue<Card> cards = this.playerCards.get(player);
        cards.add(card);

        List<Card> shuffleCards = new ArrayList<>(cards);
        Collections.shuffle(shuffleCards);
        cards.clear();
        cards.addAll(shuffleCards);

        this.playerCards.put(player, cards);
    }

    @Override
    protected int removePairsFromPlayer(String player, List<Integer> tourColor) {
        List<Card> pairs = findPairs(player);
        int rankToRemove = 0;
        boolean pairRemoved = false;

        if (pairs != null && pairs.size() == 2) {
            Queue<Card> originalQueue = this.playerCards.get(player);
            Queue<Card> updatedQueue = new LinkedList<>();
            rankToRemove = pairs.get(0).getValue().getRank();

            int[] codeToRemove = {
                    pairs.get(0).getColor().getCode(),
                    pairs.get(1).getColor().getCode() };

            for (Card card : originalQueue) {
                if (card.getValue().getRank() != rankToRemove ||
                        (card.getColor().getCode() != codeToRemove[0]
                                && card.getColor().getCode() != codeToRemove[1])){
                    updatedQueue.add(card);
                } else if(tourColor != null && 
                        (card.getColor().getCode() == tourColor.get(0) 
                                || card.getColor().getCode() == tourColor.get(1))){
                    updatedQueue.add(card);
                } else {
                //} else if(rankToRemove != 10 && rankToRemove != 11 && rankToRemove != 12 &&rankToRemove != 13 &&rankToRemove != 14){
                    pairRemoved = true;
                //System.out.println(card.toString());
                }
            }
            this.playerCards.put(player, updatedQueue);

            if(pairRemoved){
                System.out.println(player + " found a pair: " + pairs.get(0).toFancyString() +" " + pairs.get(1).toFancyString());
            }
        }
        return rankToRemove;
    }

    @Override
    protected List<Card> findPairs(String player) {
        Queue<Card> cards = this.playerCards.get(player);
        Map<Integer, Integer> blackCardCount = new HashMap<>();
        Map<Integer, Integer> redCardCount = new HashMap<>();
        List<Card> pair = new ArrayList<>();

        for (Card card : cards) {
            int cardRank = card.getValue().getRank();
            int cardCode = card.getColor().getCode();

            if (cardCode == 127137 || cardCode == 127137 + 16 * 3) {
                if (blackCardCount.containsKey(cardRank)) {
                    blackCardCount.put(cardRank, blackCardCount.get(cardRank) + 1);
                } else {
                    blackCardCount.put(cardRank, 1);
                }
                // blackCardCount.put(cardRank, blackCardCount.getOrDefault(cardRank, 0) + 1);

                if (blackCardCount.get(cardRank) == 2) {
                    for (Card cardPair : cards) {
                        if (cardPair.getValue().getRank() == cardRank && (cardPair.getColor().getCode() == 127137
                                || cardPair.getColor().getCode() == 127137 + 16 * 3)) {
                            pair.add(cardPair);
                            if (pair.size() == 2) {
                                return pair; 
                            }
                        }
                    }
                }
            } else if (cardCode == 127137 + 16 || cardCode == 127137 + 16 * 2) {
                if (redCardCount.containsKey(cardRank)) {
                    redCardCount.put(cardRank, redCardCount.get(cardRank) + 1);
                } else {
                    redCardCount.put(cardRank, 1);
                }
                // redCardCount.put(cardRank, redCardCount.getOrDefault(cardRank, 0) + 1);

                if (redCardCount.get(cardRank) == 2) {
                    for (Card cardPair : cards) {
                        if (cardPair.getValue().getRank() == cardRank && (cardPair.getColor().getCode() == 127137 + 16
                                || cardPair.getColor().getCode() == 127137 + 16 * 2)) {
                            pair.add(cardPair);
                            if (pair.size() == 2) {
                                return pair;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    @Override
    protected boolean checkLoser(String player) {
        Queue<Card> cards = this.playerCards.get(player);
        for (Card card : cards) {
            if (card.getColor().getCode() == 127137 && card.getValue().getRank() == 11) {
                return true;
            }
        }
        return false;
    }
}
