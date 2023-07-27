import java.util.BitSet;

public interface BlackQueenPlayer {

    BitSet getCards();

    void setCards(BitSet cards);

    String getName();

    int playCard(BlackQueenGame game);

}
