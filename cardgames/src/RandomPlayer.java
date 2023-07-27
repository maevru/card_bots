import java.util.BitSet;
import java.util.Random;

public class RandomPlayer implements BlackQueenPlayer {

    private final String name;
    private BitSet cards;
    public static final Random RNG = new Random();
    public BitSet noPenaltyCards = new BitSet(52);

    public RandomPlayer(String name) {
        this.name = name;
        noPenaltyCards.set(0, 52);
        noPenaltyCards.clear(23);
        noPenaltyCards.clear(39, 52);
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

        if(game.isNewRound()){
            if(playableCards.get(0)){
                cards.clear(0);
                return 0;
            } else if(game.isPenaltyPlayed()){
                int random = RNG.nextInt(playableCards.cardinality());
                int card = -1;
                for (int i = 0; i <= random; i++) {
                    card = playableCards.nextSetBit(card + 1);
                }
                cards.clear(card);
                return card;
            } else {
                playableCards.and(noPenaltyCards);
                if(playableCards.isEmpty()){
                    int random = RNG.nextInt(cards.cardinality());
                    int card = -1;
                    for (int i = 0; i <= random; i++) {
                        card = cards.nextSetBit(card + 1);
                    }
                    cards.clear(card);
                    return card;
                } else {
                    int random = RNG.nextInt(playableCards.cardinality());
                    int card = -1;
                    for (int i = 0; i <= random; i++) {
                        card = playableCards.nextSetBit(card + 1);
                    }
                    cards.clear(card);
                    return card;
                }
            }
        } else {
            playableCards.and(game.getCurrentSymbol());
            if(playableCards.isEmpty()){
                int random = RNG.nextInt(cards.cardinality());
                int card = -1;
                for (int i = 0; i <= random; i++) {
                    card = cards.nextSetBit(card + 1);
                }
                cards.clear(card);
                return card;
            } else {
                int random = RNG.nextInt(playableCards.cardinality());
                int card = -1;
                for (int i = 0; i <= random; i++) {
                    card = playableCards.nextSetBit(card + 1);
                }
                cards.clear(card);
                return card;
            }
        }
    }
}
