package fr.pantheonsorbonne.miage.game;

public class PaireAce extends SpecialPaire {
    public PaireAce(Card[] paire){
        super(paire);
    }

    @Override
    public String getSpecialPaireName(){
        return "Paire of ace";
    }

    @Override 
    public String getFunctionName(){
        return "Define the tour color";
    }

    
}