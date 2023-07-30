import java.util.*;

/**
 * This class simulates the card game Black Lady.
 * Rules:
 * The game is played with 4 players and the player with the 2 of clubs starts by playing this cards.
 * Then players must follow suit if they can; otherwise may discard any card.
 * A trick is won by the highest card of the led suit and the winner of the trick leads to the next.
 * The goal is to avoid capturing hearts (each one penalty point) and the queen of spades (13 penalty points).
 * The first trick no hearts or the queen of spades can be played.
 * No hearts can be played until a heart has fallen.
 */
public class BlackLadySim {

    public static void main(String[] args) {
        BlackLadySim blackLadySim = new BlackLadySim();
        long start = System.currentTimeMillis();
        blackLadySim.bigTest(1);
        System.out.println("Time: " + (System.currentTimeMillis() - start));
    }

    private final ArrayList<Integer> cards = new ArrayList<>();

    public BlackLadySim(){
        //0-12 for Clubs 2 to ace
        //13-25 for Spades 2 to ace
        //26-38 for Diamonds 2 to ace
        //39-51 for Hearts 2 to ace
        //Spades Queen is 23
        for(int i = 0; i < 52; i++){
            cards.add(i);
        }
    }

    public void bigTest(int numberOfGames){
        BlackLadyPlayer P1 = new SmartPlayerV2("P1");
        BlackLadyPlayer P2 = new RandomPlayer("P2");
        BlackLadyPlayer P3 = new RandomPlayer("P3");
        BlackLadyPlayer P4 = new RandomPlayer("P4");
        HashMap<String, Integer> score = new HashMap<>();


        for(int i = 0; i < numberOfGames; i++){
            BlackLadyGame game = playGameWith4Players(P1, P2, P3, P4);
            for(BlackLadyRound round : game.getRounds()){
                score.put(round.getWinner().getName(), score.getOrDefault(round.getWinner().getName(), 0) + round.getPenaltyPoints());
            }
            //game.printGame();
        }

        for(String player : score.keySet()){
            System.out.println(player + ": " + score.get(player));
        }

    }

    /**
     * Simulate a game with 4 players.
     */
    public BlackLadyGame playGameWith4Players(BlackLadyPlayer P1, BlackLadyPlayer P2, BlackLadyPlayer P3, BlackLadyPlayer P4){
        ArrayList<BlackLadyPlayer> players = new ArrayList<>();
        players.add(P1);
        players.add(P2);
        players.add(P3);
        players.add(P4);

        ArrayList<Integer> shuffledCards = new ArrayList<>(cards);
        Collections.shuffle(shuffledCards);

        int counter = -1;
        for(int i = 0; i < 4; i++){
            BitSet cards = new BitSet(52);
            for(int j = 0; j < 13; j++){
                counter++;
                cards.set(shuffledCards.get(counter));
            }
            players.get(i).setCards(cards);
        }

        // Print players with cards
        /*
        CardConverter cc = new CardConverter();
        for(BlackQueenPlayerV2 player : players) {
            for (int i = player.getCards().nextSetBit(0); i != -1; i = player.getCards().nextSetBit(i + 1)) {
                System.out.print(cc.translate(i) + ", ");
            }
            System.out.println();
        }
         */


        // Find starting player
        BlackLadyPlayer startPlayer = null;
        for (BlackLadyPlayer player : players){
            if(player.getCards().get(0)){
                startPlayer = player;
                break;
            }
        }

        BlackLadyGame game = new BlackLadyGame();
        BlackLadyPlayer nextPlayer = startPlayer;
        for(int i = 0; i < 13; i++){
            game.newRound();
            for(int j = 0; j < 4; j++){
                game.playTurn(nextPlayer, nextPlayer.playCard(game));
                nextPlayer = players.get((players.indexOf(nextPlayer) + 1) % 4);
            }
            nextPlayer = game.getLastWinner();
        }
        return game;
    }
}
