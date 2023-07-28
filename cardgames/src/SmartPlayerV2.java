import java.util.*;

public class SmartPlayerV2 implements BlackQueenPlayer{

    public static void main(String[] args) {

    }

    private final String name;
    private BitSet cards;
    private static final Random RNG = new Random();
    private final BitSet noHearts = new BitSet(52);

    public SmartPlayerV2(String name){
        this.name = name;
        noHearts.set(0, 52);
        noHearts.clear(39, 52);
    }

    public BitSet getCards() {
        return cards;
    }

    public void setCards(BitSet cards) {
        this.cards = cards;
    }

    public String getName() {
        return name;
    }

    public int playCard(BlackQueenGame game) {
        BitSet playableCards = new BitSet();
        playableCards.or(cards);

        if (game.isNewRound()) {
            if (playableCards.get(0)) {
                cards.clear(0);
                return 0;
            } else if (!game.isPlayedHearts()) {
                playableCards.and(noHearts);
                if (playableCards.isEmpty()) {
                    playableCards.or(cards);
                }
            } else {
                playableCards.and(game.getCurrentSymbol());
                if (playableCards.isEmpty()) {
                    playableCards.or(cards);
                }
            }
        }

        // SHUFFLE CARDS
        ArrayList<BitSet> players = new ArrayList<>();
        players.add(new BitSet(52));
        players.add(new BitSet(52));
        players.add(new BitSet(52));
        Stack<Integer> remaining = new Stack<>();
        for(int i = game.getRemaining().nextSetBit(0); i != -1; i = game.getRemaining().nextSetBit(i + 1)){
            if(!cards.get(i)){
                remaining.add(i);
            }
        }
        Collections.shuffle(remaining);

        int player = 0;
        while(!remaining.isEmpty()){
            players.get(player).set(remaining.pop());
            player = (player + 1) % 3;
        }

        // RUN GAMES


        return 0;
    }

    public ArrayList<BitSet> giveCards(int round, BitSet remaining){

        return null;
    }
}
