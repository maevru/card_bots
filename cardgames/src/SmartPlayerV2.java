import java.util.*;

public class SmartPlayerV2 implements BlackLadyPlayer {

    public static void main(String[] args) {

    }

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

    public int playCard(BlackLadyGame game) {
        // FIND THE SET OF PLAYABLE CARDS
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
            }
        } else {
            playableCards.and(game.getCurrentSymbol());
            if (playableCards.isEmpty()) {
                playableCards.or(cards);
            }
        }

        // SHUFFLE CARDS FOR OPPONENTS
        ArrayList<BitSet> players = new ArrayList<>();
        players.add(new BitSet(52));
        players.add(new BitSet(52));
        players.add(new BitSet(52));
        Stack<Integer> remaining = new Stack<>();
        for(int i = game.getRemaining().nextSetBit(0); i != -1; i = game.getRemaining().nextSetBit(i + 1)){
            if(!cards.get(i)){
                remaining.add(i);
            }
        }
        Collections.shuffle(remaining);

        int player = 0;
        while(!remaining.isEmpty()){
            players.get(player).set(remaining.pop());
            player = (player + 1) % 3;
        }

        // RUN GAMES
        for(int i = playableCards.nextSetBit(0); i != -1; i = playableCards.nextSetBit(i + 1)){
            //TODO

        }

        return 0;
    }

    public ArrayList<BitSet> giveCards(int round, BitSet remaining){

        return null;
    }

    public int simulateRemainingGame(BlackLadyGame game, int tryCard, BitSet mainPlayer, ArrayList<BitSet> otherPlayers){
        ArrayList<BitSet> players = new ArrayList<>();
        BitSet P0 = new BitSet();
        P0.or(mainPlayer);
        players.add(P0);
        players.addAll(otherPlayers);

        boolean playedHearts = game.isPlayedHearts();
        int penaltyPoints = 0;
        int nextPlayer;


        // FINISH LAST ROUND
        int[] lastRound = game.getCardsFromLastRound();
        ArrayList<Integer> thisRound = new ArrayList<>();
        BitSet symbol = new BitSet(52);
        int card;
        int winner = -1;
        int highestCard;
        switch (game.getTurnInRound()) {
            case 0 -> {
                // PLAYER 0
                // Select card
                card = tryCard;
                players.get(0).clear(card);
                // Update remaining information
                symbol.or(getSymbolFromCard(card));
                winner = 0;
                highestCard = card;
                thisRound.add(card);

                // PLAYER 1-3
                for (int i = 1; i < 4; i++) {
                    // Select card
                    card = selectRandomPlayableCard(players.get(i), symbol);
                    players.get(i).clear(card);
                    // Update remaining information
                    if (symbol.get(card) && card > highestCard) {
                        winner = i;
                        highestCard = card;
                    }
                    thisRound.add(card);
                }
            }
            case 1 -> {
                // PLAYER 3
                // Select card
                card = lastRound[0];
                // Update remaining information
                symbol.or(getSymbolFromCard(card));
                winner = 3;
                highestCard = card;
                thisRound.add(card);

                // PLAYER 0
                // Select card
                card = tryCard;
                players.get(0).clear(card);
                // Update remaining information
                if (symbol.get(card) && card > highestCard) {
                    winner = 0;
                    highestCard = card;
                }
                thisRound.add(card);

                // PLAYER 1-2
                for (int i = 1; i < 3; i++) {
                    // Select card
                    card = selectRandomPlayableCard(players.get(i), symbol);
                    players.get(i).clear(card);
                    // Update remaining information
                    if (symbol.get(card) && card > highestCard) {
                        winner = i;
                        highestCard = card;
                    }
                    thisRound.add(card);
                }
            }
            case 2 -> {
                // PLAYER 2
                // Select card
                card = lastRound[0];
                // Update remaining information
                symbol.or(getSymbolFromCard(card));
                winner = 2;
                highestCard = card;
                thisRound.add(card);

                // PLAYER 3
                // Select card
                card = lastRound[1];
                // Update remaining information
                if (symbol.get(card) && card > highestCard) {
                    winner = 3;
                    highestCard = card;
                }
                thisRound.add(card);

                // PLAYER 0
                // Select card
                card = tryCard;
                players.get(0).clear(card);
                // Update remaining information
                if (symbol.get(card) && card > highestCard) {
                    winner = 0;
                    highestCard = card;
                }
                thisRound.add(card);

                // PLAYER 1
                // Select card
                card = selectRandomPlayableCard(players.get(1), symbol);
                players.get(1).clear(card);
                // Update remaining information
                if (symbol.get(card) && card > highestCard) {
                    winner = 1;
                    highestCard = card;
                }
                thisRound.add(card);
            }
            case 3 -> {
                // PLAYER 1
                // Select card
                card = lastRound[0];
                // Update remaining information
                symbol.or(getSymbolFromCard(card));
                winner = 1;
                highestCard = card;
                thisRound.add(card);

                // PLAYER 2
                // Select card
                card = lastRound[1];
                // Update remaining information
                if (symbol.get(card) && card > highestCard) {
                    winner = 2;
                    highestCard = card;
                }
                thisRound.add(card);

                // PLAYER 3
                // Select card
                card = lastRound[2];
                // Update remaining information
                if (symbol.get(card) && card > highestCard) {
                    winner = 3;
                    highestCard = card;
                }
                thisRound.add(card);

                // PLAYER 0
                // Select card
                card = tryCard;
                // Update remaining information
                if (symbol.get(card) && card > highestCard) {
                    winner = 0;
                    highestCard = card;
                }
                thisRound.add(card);
            }
        }

        for(int playedCard : thisRound){
            if(winner == 0){
                if (playedCard == 23) {
                    penaltyPoints += 13;
                } else if (playedCard >= 39) {
                    penaltyPoints += 1;
                    playedHearts = true;
                }
            } else{
                if (playedCard >= 39) {
                    playedHearts = true;
                }
            }
        }

        // PLAY REMAINING ROUNDS
        int numberOfRemainingRounds = players.get(0).cardinality();
        for(int i = 0; i < numberOfRemainingRounds; i++){
            thisRound.clear();
            nextPlayer = winner;
            // Select card
            card = selectRandomPlayableCardAsFirstHand(players.get(nextPlayer), playedHearts);
            players.get(nextPlayer).clear(card);
            // Update remaining information
            symbol.or(getSymbolFromCard(card));
            highestCard = card;
            thisRound.add(card);

            for(int j = 0; j < 3; j++){
                nextPlayer = (nextPlayer + 1) / 4;
                // Select card
                card = selectRandomPlayableCard(players.get(nextPlayer), symbol);
                players.get(nextPlayer).clear(card);
                // Update remaining information
                if (symbol.get(card) && card > highestCard) {
                    winner = nextPlayer;
                    highestCard = card;
                }
                thisRound.add(card);
            }

            for(int playedCard : thisRound){
                if(winner == 0){
                    if (playedCard == 23) {
                        penaltyPoints += 13;
                    } else if (playedCard >= 39) {
                        penaltyPoints += 1;
                        playedHearts = true;
                    }
                } else {
                    if (playedCard >= 39) {
                        playedHearts = true;
                    }
                }
            }
        }

        return penaltyPoints;
    }

    public int selectRandomPlayableCard(BitSet cards, BitSet symbol){
        // Find playable cards
        BitSet playableCards = new BitSet();
        playableCards.or(cards);
        playableCards.and(symbol);
        if (playableCards.isEmpty()) {
            playableCards.or(cards);
        }
        // Select a random card
        int random = RNG.nextInt(playableCards.cardinality());
        int card = -1;
        for (int j = 0; j <= random; j++) {
            card = playableCards.nextSetBit(card + 1);
        }
        return card;
    }

    public int selectRandomPlayableCardAsFirstHand(BitSet cards, boolean playedHearts){
        // Find playable cards
        BitSet playableCards = new BitSet();
        playableCards.or(cards);
        if(!playedHearts){
            playableCards.clear(39,52);
            if (playableCards.isEmpty()) {
                playableCards.or(cards);
            }
        }
        // Select a random card
        int random = RNG.nextInt(playableCards.cardinality());
        int card = -1;
        for (int j = 0; j <= random; j++) {
            card = playableCards.nextSetBit(card + 1);
        }
        return card;
    }

    public BitSet getSymbolFromCard(int card){
        BitSet symbol = new BitSet(52);
        if (card < 13) {
            symbol.set(0, 13);
        } else if (card < 26) {
            symbol.set(13, 26);
        } else if (card < 39) {
            symbol.set(26, 39);
        } else {
            symbol.set(39, 52);
        }
        return symbol;
    }
}
