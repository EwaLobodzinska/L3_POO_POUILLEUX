package fr.pantheonsorbonne.miage.engine;

import fr.pantheonsorbonne.miage.engine.local.LocalWarGame;
import fr.pantheonsorbonne.miage.exception.NoMoreCardException;
import fr.pantheonsorbonne.miage.game.Card;
import fr.pantheonsorbonne.miage.game.DeterministDeck;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class WarGameEngineTest {

    WarGameEngine engine;
    Queue<String> players;


    @BeforeEach
    void setUp() {


        this.engine = new LocalWarGame(new DeterministDeck(Card.getAllPossibleCards().toArray(new Card[0])), 2, Arrays.asList("Joueur1", "Joueur2", "Joueur3"));
        this.players = new LinkedList<>();
        this.players.addAll(Arrays.asList("Joueur1", "Joueur2", "Joueur3"));
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getInitialPlayers() {
        assertTrue(engine.getInitialPlayers().containsAll(Set.of("Joueur1", "Joueur2", "Joueur3")));
    }

    @Test
    void giveCardsToPlayer() throws NoMoreCardException {
        Collection<Card> cards = Arrays.asList(Card.valueOf("KH"), Card.valueOf("2S"));
        engine.giveCardsToPlayer(cards, "Joueur1");
        Collection<Card> cardInHand = Arrays.asList(engine.getCardFromPlayer("Joueur1"), engine.getCardFromPlayer("Joueur1"));
        assertTrue(cards.containsAll(cardInHand));
        assertTrue(cardInHand.containsAll(cards));

    }

    @Test
    void testGiveCardsToPlayer() throws NoMoreCardException {
        Collection<Card> cards = Arrays.asList(Card.valueOf("KH"), Card.valueOf("2S"));
        engine.giveCardsToPlayer("Joueur1", "KH;2S");
        Collection<Card> cardInHand = Arrays.asList(engine.getCardFromPlayer("Joueur1"), engine.getCardFromPlayer("Joueur1"));
        assertTrue(cards.containsAll(cardInHand));
        assertTrue(cardInHand.containsAll(cards));
    }

    @Test
    void playRoundSimpleRound() throws NoMoreCardException {
        engine.giveCardsToPlayer(Arrays.asList(Card.valueOf("KH")), "Joueur1");
        engine.giveCardsToPlayer(Arrays.asList(Card.valueOf("QH")), "Joueur2");
        assertTrue(engine.playRound(this.players, "Joueur1", "Joueur2", new LinkedList<>()));
        Collection<Card> cardInHand = Arrays.asList(engine.getCardFromPlayer("Joueur1"), engine.getCardFromPlayer("Joueur1"));

        assertTrue(cardInHand.contains(Card.valueOf("KH")));
        assertTrue(cardInHand.contains(Card.valueOf("QH")));
        assertEquals(2, cardInHand.size());
    }

    @Test
    void playRoundEquality() {
        engine.giveCardsToPlayer(Arrays.asList(Card.valueOf("KH")), "Joueur1");
        engine.giveCardsToPlayer(Arrays.asList(Card.valueOf("KD")), "Joueur2");
        Queue<Card> handOver = new LinkedList<>();
        assertFalse(engine.playRound(this.players, "Joueur1", "Joueur2", handOver));

        assertThrows(NoMoreCardException.class, () -> engine.getCardFromPlayer("Joueur1"));
        assertThrows(NoMoreCardException.class, () -> engine.getCardFromPlayer("Joueur2"));
        assertTrue(handOver.contains(Card.valueOf("KH")));
        assertTrue(handOver.contains(Card.valueOf("KD")));
        assertEquals(2, handOver.size());
    }

    @Test
    void declareWinner() {
        engine.declareWinner("Joueur1");
        //nothing to test here, as the winner declaration is just a prompt
    }

    @Test
    void getCardOrGameOver() throws NoMoreCardException {
        engine.giveCardsToPlayer(Arrays.asList(Card.valueOf("KH")), "Joueur1");
        engine.giveCardsToPlayer(Arrays.asList(), "Joueur2");
        Queue<Card> leftOverCards = new LinkedList<>(Arrays.asList(Card.valueOf("1H"), Card.valueOf("2H")));
        assertNull(engine.getCardOrGameOver(leftOverCards, "Joueur2", "Joueur1"));

        Collection<Card> cardFromJoueur1 = Arrays.asList(engine.getCardFromPlayer("Joueur1"), engine.getCardFromPlayer("Joueur1"), engine.getCardFromPlayer("Joueur1"));
        assertEquals(3, cardFromJoueur1.size());
        assertTrue(cardFromJoueur1.containsAll(Arrays.asList(Card.valueOf("KH"), Card.valueOf("1H"), Card.valueOf("2H"))));
    }

    @Test
    void getWinnerWinJ2() {
        assertEquals("Joueur2", WarGameEngine.getWinner("Joueur1", "Joueur2", Card.valueOf("2H"), Card.valueOf("3H")));
    }

    @Test
    void getWinnerWinJ1() {
        assertEquals("Joueur1", WarGameEngine.getWinner("Joueur1", "Joueur2", Card.valueOf("3H"), Card.valueOf("2H")));
    }

    @Test
    void getWinnerTie() {
        assertEquals(null, WarGameEngine.getWinner("Joueur1", "Joueur2", Card.valueOf("3H"), Card.valueOf("3S")));
    }


    @Test
    void play() throws NoMoreCardException {
        this.engine.play();
        assertThrows(NoMoreCardException.class, () -> this.engine.getCardFromPlayer("Joueur2"));
        assertThrows(NoMoreCardException.class, () -> this.engine.getCardFromPlayer("Joueur3"));
        Collection<Card> player1Cards = Arrays.asList(this.engine.getCardFromPlayer("Joueur1"), this.engine.getCardFromPlayer("Joueur1"), this.engine.getCardFromPlayer("Joueur1"));
        player1Cards.containsAll(Card.getAllPossibleCards().subList(0, 3));


    }
}