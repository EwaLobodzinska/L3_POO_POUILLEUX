package fr.pantheonsorbonne.miage.engine.net;

import fr.pantheonsorbonne.miage.Facade;
import fr.pantheonsorbonne.miage.PlayerFacade;
import fr.pantheonsorbonne.miage.game.Card;
import fr.pantheonsorbonne.miage.model.Game;
import fr.pantheonsorbonne.miage.model.GameCommand;

import java.util.Deque;
import java.util.Queue;
import java.util.LinkedList;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * this is the player part of the network version of the war game
 */
public class PouilleuxGameNetworkPlayer {

    static final String playerId = "Player-" + new Random().nextInt();
    static final Queue<Card> hand = new LinkedList<>();
    static final PlayerFacade playerFacade = Facade.getFacade();
    static Game pouilleux;

    public static void main(String[] args) {

        playerFacade.waitReady();
        playerFacade.createNewPlayer(playerId);
        pouilleux = playerFacade.autoJoinGame("POUILLEUX");
        while (true) {
            GameCommand command = playerFacade.receiveGameCommand(pouilleux);
            switch (command.name()) {
                case "cardsForYou":
                    System.out.println("I gave");
                    handleCardsForYou(command);
                    break;
                case "getACard":
                    System.out.println(
                            "I took a card ");
                    handleGetACard(command);
                    break;
                case "checkACard":
                    //System.out.println(
                            //"I gave a card ");
                    handleCheckACard(command);
                    break;
                case "getAHand":
                    System.out.println(
                            "My hand " + hand.stream().map(Card::toFancyString).collect(Collectors.joining(" ")));
                    handleGetAHand(command);
                    break;
                case "gameOver":
                    handleGameOverCommand(command);
                    break;
                case "cardsWithoutPair":
                    handleCardsWithoutPair(command);
                    break;
            }
        }
    }

    private static void handleCardsForYou(GameCommand command) {
        for (Card card : Card.stringToCards(command.body())) {
            if (!hand.contains(card)) {
                hand.offer(card);
            }
        }
    }

    private static void handleGetACard(GameCommand command) {
        if (command.params().get("playerId").equals(playerId)) {
            if (!hand.isEmpty()) {
                // String handString =
                // hand.stream().map(Card::toString).collect(Collectors.joining(", "));
                // playerFacade.sendGameCommandToAll(pouilleux, new GameCommand("cards",
                // handString));
                playerFacade.sendGameCommandToAll(pouilleux, new GameCommand("card",
                        hand.poll().toString()));
            } else {
                playerFacade.sendGameCommandToAll(pouilleux, new GameCommand("outOfCard", playerId));
            }
        }
    }

    private static void handleCheckACard(GameCommand command) {

        if (command.params().get("playerId").equals(playerId)) {
            if (!hand.isEmpty()) {
                // String handString =
                // hand.stream().map(Card::toString).collect(Collectors.joining(", "));
                // playerFacade.sendGameCommandToAll(pouilleux, new GameCommand("cards",
                // handString));
                playerFacade.sendGameCommandToAll(pouilleux, new GameCommand("card",
                        hand.peek().toString()));
            } else {
                playerFacade.sendGameCommandToAll(pouilleux, new GameCommand("outOfCard", playerId));
            }
        }
    }

    private static void handleGetAHand(GameCommand command) {
        if (command.params().get("playerId").equals(playerId)) {
            if (!hand.isEmpty()) {
                String handString = hand.stream().map(Card::toString).collect(Collectors.joining(", "));
                playerFacade.sendGameCommandToAll(pouilleux, new GameCommand("cards", handString));
                // playerFacade.sendGameCommandToAll(pouilleux, new GameCommand("cards",
                // hand.toString()));
            } else {
                playerFacade.sendGameCommandToAll(pouilleux, new GameCommand("outOfCards", playerId));
            }
        }
    }

    private static void handleCardsWithoutPair(GameCommand command) {
        hand.clear();
        for (Card card : Card.stringToCards(command.body())) {
            hand.offer(card);
        }
    }

    private static void handleGameOverCommand(GameCommand command) {
        if (command.body().equals("win")) {
            System.out.println("I've won!");
        } else if (command.body().equals("lose")) {
            System.out.println("Game Over! I've lost");
        } else {
            System.out.println("Game Over!");
        }
        System.exit(0);
    }
}
