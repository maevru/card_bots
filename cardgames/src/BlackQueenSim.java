import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class BlackQueenSim {

    public static void main(String[] args) {
        BlackQueenSim blackQueenSim = new BlackQueenSim();
    }

    private final ArrayList<String> cards = new ArrayList<>();

    public BlackQueenSim(){
        cards.addAll(Arrays.asList(
                "C1", "C2", "C3", "C4", "C5", "C6", "C7", "C8", "C9", "C10", "C11", "C12", "C13",
                "S1", "S2", "S3", "S4", "S5", "S6", "S7", "S8", "S9", "S10", "S11", "S12", "S13",
                "D1", "D2", "D3", "D4", "D5", "D6", "D7", "D8", "D9", "D10", "D11", "D12", "D13",
                "H1", "H2", "H3", "H4", "H5", "H6", "H7", "H8", "H9", "H10", "H11", "H12", "H13"));
    }

    public void bigTest(int numberOfGames){
        BlackQueenPlayer P1 = new RandomPlayer("P1");
        BlackQueenPlayer P2 = new RandomPlayer("P2");
        BlackQueenPlayer P3 = new RandomPlayer("P3");
        BlackQueenPlayer P4 = new RandomPlayer("P4");
    }

    /**
     * Simulate a game with 4 RandomPlayers.
     */
    public String playGameWith4Players(BlackQueenPlayer P1, BlackQueenPlayer P2, BlackQueenPlayer P3, BlackQueenPlayer P4){
        ArrayList<BlackQueenPlayer> players = new ArrayList<>();
        players.add(P1);
        players.add(P2);
        players.add(P3);
        players.add(P4);

        ArrayList<String> shuffledCards = new ArrayList<>(cards);
        Collections.shuffle(shuffledCards);

        int counter = -1;
        for(int i = 1; i <= 4; i++){
            ArrayList<String> cards = new ArrayList<>();
            for(int j = 0; j < 13; j++){
                counter++;
                cards.add(shuffledCards.get(counter));
            }
            players.get(players.size() - 1).setCards(cards);
        }

        // Print players with cards
        for(BlackQueenPlayer player : players){
            System.out.println(player.getName());
            for(String card : player.getCards()){
                System.out.print(card + " ");
            }
            System.out.println("");
        }

        // Find starting player
        BlackQueenPlayer startPlayer = null;
        for (BlackQueenPlayer player : players){
            if(player.hasCard("C1")){
                startPlayer = player;
                break;
            }
        }

        String history = "N";
        String round;
        String playedCard;
        BlackQueenPlayer nextPlayer = startPlayer;
        for(int i = 0; i < 13; i++){
            round = "";
            for(int j = 0; j < 4; j++){
                playedCard = nextPlayer.playCard(history, round);
                round += nextPlayer.getName();
                round += " ";
                round += playedCard;
                round += " ";
                nextPlayer = players.get((players.indexOf(nextPlayer) + 1) % 4);
            }

            history += " ";
            history += round;
            if(history.charAt(0) == 'N'){
                for(String str : round.split(" ")){
                    if(str.equals("S11") || str.charAt(0) == 'H'){
                        history = "Y" + history;
                    }
                }
            }

            String[] splitRound = round.split(" ");
            String nextPlayerName = splitRound[0];
            char type = splitRound[1].charAt(0);
            int number = Integer.parseInt(splitRound[1].substring(1));
            if(splitRound[3].charAt(0) == type && Integer.parseInt(splitRound[3].substring(1)) > number){
                number = Integer.parseInt(splitRound[3].substring(1));
                nextPlayerName = splitRound[2];
            }
            if(splitRound[5].charAt(0) == type && Integer.parseInt(splitRound[5].substring(1)) > number){
                number = Integer.parseInt(splitRound[5].substring(1));
                nextPlayerName = splitRound[4];
            }
            if(splitRound[7].charAt(0) == type && Integer.parseInt(splitRound[7].substring(1)) > number){
                nextPlayerName = splitRound[6];
            }

            for(BlackQueenPlayer player : players){
                if(player.getName().equals(nextPlayerName)){
                    nextPlayer = player;
                }
            }

            // Print round and players and cards
            System.out.println(round);
            for(BlackQueenPlayer player : players){
                System.out.println(player.getName());
                for(String card : player.getCards()){
                    System.out.print(card + " ");
                }
                System.out.println("");
            }
        }

        return history;
    }
}
