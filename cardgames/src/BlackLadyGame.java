import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;

public class BlackLadyGame {
    private final ArrayList<BlackLadyRound> rounds = new ArrayList<>();
    private final BitSet remainingCards = new BitSet(52);
    private boolean playedHearts;

    private final HashMap<BlackLadyPlayer, BitSet> canPlay = new HashMap<>();

    public BlackLadyGame(ArrayList<BlackLadyPlayer> players){
        playedHearts = false;
        remainingCards.set(0,52);
        for(BlackLadyPlayer player : players){
            BitSet set = new BitSet(52);
            set.set(0,52);
            canPlay.put(player, set);
        }
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

    public BitSet getRemainingCards(){return remainingCards;}
    public ArrayList<BlackLadyRound> getRounds(){
        return rounds;
    }

    public ArrayList<Integer> getCardsFromLastRound(){
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
        assert (canPlay.get(player).get(card));

        remainingCards.clear(card);
        for(BlackLadyPlayer p : canPlay.keySet()){
            canPlay.get(p).clear(card);
        }

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
