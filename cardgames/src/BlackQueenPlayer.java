import java.util.ArrayList;

public interface BlackQueenPlayer {

    ArrayList<String> getCards();

    void setCards(ArrayList<String> cards);

    String getName();

    boolean hasCard(String card);

    String playCard(String history, String round);
}
