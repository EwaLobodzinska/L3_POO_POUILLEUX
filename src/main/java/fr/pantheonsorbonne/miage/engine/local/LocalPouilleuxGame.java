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
        LocalPouilleuxGame localPouilleuxGame = new LocalPouilleuxGame(new RandomDeck(), Arrays.asList("Joueur1", "Joueur2", "Joueur3"));
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
    protected boolean playRound(Queue<String> players, String playerA, String playerB) {
        System.out.println("New round:");
        System.out.println(this.playerCards.keySet().stream().filter(p -> !this.playerCards.get(p).isEmpty()).map(p -> p + " has " + this.playerCards.get(p).stream().map(c -> c.toFancyString()).collect(Collectors.joining(" "))).collect(Collectors.joining("\n")));
        System.out.println();
        return super.playRound(players, playerA, playerB);

    }

    @Override
    protected void declareWinner(String winner) {
        System.out.println(winner + " has won!");
    }

    @Override
    protected Card getCardOrGameOver(String cardPlayer, String cardProviderPlayer) {

        if (!this.playerCards.containsKey(cardProviderPlayer) || this.playerCards.get(cardProviderPlayer).isEmpty()) {
            this.playerCards.remove(cardProviderPlayer);
            return null;
        } else {
            return this.playerCards.get(cardProviderPlayer).poll();
        }
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
        this.playerCards.get(player).add(card);
    }

    //@Override
    // protected void removePairsFromPlayer(String player){
    //     int rankToRemove = findPairs(player);
    //     if (rankToRemove != 0){
    //         for(Card card: this.playerCards.get(player)){
    //             int cardRank = card.getValue().getRank();
    //             if(cardRank == rankToRemove){
    //                 this.playerCards.get(player).remove(card);
                    
    //             }
    //         }
    //     }
    // }

    @Override
    protected void removePairsFromPlayer(String player) {
    int rankToRemove = findPairs(player); // Identify the rank to remove
    if (rankToRemove != 0) {
        Queue<Card> originalQueue = this.playerCards.get(player);
        Queue<Card> updatedQueue = new LinkedList<>();

        for (Card card : originalQueue) {
            if (card.getValue().getRank() != rankToRemove) {
                updatedQueue.add(card); // Keep only the cards that don't match the rank
            }
        }

        // Replace the player's queue with the filtered one
        this.playerCards.put(player, updatedQueue);
    }
}


    @Override
    protected int findPairs(String player){
            Queue<Card> cards = this.playerCards.get(player);
            Map<Integer, Integer> cardCount = new HashMap<>();
            
            for(Card card : cards){
                int cardRank = card.getValue().getRank();
                if(cardCount.containsKey(cardRank)){
                    cardCount.put(cardRank, cardCount.get(cardRank) + 1);
                    return cardRank;
                }else{
                    cardCount.put(cardRank, 1);
                }
            }     
            return 0;
         
    }
}
