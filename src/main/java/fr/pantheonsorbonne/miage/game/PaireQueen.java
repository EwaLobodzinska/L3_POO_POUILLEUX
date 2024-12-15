package fr.pantheonsorbonne.miage.game;

import java.util.ArrayList;
import java.util.List;

public class PaireQueen extends SpecialPaire {
    public PaireQueen(Card[] paire){
        super(paire);
    }

    @Override
    public String getSpecialPaireName(){
        return "Paire of queens";
    }

    @Override 
    public String getFunctionName(){
        return "Change the game direction";
    }

    // --> access players in GameEngine
    // public Queue<String> changeDirection(Queue<String> players){
    //     Stack<Integer> reversePlayers = new Stack<>();
    //     while (!players.isEmpty()) {
    //         reversePlayers.add(players.peek());
    //         players.remove();
    //     }
    //     while (!stack.isEmpty()) {
    //         players.add(reversePlayers.peek());
    //         reversePlayers.pop();
    //     }
    // }

}