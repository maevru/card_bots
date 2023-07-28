import java.util.BitSet;
import java.util.Random;

public class SmartPlayerV1 implements BlackQueenPlayer{

    private final String name;
    private BitSet cards;
    public static final Random RNG = new Random();
    public BitSet noPenaltyCards = new BitSet(52);

    public SmartPlayerV1(String name) {
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
        BitSet remaining = new BitSet(52);
        remaining.or(cards);
        remaining.flip(0,52);
        remaining.and(game.getRemaining());

        BitSet playableCards = new BitSet();
        playableCards.or(cards);

        if(game.isNewRound()){
            // If 0 he must start with it.
            if(playableCards.get(0)) {
                cards.clear(0);
                return 0;
            }
            // Hunt the queen if possible.
            if(remaining.get(23)){
                for(int i = 22; i >= 13; i--){
                    if(cards.get(i)){
                        cards.clear(i);
                        return i;
                    }
                }
            }

            if(game.isPlayedHearts()){
                // TODO
                // Play low heart if possible
                BitSet remainingHearts = new BitSet();
                remainingHearts.set(39, 52);
                remainingHearts.and(remaining);
                for(int i = 39; i < 47; i++){
                    if(cards.get(i)){
                        cards.clear(i);
                        return i;
                    }
                }

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
                //Throw away Queen of Spades or highest hearts
                if(cards.get(23)){
                    cards.clear(23);
                    return 23;
                }
                for(int i = 51; i >= 39; i--){
                    if(cards.get(i)){
                        cards.clear(i);
                        return i;
                    }
                }
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
