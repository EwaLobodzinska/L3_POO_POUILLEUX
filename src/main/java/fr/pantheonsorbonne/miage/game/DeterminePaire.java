package fr.pantheonsorbonne.miage.game;

public class DeterminePaire {
    private Card[] paire;


    public SpecialPaire detemineSpecialPaire(Card[] paire){
        int cardRank = paire[0].getValue().getRank();
        if (cardRank == 10){
            return new Paire10(paire);
        }
        else if (cardRank == 11){
            return new PaireValet(paire);
        }
        else if (cardRank == 12){
            return new PaireQueen(paire);
        }
        else if (cardRank == 13){
            return new PaireKing(paire);
        }
        else if (cardRank == 14){
            return new PaireAce(paire);
        }
        return null;
    }
}
