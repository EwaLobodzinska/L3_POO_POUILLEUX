package fr.pantheonsorbonne.miage.game;

public abstract class SpecialPaire {
    private Card[] paire;

    public SpecialPaire(Card[] paire){
        this.paire = paire;
    }

    public abstract String getSpecialPaireName();
    public abstract String getFunctionName();
}


public class Paire10 extends SpecialPaire {
    public Paire10(Card[] paire){
        super(paire);
    }

    @Override
    public String getSpecialPaireName(){
        return "Paire of 10";
    }

    @Overrdie 
    public String getFunctionName(){
        return "";
    }
}


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
}

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
}

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
}

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
}

public class PaireAs extends SpecialPaire {
    public PaireAs(Card[] paire){
        super(paire);
    }

    @Override
    public String getSpecialPaireName(){
        return "Paire of as";
    }

    @Override 
    public String getFunctionName(){
        return "Define the tour color";
    }
}
