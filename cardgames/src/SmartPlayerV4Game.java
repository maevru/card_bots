import java.util.ArrayList;
import java.util.BitSet;

public class SmartPlayerV4Game {

    private final ArrayList<Integer> cards;
    private final ArrayList<Integer> players;
    private final ArrayList<BitSet> playersCards = new ArrayList<>();
    private final BitSet remaining = new BitSet();
    private final int[] score;

    SmartPlayerV4Game(ArrayList<BitSet> players, BitSet remaining){
        this.players = new ArrayList<>();
        this.cards = new ArrayList<>();
        this.remaining.or(remaining);
        this.playersCards.addAll(players);
        this.score = new int[4];
    }

    SmartPlayerV4Game(SmartPlayerV4Game game){
        this.cards = new ArrayList<>(game.getCards());
        this.players = new ArrayList<>(game.getPlayers());
        this.score = new int[4];
        for(int i = 0; i < 4; i++){
            score[i] = game.getScore()[i];
        }
    }

    public void makeMove(int player, int card){
        cards.add(card);
        players.add(player);

        playersCards.get(player).clear(card);
        remaining.clear(card);
    }

    public void undoMove(int player, int card){
        cards.remove(cards.size() - 1);
        players.remove(players.size() - 1);

        playersCards.get(player).set(card);
        remaining.set(card);
    }

    public void addScore(int player, int penalty){
        score[player] += penalty;
    }

    public void removeScore(int player, int penalty){
        score[player] -= penalty;
    }

    public ArrayList<BitSet> getPlayersCards() {
        return playersCards;
    }

    public BitSet getRemaining() {
        return remaining;
    }

    public int getTurn(){
        return cards.size();
    }

    public int[] getScore(){
        return score;
    }

    public ArrayList<Integer> getCards() {
        return cards;
    }

    public ArrayList<Integer> getPlayers() {
        return players;
    }

    public void print(){
        CardConverter cc = new CardConverter();
        System.out.println("Score: ");
        for(int i = 0; i < 4; i++){
            System.out.print(score[i] + " ");
        }
        for(int i = 0; i < cards.size(); i++){
            if(i % 4 == 0){
                System.out.println();
            }
            System.out.print("P" + players.get(i) + ": " + cc.translate(cards.get(i)) + " ");
        }
    }
}
