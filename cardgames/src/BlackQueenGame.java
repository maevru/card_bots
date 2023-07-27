import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;

public class BlackQueenGame {

    private boolean penaltyPlayed;
    private final ArrayList<BlackQueenRound> rounds = new ArrayList<>();

    public BlackQueenGame(){
        penaltyPlayed = false;
    }

    public void newRound() {
        rounds.add(new BlackQueenRound());
    }

    public BlackQueenPlayer getLastWinner(){
        return rounds.get(rounds.size() - 1).getWinner();
    }

    public BitSet getCurrentSymbol(){
        return rounds.get(rounds.size() - 1).getSymbol();
    }

    public ArrayList<BlackQueenRound> getRounds(){
        return rounds;
    }

    public boolean isPenaltyPlayed(){return penaltyPlayed;}

    public boolean isNewRound(){
        return rounds.get(rounds.size() - 1).isNewRound();
    }

    public void playTurn(BlackQueenPlayer player, int card){
        if(!penaltyPlayed && (card == 23 || card >= 39)){
            penaltyPlayed = true;
        }

        rounds.get(rounds.size() - 1).playTurn(player, card);
    }

    public void printGame(){
        CardConverter cc = new CardConverter();
        for(BlackQueenRound round : rounds){
            round.printRound(cc);
        }
    }
}
