import java.util.ArrayList;
import java.util.BitSet;

public class BlackLadyGame {

    private boolean playedHearts;
    private final ArrayList<BlackLadyRound> rounds = new ArrayList<>();
    private final BitSet remaining = new BitSet(52);


    public BlackLadyGame(){
        playedHearts = false;
        remaining.set(0,52);
    }

    public void newRound() {
        rounds.add(new BlackLadyRound());
    }

    public BlackLadyPlayer getLastWinner(){
        return rounds.get(rounds.size() - 1).getWinner();
    }

    public BitSet getCurrentSymbol(){
        return rounds.get(rounds.size() - 1).getSymbol();
    }

    public BitSet getRemaining(){return remaining;}
    public ArrayList<BlackLadyRound> getRounds(){
        return rounds;
    }

    public int[] getCardsFromLastRound(){
        return rounds.get(rounds.size() - 1).getCards();
    }

    public boolean isPlayedHearts(){return playedHearts;}

    public boolean isNewRound(){
        return rounds.get(rounds.size() - 1).isNewRound();
    }

    public int getTurnInRound(){
        return rounds.get(rounds.size() - 1).getTurn();
    }

    public void playTurn(BlackLadyPlayer player, int card){
        remaining.clear(card);
        if(!playedHearts && (card >= 39)){
            playedHearts = true;
        }

        rounds.get(rounds.size() - 1).playTurn(player, card);
    }

    public void printGame(){
        CardConverter cc = new CardConverter();
        for(BlackLadyRound round : rounds){
            round.printRound(cc);
        }
    }
}
