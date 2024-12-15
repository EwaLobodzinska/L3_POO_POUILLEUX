package fr.pantheonsorbonne.miage.game;

public class PaireKing extends SpecialPaire {
    public PaireKing(Card[] paire){
        super(paire);
    }

    @Override
    public String getSpecialPaireName(){
        return "Paire of kings";
    }

    @Override 
    public String getFunctionName(){
        return "Change the cards between two players";
    }

    // --> access cards in Local
}