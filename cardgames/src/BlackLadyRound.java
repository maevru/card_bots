import java.util.ArrayList;
import java.util.BitSet;

public class BlackLadyRound {

    private final ArrayList<String> players = new ArrayList<>();
    private final ArrayList<Integer> cards = new ArrayList<>();
    private int turn = 0;
    private BlackLadyPlayer winner;
    private final BitSet symbol = new BitSet(52);
    private int highestCard = -1;
    private int penaltyPoints = 0;

    public BlackLadyRound(){
        symbol.set(0, 52);
    }

    public void playTurn(BlackLadyPlayer player, int card){
        if(turn == 0){
            symbol.clear();
            if(card < 13){
                symbol.set(0, 13);
            } else if(card < 26){
                symbol.set(13, 26);
            } else if(card < 39){
                symbol.set(26, 39);
            } else {
                symbol.set(39, 52);
            }

            winner = player;
            highestCard = card;

        } else if(symbol.get(card) && card > highestCard){
            winner = player;
            highestCard = card;
        }

        if(card == 23){
            penaltyPoints += 13;
        } else if(card >= 39){
            penaltyPoints += 1;
        }

        players.add(player.getName());
        cards.add(card);
        turn++;
    }

    public ArrayList<String> getPlayers(){return players;}

    public ArrayList<Integer> getCards(){return cards;}

    public BlackLadyPlayer getWinner(){return winner;}

    public int getPenaltyPoints(){return penaltyPoints;}

    public BitSet getSymbol(){return symbol;}

    public boolean isNewRound(){
        return turn == 0;
    }

    public void printRound(CardConverter cc){
        System.out.println(players.get(0) + ": " + cc.translate(cards.get(0)) + " "
                            + players.get(1) + ": " + cc.translate(cards.get(1)) + " "
                            + players.get(2) + ": " + cc.translate(cards.get(2)) + " "
                            + players.get(3) + ": " + cc.translate(cards.get(3)));
    }

    public int getTurn() {
        return turn;
    }
}
