import java.util.*;

public class BlackQueenSim {

    public static void main(String[] args) {
        BlackQueenSim blackQueenSim = new BlackQueenSim();
        long start = System.currentTimeMillis();
        blackQueenSim.bigTest(1000000);
        System.out.println("Time: " + (System.currentTimeMillis() - start));
    }

    private final ArrayList<Integer> cards = new ArrayList<>();

    public BlackQueenSim(){
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
        BlackQueenPlayer P1 = new RandomPlayer("P1");
        BlackQueenPlayer P2 = new RandomPlayer("P2");
        BlackQueenPlayer P3 = new RandomPlayer("P3");
        BlackQueenPlayer P4 = new RandomPlayer("P4");
        HashMap<String, Integer> score = new HashMap<>();


        for(int i = 0; i < numberOfGames; i++){
            BlackQueenGame game = playGameWith4Players(P1, P2, P3, P4);
            for(BlackQueenRound round : game.getRounds()){
                score.put(round.getWinner().getName(), score.getOrDefault(round.getWinner().getName(), 0) + round.getPenaltyPoints());
            }
            //game.printGame();
        }

        for(String player : score.keySet()){
            System.out.println(player + ": " + score.get(player));
        }

    }

    /**
     * Simulate a game with 4 RandomPlayers.
     */
    public BlackQueenGame playGameWith4Players(BlackQueenPlayer P1, BlackQueenPlayer P2, BlackQueenPlayer P3, BlackQueenPlayer P4){
        ArrayList<BlackQueenPlayer> players = new ArrayList<>();
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
        BlackQueenPlayer startPlayer = null;
        for (BlackQueenPlayer player : players){
            if(player.getCards().get(0)){
                startPlayer = player;
                break;
            }
        }

        BlackQueenGame game = new BlackQueenGame();
        BlackQueenPlayer nextPlayer = startPlayer;
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
