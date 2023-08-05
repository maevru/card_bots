import java.util.*;

public class SmartPlayerV4 {

    private final Random RNG = new Random(69);
    private final BitSet CLUBS = new BitSet();
    private final BitSet SPADES = new BitSet();
    private final BitSet DIAMONDS = new BitSet();
    private final BitSet HEARTS = new BitSet();
    private final BitSet NOHEARTS = new BitSet();

    // For getUsefulAsFirstHand
    private final BitSet USEFUL_CLUBS = new BitSet();
    private final BitSet USEFUL_SPADES = new BitSet();
    private final BitSet USEFUL_DIAMONDS = new BitSet();
    private final BitSet USEFUL_HEARTS = new BitSet();

    public SmartPlayerV4(){
        CLUBS.set(0,13);
        SPADES.set(13,26);
        DIAMONDS.set(26,39);
        HEARTS.set(39,52);
        NOHEARTS.set(0,39);
    }
    private long NODES;

    public static void main(String[] args) {
        SmartPlayerV4 playerV4 = new SmartPlayerV4();

        for(int i = 1; i <= 13; i++){
            System.out.println();
            System.out.println( i + " ROUND(S)");
            ArrayList<BitSet> players = playerV4.sortCards(i);
            System.out.println("Cards sorted.");

            long start = System.currentTimeMillis();
            playerV4.NODES = 0;
            BitSet remaining = new BitSet();
            int startPlayer = -1;
            for(BitSet player : players){
                remaining.or(player);
                if (player.get(0)){
                    startPlayer = players.indexOf(player);
                }
            }
            int[] penaltyPoints = new int[4];
            int[] optimalGame = playerV4.simulate(players, 0, startPlayer, new BitSet(), -1, -1, false, remaining, 0, penaltyPoints);
            long time = System.currentTimeMillis() - start;
            System.out.println("All games simulated in " + time + " ms.");
            System.out.println("Nodes: " + playerV4.NODES);
        }
    }

    public ArrayList<BitSet> sortCards(int numberOfRounds){
        ArrayList<BitSet> players = new ArrayList<>();
        for(int i = 0; i < 4; i++){
            players.add(new BitSet(52));
        }

        Stack<Integer> cards = new Stack<>();
        for(int i = 0; i < numberOfRounds; i++){
            for(int j = 0; j < 4; j++){
                cards.add((j * 13) + i);
            }
        }
        Collections.shuffle(cards, RNG);

        int player = 0;
        while(!cards.isEmpty()){
            players.get(player).set(cards.pop());
            player = (player + 1) % 4;
        }

        return players;
    }

    public int[] simulate(ArrayList<BitSet> players, int turn, int nextPlayer, BitSet symbol, int highestCard, int winner,
                          boolean heartsPlayed, BitSet remaining, int penaltyPoints, int[] score){
        NODES++;

        BitSet cards = new BitSet();
        cards.or(players.get(nextPlayer));

        if(turn % 4 == 0){
            // Start with card 0
            if(cards.get(0)){
                players.get(nextPlayer).clear(0);
                remaining.clear(0);
                return simulate(players, turn + 1, (nextPlayer + 1) % 4, getSymbolFromCard(0), 0, nextPlayer, false, remaining, 0, score);
            }

            // If no remaining hearts left stop computing
            BitSet remainingHearts = new BitSet();
            remainingHearts.or(remaining);
            remainingHearts.and(HEARTS);
            if(remainingHearts.isEmpty()){
                return score;
            }

            if(heartsPlayed){
                BitSet usefulCards = getUsefulAsFirstHand(cards, remaining);
                for(int i = usefulCards.nextSetBit(0); i != -1; i = usefulCards.nextSetBit(i + 1)){
                    players.get(nextPlayer).clear(i);
                    remaining.clear(i);
                    if(HEARTS.get(i)){
                        simulate(players, turn + 1, (nextPlayer + 1) % 4, getSymbolFromCard(i), i, nextPlayer, true, remaining, penaltyPoints + 1, score);
                    } else {
                        simulate(players, turn + 1, (nextPlayer + 1) % 4, getSymbolFromCard(i), i, nextPlayer, true, remaining, penaltyPoints, score);
                    }

                    players.get(nextPlayer).set(i);
                    remaining.set(i);
                }
            } else {
                BitSet playable = new BitSet();
                playable.or(cards);
                playable.and(NOHEARTS);
                if(playable.isEmpty()){
                    playable.or(cards);
                }
                BitSet usefulCards = getUsefulAsFirstHand(playable, remaining);
                for(int i = usefulCards.nextSetBit(0); i != -1; i = usefulCards.nextSetBit(i + 1)){
                    players.get(nextPlayer).clear(i);
                    remaining.clear(i);
                    if(HEARTS.get(i)){
                        simulate(players, turn + 1, (nextPlayer + 1) % 4, getSymbolFromCard(i), i, nextPlayer, false, remaining, penaltyPoints + 1, score);
                    } else {
                        simulate(players, turn + 1, (nextPlayer + 1) % 4, getSymbolFromCard(i), i, nextPlayer, false, remaining, penaltyPoints, score);
                    }
                    players.get(nextPlayer).set(i);
                    remaining.set(i);
                }
            }

        } else {
            BitSet playable = new BitSet();
            playable.or(cards);
            playable.and(symbol);
            if(playable.isEmpty()){
                playable.or(cards);
            }
            BitSet usefulCards = getUsefulCards(playable, remaining);
            for(int i = usefulCards.nextSetBit(0); i != -1; i = usefulCards.nextSetBit(i + 1)){
                players.get(nextPlayer).clear(i);
                remaining.clear(i);
                if(symbol.get(i) & i > highestCard){
                    if(turn % 4 == 3){
                        if(HEARTS.get(i)){
                            score[nextPlayer] += penaltyPoints + 1;
                        } else {
                            score[nextPlayer] += penaltyPoints;
                        }
                        simulate(players, turn + 1, nextPlayer, symbol, i, nextPlayer, HEARTS.get(i), remaining, 0, score);
                    } else {
                        if(HEARTS.get(i)){
                            simulate(players, turn + 1, (nextPlayer + 1) % 4, symbol, i, nextPlayer, HEARTS.get(i), remaining, penaltyPoints + 1, score);
                        } else {
                            simulate(players, turn + 1, (nextPlayer + 1) % 4, symbol, i, nextPlayer, HEARTS.get(i), remaining, penaltyPoints, score);
                        }
                    }
                } else {
                    if(turn % 4 == 3){
                        if(HEARTS.get(i)){
                            score[winner] += penaltyPoints + 1;
                        } else {
                            score[winner] += penaltyPoints;
                        }
                        simulate(players, turn + 1, winner, symbol, highestCard, winner, HEARTS.get(i), remaining, 0, score);
                    } else {
                        if(HEARTS.get(i)){
                            simulate(players, turn + 1, (nextPlayer + 1) % 4, symbol, highestCard, winner, HEARTS.get(i), remaining, penaltyPoints + 1, score);
                        } else {
                            simulate(players, turn + 1, (nextPlayer + 1) % 4, symbol, highestCard, winner, HEARTS.get(i), remaining, penaltyPoints, score);
                        }
                    }
                }
                players.get(nextPlayer).set(i);
                remaining.set(i);
            }
        }

        return score;
    }

