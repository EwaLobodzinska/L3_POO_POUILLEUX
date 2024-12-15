package fr.pantheonsorbonne.miage.game;

public class PaireValet extends SpecialPaire {
    public PaireValet(Card[] paire){
        super(paire);
    }

    @Override
    public String getSpecialPaireName(){
        return "Paire of valets";
    }

    @Override 
    public String getFunctionName(){
        return "Choose one card from another player";
    }

    // --> access cards in Local
}