package fr.pantheonsorbonne.miage.engine.net;

import fr.pantheonsorbonne.miage.Facade;
import fr.pantheonsorbonne.miage.HostFacade;
import fr.pantheonsorbonne.miage.engine.PouilleuxGameEngine;
import fr.pantheonsorbonne.miage.exception.NoMoreCardException;
import fr.pantheonsorbonne.miage.game.Card;
import fr.pantheonsorbonne.miage.game.Deck;
import fr.pantheonsorbonne.miage.game.RandomDeck;
import fr.pantheonsorbonne.miage.model.Game;
import fr.pantheonsorbonne.miage.model.GameCommand;

import java.util.*;

/**
 * This class implements the war game with the network engine
 */
public class PouilleuxGameNetworkEngine extends PouilleuxGameEngine {
    private static final int PLAYER_COUNT = 4;

    private final HostFacade hostFacade;
    private final Set<String> players;
    private final Game pouilleux;

    public PouilleuxGameNetworkEngine(Deck deck, HostFacade hostFacade, Set<String> players,
            fr.pantheonsorbonne.miage.model.Game pouilleux) {
        super(deck);
        this.hostFacade = hostFacade;
        this.players = players;
        this.pouilleux = pouilleux;
    }

    public static void main(String[] args) {
        HostFacade hostFacade = Facade.getFacade();
        hostFacade.waitReady();

        hostFacade.createNewPlayer("Host");

        fr.pantheonsorbonne.miage.model.Game pouilleux = hostFacade.createNewGame("POUILLEUX");

        hostFacade.waitForExtraPlayerCount(PLAYER_COUNT);

        PouilleuxGameEngine host = new PouilleuxGameNetworkEngine(new RandomDeck(), hostFacade, pouilleux.getPlayers(),
                pouilleux);
        host.play();
        System.exit(0);
    }

    @Override
    protected List<String> getInitialPlayers() {
        return new ArrayList<String>(this.pouilleux.getPlayers());
    }

    @Override
    protected void giveCardsToPlayer(String playerName, String hand) {
        hostFacade.sendGameCommandToPlayer(pouilleux, playerName, new GameCommand("cardsForYou", hand));
    }

    @Override
    protected void declareWinner(String winner) {
        hostFacade.sendGameCommandToPlayer(pouilleux, winner, new GameCommand("gameOver", "win"));
    }

    @Override
    protected Card getCardOrGameOver(String cardProviderPlayer) {
        try {
            return getCardFromPlayer(cardProviderPlayer);
        } catch (NoMoreCardException nmc) {
            return null;
        }
    }

    @Override
    protected boolean checkCardOrGameOver(String cardProviderPlayer) {
        hostFacade.sendGameCommandToPlayer(pouilleux, cardProviderPlayer, new GameCommand("checkACard"));
        GameCommand expectedCard = hostFacade.receiveGameCommand(pouilleux);
        if (expectedCard.name().equals("outOfCard")) {
            return false;
        }
        return true;
    }

    @Override
    protected void giveCardsToPlayer(Collection<Card> roundStack, String player) {
        List<Card> cards = new ArrayList<>();
        cards.addAll(roundStack);
        Collections.shuffle(cards);
        hostFacade.sendGameCommandToPlayer(pouilleux, player,
                new GameCommand("cardsForYou", Card.cardsToString(cards.toArray(new Card[cards.size()]))));
    }

    @Override
    protected Card getCardFromPlayer(String player) throws NoMoreCardException {
        hostFacade.sendGameCommandToPlayer(pouilleux, player, new GameCommand("getACard"));
        GameCommand expectedCard = hostFacade.receiveGameCommand(pouilleux);
        if (expectedCard.name().equals("card")) {
            return Card.valueOf(expectedCard.body());
        }
        if (expectedCard.name().equals("outOfCard")) {
            throw new NoMoreCardException();
        }
        // should not happen!
        throw new RuntimeException("invalid state");
    }

    @Override
    protected void giveOneCardToPlayer(Card card, String player) {
        List<Card> cards = new ArrayList<>();
        cards.add(card);
        hostFacade.sendGameCommandToPlayer(pouilleux, player,
                new GameCommand("cardsForYou", Card.cardsToString(cards.toArray(new Card[cards.size()]))));
    }

    @Override
    protected int removePairsFromPlayer(String player, List<Integer> tourColor) {
        List<Card> pairs = findPairs(player);
        int rankToRemove = 0;

        if (pairs != null && pairs.size() == 2) {
            hostFacade.sendGameCommandToPlayer(pouilleux, player, new GameCommand("getAHand"));
            GameCommand expectedHand = hostFacade.receiveGameCommand(pouilleux);
            if (expectedHand.name().equals("cards")) {
                Card[] cards = Card.stringToCards(expectedHand.body());
                Queue<Card> originalQueue = new LinkedList<>();
                for (Card card : cards) {
                    originalQueue.offer(card);
                }
                Queue<Card> updatedQueue = new LinkedList<>();
                rankToRemove = pairs.get(0).getValue().getRank();

                int[] codeToRemove = {
                        pairs.get(0).getColor().getCode(),
                        pairs.get(1).getColor().getCode() };

                for (Card card : originalQueue) {
                    if (card.getValue().getRank() != rankToRemove ||
                            (card.getColor().getCode() != codeToRemove[0]
                                    && card.getColor().getCode() != codeToRemove[1])) {
                        updatedQueue.add(card);
                    } else if (tourColor != null &&
                            (card.getColor().getCode() == tourColor.get(0)
                                    || card.getColor().getCode() == tourColor.get(1))) {
                        updatedQueue.add(card);
                        // } else {
                    } else if (rankToRemove != 10 && rankToRemove != 11 && rankToRemove != 12 && rankToRemove != 13
                            && rankToRemove != 14) {
                        System.out.println(card.toString());
                    }
                }
                hostFacade.sendGameCommandToPlayer(pouilleux, player, new GameCommand("cardsWithoutPair",
                        Card.cardsToString(updatedQueue.toArray(new Card[updatedQueue.size()]))));
                // hostFacade.sendGameCommandToPlayer(pouilleux, player, new
                // GameCommand("cardsWithoutPair"));
                // giveCardsToPlayer(updatedQueue, player);
            }
        }
        return rankToRemove;
    }

    @Override
    protected List<Card> findPairs(String player) {
        hostFacade.sendGameCommandToPlayer(pouilleux, player, new GameCommand("getAHand"));
        GameCommand expectedHand = hostFacade.receiveGameCommand(pouilleux);
        if (expectedHand.name().equals("cards")) {
            Card[] cardsReceived = Card.stringToCards(expectedHand.body());
            Queue<Card> cards = new LinkedList<>();
            for (Card card : cardsReceived) {
                cards.offer(card);
            }
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
                            if (cardPair.getValue().getRank() == cardRank
                                    && (cardPair.getColor().getCode() == 127137 + 16
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
        return null;
    }

    @Override
    protected boolean checkLoser(String player) {
        hostFacade.sendGameCommandToPlayer(pouilleux, player, new GameCommand("getAHand"));
        GameCommand expectedHand = hostFacade.receiveGameCommand(pouilleux);
        if (expectedHand.name().equals("cards")) {
            Card[] cards = Card.stringToCards(expectedHand.body());
            for (Card card : cards) {
                if (card.getColor().getCode() == 127137 && card.getValue().getRank() == 11) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected void declareLoser(String loser) {
        hostFacade.sendGameCommandToPlayer(pouilleux, loser, new GameCommand("gameOver", "lose"));
    }
}
