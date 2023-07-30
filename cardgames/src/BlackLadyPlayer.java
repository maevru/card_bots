import java.util.BitSet;

public interface BlackLadyPlayer {

    BitSet getCards();

    void setCards(BitSet cards);

    String getName();

    int playCard(BlackLadyGame game);

}
