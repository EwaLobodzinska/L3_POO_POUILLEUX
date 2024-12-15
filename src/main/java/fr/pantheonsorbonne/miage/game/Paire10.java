package fr.pantheonsorbonne.miage.game;

public class Paire10 extends SpecialPaire {
    public Paire10(Card[] paire){
        super(paire);
    }

    @Override
    public String getSpecialPaireName(){
        return "Paire of 10";
    }

    @Override 
    public String getFunctionName(){
        return "Next player skips his tour";
    }
    // --> access players in GameEngine
    // if special pair == 10 {
                // String skipRoundPlayer = players.poll();
                // players.offer(firstPlayerInRound);
            // }
}