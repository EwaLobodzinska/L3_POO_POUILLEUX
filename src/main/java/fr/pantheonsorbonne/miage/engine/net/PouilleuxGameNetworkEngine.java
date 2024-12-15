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

    public PouilleuxGameNetworkEngine(Deck deck, HostFacade hostFacade, Set<String> players, fr.pantheonsorbonne.miage.model.Game war) {
        super(deck);
        this.hostFacade = hostFacade;
        this.players = players;
        this.pouilleux = war;
    }

    public static void main(String[] args) {
        //create the host facade
        HostFacade hostFacade = Facade.getFacade();
        hostFacade.waitReady();

        //set the name of the player
        hostFacade.createNewPlayer("Host");

        //create a new game of war
        fr.pantheonsorbonne.miage.model.Game war = hostFacade.createNewGame("POUILLEUX");

        //wait for enough players to join
        hostFacade.waitForExtraPlayerCount(PLAYER_COUNT);

        PouilleuxGameEngine host = new PouilleuxGameNetworkEngine(new RandomDeck(), hostFacade, war.getPlayers(), war);
        host.play();
        System.exit(0);


    }

    @Override
    protected List<String> getInitialPlayers() {
        return new ArrayList<>(this.pouilleux.getPlayers());
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
            //contestant A is out of cards
            //we send him a gameover
            hostFacade.sendGameCommandToPlayer(pouilleux, cardProviderPlayer, new GameCommand("gameOver"));
            //remove him from the queue so he won't play again
            players.remove(cardProviderPlayer);
            //give back all the cards for this round to the second players
            hostFacade.sendGameCommandToPlayer(pouilleux, cardProviderPlayerOpponent, new GameCommand("cardsForYou", Card.cardsToString(leftOverCard.toArray(new Card[leftOverCard.size()]))));
            return null;
        }

    }

    @Override
    protected void giveCardsToPlayer(Collection<Card> roundStack, String winner) {
        List<Card> cards = new ArrayList<>();
        cards.addAll(roundStack);
        //shuffle the round deck so we are not stuck
        Collections.shuffle(cards);
        hostFacade.sendGameCommandToPlayer(pouilleux, winner, new GameCommand("cardsForYou", Card.cardsToString(cards.toArray(new Card[cards.size()]))));
    }

    @Override
    protected Card getCardFromPlayer(String player) throws NoMoreCardException {
        hostFacade.sendGameCommandToPlayer(pouilleux, player, new GameCommand("playACard"));
        GameCommand expectedCard = hostFacade.receiveGameCommand(pouilleux);
        if (expectedCard.name().equals("card")) {
            return Card.valueOf(expectedCard.body());
        }
        if (expectedCard.name().equals("outOfCard")) {
            throw new NoMoreCardException();
        }
        //should not happen!
        throw new RuntimeException("invalid state");

    }

    @Override
    protected void giveOneCardToPlayer(Card card, String player) {
    }

    @Override
    protected void removePairsFromPlayer(String player){
    }

    @Override
    protected List<Card> findPairs(String player){ 
            return null;
         
    }

    @Override
    protected boolean checkLoser(String player){
        return false;
    }

    @Override
    protected void declareLoser(String loser){

    }

    @Override
    protected boolean checkCardOrGameOver(String cardProviderPlayer){
        return false;
    }



}
