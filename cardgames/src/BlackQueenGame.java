import java.util.ArrayList;
import java.util.BitSet;

public class BlackQueenGame {

    private boolean playedHearts;
    private final ArrayList<BlackQueenRound> rounds = new ArrayList<>();
    private final BitSet remaining = new BitSet(52);

    public BlackQueenGame(){
        playedHearts = false;
        remaining.set(0,52);
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

    public BitSet getRemaining(){return remaining;}
    public ArrayList<BlackQueenRound> getRounds(){
        return rounds;
    }

    public boolean isPlayedHearts(){return playedHearts;}

    public boolean isNewRound(){
        return rounds.get(rounds.size() - 1).isNewRound();
    }

    public int getTurnInRound(){
        return rounds.get(rounds.size() - 1).getTurn();
    }

    public void playTurn(BlackQueenPlayer player, int card){
        remaining.clear(card);
        if(!playedHearts && (card >= 39)){
            playedHearts = true;
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
