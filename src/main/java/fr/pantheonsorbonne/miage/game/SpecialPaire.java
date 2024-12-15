package fr.pantheonsorbonne.miage.game;

public abstract class SpecialPaire {
    private Card[] paire;

    public SpecialPaire(Card[] paire){
        this.paire = paire;
    }

    public abstract String getSpecialPaireName();
    public abstract String getFunctionName();
}
