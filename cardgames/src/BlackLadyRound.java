import java.util.BitSet;

public class BlackLadyRound {

    private final BlackLadyPlayer[] players = new BlackLadyPlayer[4];
    private final int[] cards = new int[4];
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

        players[turn] = player;
        cards[turn] = card;
        turn++;
    }

    public BlackLadyPlayer getWinner(){return winner;}

    public int getPenaltyPoints(){return penaltyPoints;}

    public BitSet getSymbol(){return symbol;}

    public boolean isNewRound(){
        return turn == 0;
    }

    public int[] getCards(){
        return cards;
    }

    public void printRound(CardConverter cc){
        System.out.println(players[0].getName() + ": " + cc.translate(cards[0]) + " "
                            + players[1].getName() + ": " + cc.translate(cards[1]) + " "
                            + players[2].getName() + ": " + cc.translate(cards[2]) + " "
                            + players[3].getName() + ": " + cc.translate(cards[3]));
    }

    public int getTurn() {
        return turn;
    }
}
