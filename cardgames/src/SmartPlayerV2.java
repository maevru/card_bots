import java.util.BitSet;
import java.util.Random;

public class SmartPlayerV2 implements BlackQueenPlayer{

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

        return 0;
    }
}
