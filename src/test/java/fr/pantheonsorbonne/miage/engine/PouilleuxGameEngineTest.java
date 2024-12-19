package fr.pantheonsorbonne.miage.engine;

import fr.pantheonsorbonne.miage.engine.local.LocalPouilleuxGame;
import fr.pantheonsorbonne.miage.exception.NoMoreCardException;
import fr.pantheonsorbonne.miage.game.Card;
import fr.pantheonsorbonne.miage.game.DeterministDeck;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class PouilleuxGameEngineTest {

    PouilleuxGameEngine engine;
    Deque<String> players;


    @BeforeEach
    void setUp() {
        this.engine = new LocalPouilleuxGame(new DeterministDeck(Card.getAllPossibleCards().toArray(new Card[0])), Arrays.asList("Joueur1", "Joueur2", "Joueur3"));
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
    void giveOneCardToPlayer() throws NoMoreCardException {
        Card card = Card.valueOf("KH");
        engine.giveOneCardToPlayer(card, "Joueur1"); 
        Collection<Card> cardInHand = Arrays.asList(engine.getCardFromPlayer("Joueur1"));
        assertTrue(cardInHand.contains(card));
    }

    @Test
    void getCardFromPlayer() throws NoMoreCardException{
        engine.giveCardsToPlayer(Arrays.asList(Card.valueOf("KH")), "Joueur1");
        Card card = engine.getCardFromPlayer("Joueur1");
        assertEquals("KH", card.toString());
    }

    @Test
    void playRoundSimpleRound() throws NoMoreCardException {
        engine.giveCardsToPlayer(Arrays.asList(Card.valueOf("2H"), Card.valueOf("10C")), "Joueur1");
        engine.giveCardsToPlayer(Arrays.asList(Card.valueOf("2D")), "Joueur2");
        Map<Integer, List<Integer>> tourColor = new HashMap<>();
        String winner = engine.playRound(this.players, "Joueur1", "Joueur2", tourColor);
        
        Collection<Card> cardInHand = Arrays.asList(engine.getCardFromPlayer("Joueur1"));

        assertFalse(cardInHand.contains(Card.valueOf("2H")));
        assertEquals("Joueur2", winner);
    }

    @Test
    void playRoundTourColor() throws NoMoreCardException {
        engine.giveCardsToPlayer(Arrays.asList(Card.valueOf("2H"), Card.valueOf("10C")), "Joueur1");
        engine.giveCardsToPlayer(Arrays.asList(Card.valueOf("2D")), "Joueur2");
        Map<Integer, List<Integer>> tourColor = new HashMap<>();
        List<Integer> colorList = new ArrayList<>();
        colorList.add(127137);
        colorList.add(127137 + 16 * 3);
        tourColor.put(players.size(), colorList);

        engine.playRound(this.players, "Joueur1", "Joueur2", tourColor);
        Collection<Card> cardInHand = Arrays.asList(engine.getCardFromPlayer("Joueur1"));

        assertTrue(cardInHand.contains(Card.valueOf("2H")));

    }

    @Test
    void getCardOrGameOver() throws NoMoreCardException {
        engine.giveCardsToPlayer(Arrays.asList(Card.valueOf("KH")), "Joueur1");
        engine.giveCardsToPlayer(Arrays.asList(), "Joueur2");
        assertNull(engine.getCardOrGameOver("Joueur2"));
        assertEquals("KH", engine.getCardOrGameOver("Joueur1").toString());
    }

    @Test 
    void findPairs(){
        engine.giveCardsToPlayer(Arrays.asList(Card.valueOf("KH"), Card.valueOf("KD")), "Joueur1");
        engine.giveCardsToPlayer(Arrays.asList(Card.valueOf("KS"), Card.valueOf("KD")), "Joueur2");
        List<Card> pair = new ArrayList<>();
        pair.add(Card.valueOf("KD"));
        pair.add(Card.valueOf("KH"));
        assertTrue(pair.containsAll(engine.findPairs("Joueur1")));
        assertNull(engine.findPairs("Joueur2"));
    }

    @Test
    void removePairsFromPlayer() throws NoMoreCardException {
        engine.giveCardsToPlayer(Arrays.asList(Card.valueOf("10C"), Card.valueOf("10S"), Card.valueOf("10D")), "Joueur1");
        int rank = engine.removePairsFromPlayer("Joueur1", null);
        Collection<Card> cardsInHand = Arrays.asList(engine.getCardFromPlayer("Joueur1"));
        List<Card> pair = new ArrayList<>();
        pair.add(Card.valueOf("10C"));
        pair.add(Card.valueOf("10S"));
        
        assertFalse(cardsInHand.containsAll(pair));
        assertEquals(10, rank);
        assertEquals(1, cardsInHand.size());
    }

    @Test 
    void getSecondCard() throws NoMoreCardException{
        engine.giveCardsToPlayer(Arrays.asList(Card.valueOf("7C")), "Joueur1");
        engine.giveCardsToPlayer(Arrays.asList(Card.valueOf("7S")), "Joueur2");
        Deque<String> playersTest = new LinkedList<>();
        playersTest.add("Joueur1");
        playersTest.add("Joueur2");
        Map<Integer, List<Integer>> tourColor = new HashMap<>();

        String winner = engine.getSecondCard("Joueur1", players, tourColor);
        Collection<Card> cardsInHand = Arrays.asList(engine.getCardFromPlayer("Joueur1"), engine.getCardFromPlayer("Joueur1"));
        assertTrue(cardsInHand.contains(Card.valueOf("7S")));
        assertEquals("Joueur2", winner);
    }   

    @Test 
    void changeCards() throws NoMoreCardException{
        engine.giveCardsToPlayer(Arrays.asList(Card.valueOf("8C")), "Joueur1");
        engine.giveCardsToPlayer(Arrays.asList(Card.valueOf("7S")), "Joueur2");
        Deque<String> playersTest = new LinkedList<>();
        playersTest.add("Joueur1");
        playersTest.add("Joueur2");

        engine.changeCards("Joueur1", playersTest);
        Collection<Card> cardsInHandFirst = Arrays.asList(engine.getCardFromPlayer("Joueur1"));
        Collection<Card> cardsInHandSecond = Arrays.asList(engine.getCardFromPlayer("Joueur2"));

        assertTrue(cardsInHandFirst.contains(Card.valueOf("7S")));
        assertTrue(cardsInHandSecond.contains(Card.valueOf("8C")));
    }  
    
    // @Test
    // void checkLoser(){
    //     engine.giveCardsToPlayer(Arrays.asList(Card.valueOf("11S")), "Joueur1");
    //     engine.giveCardsToPlayer(Arrays.asList(Card.valueOf("7S")), "Joueur2");
    //     assertTrue(engine.checkLoser("Joueur1"));
    //     assertFalse(engine.checkLoser("Joueur2"));
    // }
}