    public BitSet getSymbolFromCard(int card){
        if (card < 13) {
            return CLUBS;
        } else if (card < 26) {
            return SPADES;
        } else if (card < 39) {
            return DIAMONDS;
        } else {
            return HEARTS;
        }
    }

    public BitSet getUsefulAsFirstHand(BitSet cards, BitSet remaining){
        BitSet usefulCards = new BitSet();
        usefulCards.or(cards);

        BitSet realRemaining = new BitSet();
        cards.flip(0,52);
        realRemaining.or(remaining);
        realRemaining.and(cards);
        cards.flip(0,52);

        USEFUL_CLUBS.or(CLUBS);
        USEFUL_SPADES.or(SPADES);
        USEFUL_DIAMONDS.or(DIAMONDS);
        USEFUL_HEARTS.or(HEARTS);

        USEFUL_CLUBS.and(realRemaining);
        USEFUL_SPADES.and(realRemaining);
        USEFUL_DIAMONDS.and(realRemaining);
        USEFUL_HEARTS.and(realRemaining);

        if(USEFUL_CLUBS.isEmpty()){
            usefulCards.clear(0,13);
        }
        if(USEFUL_SPADES.isEmpty()){
            usefulCards.clear(13,26);
        }
        if(USEFUL_DIAMONDS.isEmpty()){
            usefulCards.clear(26,39);
        }
        if(USEFUL_HEARTS.isEmpty()){
            usefulCards.clear(39,52);
        }

        if(usefulCards.isEmpty()) {
            usefulCards.set(cards.nextSetBit(0));
        }

        return getUsefulCards(usefulCards, remaining);
    }

    public BitSet getUsefulCards(BitSet cards, BitSet remaining){
        BitSet usefulCards = new BitSet();
        usefulCards.or(cards);

        for(int card = cards.nextSetBit(0); card != -1; card = cards.nextSetBit(card + 1)){
            if(card < 13){
                for(int j = card + 1; j < 13; j++){
                    if(remaining.get(j)){
                        if(cards.get(j)){
                            usefulCards.clear(card);
                        }
                        break;
                    }
                }
            } else if(card < 26){
                for(int j = card + 1; j < 26; j++){
                    if(remaining.get(j)){
                        if(cards.get(j)){
                            usefulCards.clear(card);
                        }
                        break;
                    }
                }
            } else if(card < 39){
                for(int j = card + 1; j < 39; j++){
                    if(remaining.get(j)){
                        if(cards.get(j)){
                            usefulCards.clear(card);
                        }
                        break;
                    }
                }
            } else {
                for(int j = card + 1; j < 52; j++){
                    if(remaining.get(j)){
                        if(cards.get(j)){
                            usefulCards.clear(card);
                        }
                        break;
                    }
                }
            }
        }

        return usefulCards;
    }
}
