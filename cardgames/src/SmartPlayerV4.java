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
                System.out.println(player);
                remaining.or(player);
                if (player.get(0)){
                    startPlayer = players.indexOf(player);
                }
            }
            SmartPlayerV4Game game = new SmartPlayerV4Game(players, remaining);
            SmartPlayerV4Game optimalGame = playerV4.simulate(startPlayer, new BitSet(), -1, -1, false, 0, game);

            optimalGame.print();

            long time = System.currentTimeMillis() - start;
            System.out.println();
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

    public SmartPlayerV4Game simulate(int nextPlayer, BitSet symbol, int highestCard, int winner, boolean heartsPlayed, int penaltyPoints, SmartPlayerV4Game game){
        NODES++;

        BitSet cards = new BitSet();
        cards.or(game.getPlayersCards().get(nextPlayer));

        int bestScore = Integer.MAX_VALUE;
        SmartPlayerV4Game simulatedGame;
        SmartPlayerV4Game bestGame = null;

        if(game.getTurn() % 4 == 0){
            // Start with card 0
            if(cards.get(0)){
                game.makeMove(nextPlayer, 0);
                return simulate((nextPlayer + 1) % 4, getSymbolFromCard(0), 0, nextPlayer, false, 0, game);
            }

            // If no remaining hearts left stop computing
            BitSet remainingHearts = new BitSet();
            remainingHearts.or(game.getRemaining());
            remainingHearts.and(HEARTS);
            if(remainingHearts.isEmpty()){
                return new SmartPlayerV4Game(game);
            }

            if(heartsPlayed){
                BitSet usefulCards = getUsefulAsFirstHand(cards, game.getRemaining());
                for(int i = usefulCards.nextSetBit(0); i != -1; i = usefulCards.nextSetBit(i + 1)){

                    game.makeMove(nextPlayer, i);
                    if(HEARTS.get(i)){
                        simulatedGame = simulate( (nextPlayer + 1) % 4, getSymbolFromCard(i), i, nextPlayer, true, penaltyPoints + 1, game);
                    } else {
                        simulatedGame = simulate((nextPlayer + 1) % 4, getSymbolFromCard(i), i, nextPlayer, true, penaltyPoints, game);
                    }
                    game.undoMove(nextPlayer, i);

                    if(simulatedGame.getScore()[nextPlayer] < bestScore){
                        bestScore = simulatedGame.getScore()[nextPlayer];
                        bestGame = new SmartPlayerV4Game(simulatedGame);
                        if(bestScore == 0){
                            return bestGame;
                        }
                    }
                }

            } else {
                BitSet playable = new BitSet();
                playable.or(cards);
                playable.and(NOHEARTS);
                if(playable.isEmpty()){
                    playable.or(cards);
                }
                BitSet usefulCards = getUsefulAsFirstHand(playable, game.getRemaining());
                for(int i = usefulCards.nextSetBit(0); i != -1; i = usefulCards.nextSetBit(i + 1)){
                    game.makeMove(nextPlayer, i);
                    if(HEARTS.get(i)){
                        simulatedGame = simulate((nextPlayer + 1) % 4, getSymbolFromCard(i), i, nextPlayer, false, penaltyPoints + 1, game);
                    } else {
                        simulatedGame = simulate((nextPlayer + 1) % 4, getSymbolFromCard(i), i, nextPlayer, false, penaltyPoints, game);
                    }
                    game.undoMove(nextPlayer, i);
                    if(simulatedGame.getScore()[nextPlayer] < bestScore){
                        bestScore = simulatedGame.getScore()[nextPlayer];
                        bestGame = new SmartPlayerV4Game(simulatedGame);
                        if(bestScore == 0){
                            return bestGame;
                        }
                    }
                }
            }

        } else {
            BitSet usefulCards = getUsefulCardsAsNotFirstHand(cards, game.getRemaining(), symbol, game.getTurn() < 4, highestCard);
            for(int i = usefulCards.nextSetBit(0); i != -1; i = usefulCards.nextSetBit(i + 1)){
                if(symbol.get(i) & i > highestCard) {
                    winner = nextPlayer;
                    highestCard = i;
                }

                if(game.getTurn() % 4 == 3){
                    int totalPenaltyPoints = penaltyPoints;
                    if(HEARTS.get(i)){
                        totalPenaltyPoints++;
                    }
                    game.makeMove(nextPlayer, i);
                    game.addScore(winner, totalPenaltyPoints);
                    simulatedGame = simulate(winner, symbol, highestCard, winner, HEARTS.get(i), 0, game);
                    game.removeScore(winner, totalPenaltyPoints);
                } else {
                    game.makeMove(nextPlayer, i);
                    if(HEARTS.get(i)){
                        simulatedGame = simulate((nextPlayer + 1) % 4, symbol, highestCard, winner, HEARTS.get(i),penaltyPoints + 1, game);
                    } else {
                        simulatedGame = simulate((nextPlayer + 1) % 4, symbol, highestCard, winner, HEARTS.get(i), penaltyPoints, game);
                    }
                }

                game.undoMove(nextPlayer, i);

                if(simulatedGame.getScore()[nextPlayer] < bestScore){
                    bestScore = simulatedGame.getScore()[nextPlayer];
                    bestGame = new SmartPlayerV4Game(simulatedGame);
                    if(bestScore == 0){
                        return bestGame;
                    }
                }
            }
        }

        return bestGame;
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

    /**
     * Removes the lowest card of 2 when no remaining cards between the 2 are left in the game.
     */
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

    /**
     * If player can not follow he plays the highest of any symbol.
     * When he can follow we use getUsefulCards.
     */
    public BitSet getUsefulCardsAsNotFirstHand(BitSet cards, BitSet remaining, BitSet symbol, boolean isFirstRound, int highest){
        BitSet playable = new BitSet(52);

        if(isFirstRound){
            for (int i = 12; i >= 0; i--){
                if(cards.get(i)){
                    playable.set(i);
                    return playable;
                }
            }
        }

        playable.or(cards);
        playable.and(symbol);

        if(playable.isEmpty()){
            for (int i = 12; i >= 0; i--){
                if(cards.get(i)){
                    playable.set(i);
                    break;
                }
            }
            for (int i = 25; i >= 13; i--){
                if(cards.get(i)){
                    playable.set(i);
                    break;
                }
            }
            for (int i = 38; i >= 26; i--){
                if(cards.get(i)){
                    playable.set(i);
                    break;
                }
            }
            for (int i = 51; i >= 39; i--){
                if(cards.get(i)){
                    playable.set(i);
                    break;
                }
            }
            return playable;

        } else {
            playable.clear(symbol.nextSetBit(0), highest);
            for(int i = highest - 1; i >= symbol.nextSetBit(0); i--){
                if(cards.get(i)){
                    playable.set(i);
                    break;
                }
            }
            return  getUsefulCards(playable, remaining);
        }
    }
}
