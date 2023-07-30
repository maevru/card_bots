import java.util.*;

public class SmartPlayerV3 implements BlackLadyPlayer{

    private static final int NUMBER_OF_GAMES = 100;
    private static final int NUMBER_OF_SHUFFLES = 10;
    private final String name;
    private BitSet cards;
    private static final Random RNG = new Random();

    public SmartPlayerV3(String name){
        this.name = name;
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
        BitSet playableCards = new BitSet(52);
        BitSet usefulCards = new BitSet(52);
        playableCards.or(cards);

        if (game.isNewRound()) {
            if (playableCards.get(0)) {
                cards.clear(0);
                return 0;
            } else if (!game.isPlayedHearts()) {
                playableCards.clear(39, 52);
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

        if(playableCards.cardinality() == 1){
            cards.clear(playableCards.nextSetBit(0));
            return playableCards.nextSetBit(0);
        }

        // FIND THE SET OF USEFUL AND PLAYABLE CARDS
        BitSet realRemaining = new BitSet();
        realRemaining.or(game.getRemaining());
        cards.flip(0,52);
        realRemaining.and(cards);
        cards.flip(0,52);
        usefulCards.or(playableCards);
        // (Don't play a card when no one will follow)
        if (game.isNewRound()){
            BitSet clubs = new BitSet(52);
            BitSet spades = new BitSet(52);
            BitSet diamonds = new BitSet(52);
            BitSet hearts = new BitSet(52);
            clubs.set(0, 13);
            spades.set(13, 26);
            diamonds.set(26, 39);
            hearts.set(39, 52);
            clubs.and(realRemaining);
            spades.and(realRemaining);
            diamonds.and(realRemaining);
            hearts.and(realRemaining);

            if(clubs.isEmpty()){
                usefulCards.clear(0,13);
            }
            if(spades.isEmpty()){
                usefulCards.clear(13,26);
            }
            if(diamonds.isEmpty()){
                usefulCards.clear(26,39);
            }
            if(hearts.isEmpty()){
                usefulCards.clear(39,52);
            }

            if(usefulCards.isEmpty()){
                usefulCards.or(playableCards);
            } else if(usefulCards.cardinality() == 1){
                cards.clear(usefulCards.nextSetBit(0));
                return usefulCards.nextSetBit(0);
            }
        }

        /*
        // If all cards between 2 cards have been played there is no need to check them both.
        for(int card = usefulCards.nextSetBit(0); card != -1; card = usefulCards.nextSetBit(card + 1)){
            if(card < 13){
                for(int j = card + 1; j < 13; j++){
                    if(cards.get(j)){
                        usefulCards.clear(card);
                        break;
                    }
                    if(realRemaining.get(j)){
                        break;
                    }
                }
            } else if(card < 26){
                for(int j = card + 1; j < 26; j++){
                    if(cards.get(j)){
                        usefulCards.clear(card);
                        break;
                    }
                    if(realRemaining.get(j)){
                        break;
                    }
                }
            } else if(card < 39){
                for(int j = card + 1; j < 39; j++){
                    if(cards.get(j)){
                        usefulCards.clear(card);
                        break;
                    }
                    if(realRemaining.get(j)){
                        break;
                    }
                }
            } else {
                for(int j = card + 1; j < 52; j++){
                    if(cards.get(j)){
                        usefulCards.clear(card);
                        break;
                    }
                    if(realRemaining.get(j)){
                        break;
                    }
                }
            }
        }
         */

        HashMap<Integer, Integer> penaltyPoints = new HashMap<>();
        for(int shuffles = 0; shuffles < NUMBER_OF_SHUFFLES; shuffles++){
            // SHUFFLE CARDS FOR OPPONENTS
            ArrayList<BitSet> players = giveCards(game.getRemaining());

            // RUN GAMES
            for(int card = usefulCards.nextSetBit(0); card != -1; card = usefulCards.nextSetBit(card + 1)){

                for(int counter = 0; counter < NUMBER_OF_GAMES; counter++){
                    int points = simulateRemainingGame(game, card, cards, players);
                    penaltyPoints.put(card, penaltyPoints.getOrDefault(card, 0) + points);
                }
            }
        }

        int leastPoints = Integer.MAX_VALUE;
        int bestCard = -1;
        for(int card : penaltyPoints.keySet()){
            if(penaltyPoints.get(card) < leastPoints){
                leastPoints = penaltyPoints.get(card);
                bestCard = card;
            }
        }

        cards.clear(bestCard);
        return bestCard;
    }

    public ArrayList<BitSet> giveCards(BitSet remaining){
        ArrayList<BitSet> players = new ArrayList<>();
        players.add(new BitSet(52));
        players.add(new BitSet(52));
        players.add(new BitSet(52));

        BitSet realRemaining = new BitSet(52);
        realRemaining.or(remaining);
        cards.flip(0,52);
        realRemaining.and(cards);
        cards.flip(0,52);
        int number = (realRemaining.cardinality() / 3);
        if(realRemaining.cardinality() % 3 > 0){
            number++;
        }
        for(int i = 0; i < number; i++){
            int card = -1;
            for(int j = 0; j <= RNG.nextInt(realRemaining.cardinality()); j++){
                card = realRemaining.nextSetBit(card + 1);
            }
            players.get(0).set(card);
            realRemaining.clear(card);
        }
        number = (realRemaining.cardinality() / 2) + (realRemaining.cardinality() % 2);
        for(int i = 0; i < number; i++){
            int card = -1;
            for(int j = 0; j <= RNG.nextInt(realRemaining.cardinality()); j++){
                card = realRemaining.nextSetBit(card + 1);
            }
            players.get(1).set(card);
            realRemaining.clear(card);
        }

        players.get(2).or(realRemaining);

        return players;
    }

    public int simulateRemainingGame(BlackLadyGame game, int tryCard, BitSet mainPlayer, ArrayList<BitSet> otherPlayers){
        ArrayList<BitSet> players = new ArrayList<>();
        BitSet P0 = new BitSet(52);
        P0.or(mainPlayer);
        players.add(P0);
        BitSet P1 = new BitSet(52);
        P1.or(otherPlayers.get(0));
        players.add(P1);
        BitSet P2 = new BitSet(52);
        P2.or(otherPlayers.get(1));
        players.add(P2);
        BitSet P3 = new BitSet(52);
        P3.or(otherPlayers.get(2));
        players.add(P3);

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
                players.get(0).clear(card);
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
                nextPlayer = (nextPlayer + 1) % 4;
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
        BitSet playableCards = new BitSet(52);
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
        BitSet playableCards = new BitSet(52);
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

